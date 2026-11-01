package com.example.sleeptheory01

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MusicService : Service() {

    interface Listener {
        fun onPlaybackChanged()
        fun onTimerTick(remainingMs: Long) // -1 = sin temporizador
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = LocalBinder()
    private var player: MediaPlayer? = null
    private var sleepTimer: CountDownTimer? = null
    private var started = false
    private var lastNotifiedMinute = -1L

    var listener: Listener? = null
    var currentIndex = -1
        private set
    var shuffleOn = false
        private set
    var timerRemainingMs = -1L
        private set

    val hasSong: Boolean get() = player != null
    val isPlaying: Boolean get() = player?.isPlaying == true

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_NEXT -> playNext()
            ACTION_PREVIOUS -> playPrevious()
            ACTION_STOP -> stopPlayback()
        }
        return START_NOT_STICKY
    }

    // ---------- Reproducción ----------

    fun play(index: Int) {
        val sounds = SoundLibrary.sounds
        if (index !in sounds.indices) return

        if (!started) {
            ContextCompat.startForegroundService(this, Intent(this, MusicService::class.java))
            started = true
        }

        releasePlayer()
        val mp = MediaPlayer.create(this, sounds[index].resId)
        if (mp == null) {
            stopPlayback()
            return
        }
        mp.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mp.setOnCompletionListener { playNext() }
        mp.start()
        player = mp
        currentIndex = index

        ServiceCompat.startForeground(
            this, NOTIFICATION_ID, buildNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
        listener?.onPlaybackChanged()
    }

    fun playNext() {
        val size = SoundLibrary.sounds.size
        if (currentIndex < 0 || size == 0) return
        val next = if (shuffleOn && size > 1) {
            var random: Int
            do {
                random = Random.nextInt(size)
            } while (random == currentIndex)
            random
        } else {
            (currentIndex + 1) % size
        }
        play(next)
    }

    fun playPrevious() {
        val size = SoundLibrary.sounds.size
        if (currentIndex < 0 || size == 0) return
        play(if (currentIndex == 0) size - 1 else currentIndex - 1)
    }

    fun togglePlayPause() {
        val mp = player ?: return
        if (mp.isPlaying) mp.pause() else mp.start()
        refreshNotification()
        listener?.onPlaybackChanged()
    }

    fun toggleShuffle() {
        shuffleOn = !shuffleOn
        listener?.onPlaybackChanged()
    }

    fun stopPlayback() {
        cancelTimerInternal()
        releasePlayer()
        currentIndex = -1
        if (started) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
            started = false
        }
        listener?.onPlaybackChanged()
        listener?.onTimerTick(-1)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    // ---------- Temporizador ----------

    fun startTimer(minutes: Int) {
        cancelTimerInternal()
        timerRemainingMs = minutes * 60_000L
        lastNotifiedMinute = -1L
        sleepTimer = object : CountDownTimer(timerRemainingMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                timerRemainingMs = millisUntilFinished
                listener?.onTimerTick(millisUntilFinished)
                val minute = millisUntilFinished / 60_000L
                if (minute != lastNotifiedMinute) {
                    lastNotifiedMinute = minute
                    refreshNotification()
                }
            }

            override fun onFinish() {
                timerRemainingMs = -1L
                stopPlayback()
            }
        }.start()
        refreshNotification()
    }

    fun cancelTimer() {
        cancelTimerInternal()
        listener?.onTimerTick(-1)
        refreshNotification()
    }

    private fun cancelTimerInternal() {
        sleepTimer?.cancel()
        sleepTimer = null
        timerRemainingMs = -1L
    }

    // ---------- Notificación ----------

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Reproductor", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Controles de la música de Sleep Theory" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun refreshNotification() {
        if (started && player != null) {
            getSystemService(NotificationManager::class.java)
                .notify(NOTIFICATION_ID, buildNotification())
        }
    }

    private fun actionIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, MusicService::class.java).setAction(action)
        return PendingIntent.getService(
            this, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildNotification(): Notification {
        val song = SoundLibrary.sounds.getOrNull(currentIndex)
        val openIntent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
        val contentIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val text = if (timerRemainingMs > 0) {
            "Se apaga en ${(timerRemainingMs + 59_999L) / 60_000L} min"
        } else {
            "Sleep Theory"
        }
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val playPauseLabel = if (isPlaying) "Pausa" else "Reproducir"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sonidos)
            .setContentTitle(song?.name ?: "Sleep Theory")
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_skip_previous, "Anterior", actionIntent(ACTION_PREVIOUS, 1))
            .addAction(playPauseIcon, playPauseLabel, actionIntent(ACTION_PLAY_PAUSE, 2))
            .addAction(R.drawable.ic_skip_next, "Siguiente", actionIntent(ACTION_NEXT, 3))
            .addAction(R.drawable.ic_stop, "Detener", actionIntent(ACTION_STOP, 4))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    override fun onDestroy() {
        sleepTimer?.cancel()
        releasePlayer()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "sleep_theory_player"
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY_PAUSE = "com.example.sleeptheory01.PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.sleeptheory01.NEXT"
        const val ACTION_PREVIOUS = "com.example.sleeptheory01.PREVIOUS"
        const val ACTION_STOP = "com.example.sleeptheory01.STOP"
    }
}