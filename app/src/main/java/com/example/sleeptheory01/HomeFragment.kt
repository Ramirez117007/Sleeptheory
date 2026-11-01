package com.example.sleeptheory01

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = -1
    private var shuffleOn = false
    private var sleepTimer: CountDownTimer? = null

    private lateinit var playerContainer: LinearLayout
    private lateinit var tvSongTitle: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnShuffle: ImageButton

    private val allSounds: List<SoundItem> = listOf(
        SoundItem("Sonido de lluvia", R.raw.rain),
        SoundItem("Sonido de océano", R.raw.ocean),
        SoundItem("Sonido blanco", R.raw.white),
        SoundItem("Sonido de naturaleza", R.raw.bird),
        SoundItem("Sonido de naturaleza 2", R.raw.natural),
        SoundItem("Sonido de DS2", R.raw.ds2),
        SoundItem("Sonido de H3 ODST", R.raw.halo),
        SoundItem("Sonido de RE7", R.raw.re7)
    )

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
        btnStop.setOnClickListener { stopPlayback() }

        btnPlayPause.setOnClickListener { togglePlayPause() }
        btnNext.setOnClickListener { playNext() }
        btnPrevious.setOnClickListener { playPrevious() }
        btnShuffle.setOnClickListener { toggleShuffle() }
        btnTimer.setOnClickListener { showTimerDialog() }

        return view
    }

    private fun setupRandomSounds(inflater: LayoutInflater, container: LinearLayout, count: Int) {
        container.removeAllViews()
        allSounds.shuffled().take(count).forEach { sound ->
            val button = inflater.inflate(R.layout.item_sound_button, container, false) as Button
            button.text = sound.name
            button.setOnClickListener { playSongAt(allSounds.indexOf(sound)) }
            container.addView(button)
        }
    }

    private fun setupRandomPhrase(textView: TextView, arrayResId: Int) {
        val phrases = resources.getStringArray(arrayResId)
        textView.text = phrases[Random.nextInt(phrases.size)]
    }

    // ---------- Reproductor ----------

    private fun playSongAt(index: Int) {
        releasePlayer()
        currentIndex = index
        val song = allSounds[index]
        mediaPlayer = MediaPlayer.create(requireContext(), song.resId)?.apply {
            setOnCompletionListener { playNext() }
            start()
        }
        tvSongTitle.text = song.name
        playerContainer.visibility = View.VISIBLE
        updatePlayPauseIcon()
    }

    private fun playNext() {
        if (allSounds.isEmpty()) return
        val next = if (shuffleOn && allSounds.size > 1) {
            var random: Int
            do {
                random = Random.nextInt(allSounds.size)
            } while (random == currentIndex)
            random
        } else {
            (currentIndex + 1) % allSounds.size
        }
        playSongAt(next)
    }

    private fun playPrevious() {
        if (allSounds.isEmpty()) return
        val previous = if (currentIndex <= 0) allSounds.size - 1 else currentIndex - 1
        playSongAt(previous)
    }

    private fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) player.pause() else player.start()
        updatePlayPauseIcon()
    }

    private fun updatePlayPauseIcon() {
        val playing = mediaPlayer?.isPlaying == true
        btnPlayPause.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
    }

    private fun toggleShuffle() {
        shuffleOn = !shuffleOn
        btnShuffle.alpha = if (shuffleOn) 1f else 0.5f
    }

    private fun stopPlayback() {
        releasePlayer()
        cancelTimer()
        playerContainer.visibility = View.GONE
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // ---------- Temporizador ----------

    private fun showTimerDialog() {
        val options = arrayOf("15 minutos", "30 minutos", "60 minutos", "Cancelar temporizador")
        val minutes = intArrayOf(15, 30, 60, 0)
        AlertDialog.Builder(requireContext())
            .setTitle("Apagar música en…")
            .setItems(options) { _, which ->
                if (minutes[which] == 0) cancelTimer() else startTimer(minutes[which])
            }
            .show()
    }

    private fun startTimer(minutes: Int) {
        cancelTimer()
        tvTimer.visibility = View.VISIBLE
        sleepTimer = object : CountDownTimer(minutes * 60_000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val m = millisUntilFinished / 60_000
                val s = (millisUntilFinished / 1000) % 60
                tvTimer.text = String.format(Locale.getDefault(), "Se apaga en %02d:%02d", m, s)
            }

            override fun onFinish() {
                stopPlayback()
            }
        }.start()
    }

    private fun cancelTimer() {
        sleepTimer?.cancel()
        sleepTimer = null
        tvTimer.visibility = View.GONE
    }

    // ---------- Ciclo de vida ----------

    override fun onDestroyView() {
        super.onDestroyView()
        sleepTimer?.cancel()
        sleepTimer = null
        releasePlayer()
    }

    private fun navigateToChat() {
        startActivity(Intent(requireContext(), ChatActivity::class.java))
    }
}