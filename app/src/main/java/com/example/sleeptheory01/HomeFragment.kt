package com.example.sleeptheory01

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment(), MusicService.Listener {

    private var service: MusicService? = null

    private lateinit var playerContainer: LinearLayout
    private lateinit var tvSongTitle: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnShuffle: ImageButton

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val s = (binder as MusicService.LocalBinder).getService()
            s.listener = this@HomeFragment
            service = s
            refreshUi()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnChat = view.findViewById<Button>(R.id.btnChat)
        val btnStop = view.findViewById<Button>(R.id.btnStop)
        val btnRefresh = view.findViewById<Button>(R.id.btnRefreshSounds)
        val soundsContainer = view.findViewById<LinearLayout>(R.id.soundsContainer)

        playerContainer = view.findViewById(R.id.playerContainer)
        tvSongTitle = view.findViewById(R.id.tvSongTitle)
        tvTimer = view.findViewById(R.id.tvTimer)
        btnPlayPause = view.findViewById(R.id.btnPlayPause)
        btnShuffle = view.findViewById(R.id.btnShuffle)
        val btnPrevious = view.findViewById<ImageButton>(R.id.btnPrevious)
        val btnNext = view.findViewById<ImageButton>(R.id.btnNext)
        val btnTimer = view.findViewById<ImageButton>(R.id.btnTimer)

        setupRandomPhrase(view.findViewById(R.id.phraseTextView), R.array.random_phrases)
        setupRandomPhrase(view.findViewById(R.id.randomPhraseTextView), R.array.random_exercises)
        setupRandomSounds(inflater, soundsContainer, 2)

        btnRefresh.setOnClickListener { setupRandomSounds(inflater, soundsContainer, 2) }
        btnChat.setOnClickListener { navigateToChat() }
        btnStop.setOnClickListener { service?.stopPlayback() }

        btnPlayPause.setOnClickListener { service?.togglePlayPause() }
        btnNext.setOnClickListener { service?.playNext() }
        btnPrevious.setOnClickListener { service?.playPrevious() }
        btnShuffle.setOnClickListener { service?.toggleShuffle() }
        btnTimer.setOnClickListener { showTimerDialog() }

        return view
    }

    // ---------- Conexión con el servicio ----------

    override fun onStart() {
        super.onStart()
        val ctx = requireContext().applicationContext
        ctx.bindService(Intent(ctx, MusicService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        service?.listener = null
        service = null
        requireContext().applicationContext.unbindService(connection)
    }

    override fun onPlaybackChanged() = refreshUi()

    override fun onTimerTick(remainingMs: Long) {
        if (remainingMs < 0) {
            tvTimer.visibility = View.GONE
        } else {
            val m = remainingMs / 60_000
            val s = (remainingMs / 1000) % 60
            tvTimer.text = String.format(Locale.getDefault(), "Se apaga en %02d:%02d", m, s)
            tvTimer.visibility = View.VISIBLE
        }
    }

    private fun refreshUi() {
        val s = service ?: return
        if (!s.hasSong) {
            playerContainer.visibility = View.GONE
            tvTimer.visibility = View.GONE
            return
        }
        playerContainer.visibility = View.VISIBLE
        tvSongTitle.text = SoundLibrary.sounds[s.currentIndex].name
        btnPlayPause.setImageResource(if (s.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        btnShuffle.alpha = if (s.shuffleOn) 1f else 0.5f
        onTimerTick(s.timerRemainingMs)
    }

    // ---------- Interfaz ----------

    private fun setupRandomSounds(inflater: LayoutInflater, container: LinearLayout, count: Int) {
        container.removeAllViews()
        SoundLibrary.sounds.shuffled().take(count).forEach { sound ->
            val button = inflater.inflate(R.layout.item_sound_button, container, false) as Button
            button.text = sound.name
            button.setOnClickListener {
                askNotificationPermission()
                service?.play(SoundLibrary.sounds.indexOf(sound))
            }
            container.addView(button)
        }
    }

    private fun setupRandomPhrase(textView: TextView, arrayResId: Int) {
        val phrases = resources.getStringArray(arrayResId)
        textView.text = phrases[Random.nextInt(phrases.size)]
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showTimerDialog() {
        val options = arrayOf("15 minutos", "30 minutos", "60 minutos", "Cancelar temporizador")
        val minutes = intArrayOf(15, 30, 60, 0)
        AlertDialog.Builder(requireContext())
            .setTitle("Apagar música en…")
            .setItems(options) { _, which ->
                if (minutes[which] == 0) service?.cancelTimer() else service?.startTimer(minutes[which])
            }
            .show()
    }

    private fun navigateToChat() {
        startActivity(Intent(requireContext(), ChatActivity::class.java))
    }
}