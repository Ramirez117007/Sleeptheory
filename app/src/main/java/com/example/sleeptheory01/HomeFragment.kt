package com.example.sleeptheory01

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

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
        val btnRefresh = view.findViewById<Button>(R.id.btnRefreshSounds)
        val soundsContainer = view.findViewById<LinearLayout>(R.id.soundsContainer)
        btnRefresh.setOnClickListener {
            setupRandomSounds(inflater, soundsContainer, count = 2)
        }
        val btnChat = view.findViewById<Button>(R.id.btnChat)
        val btnStop = view.findViewById<Button>(R.id.btnStop)
        setupRandomPhrase(view.findViewById(R.id.phraseTextView), R.array.random_phrases)
        setupRandomPhrase(view.findViewById(R.id.randomPhraseTextView), R.array.random_exercises)
        setupRandomSounds(inflater, soundsContainer, 2)

        btnRefresh.setOnClickListener { setupRandomSounds(inflater, soundsContainer, 2) }
        btnChat.setOnClickListener { navigateToChat() }
        btnStop.setOnClickListener { stopSound() }

        return view
    }

    private fun setupRandomSounds(inflater: LayoutInflater, container: LinearLayout, count: Int) {
        container.removeAllViews()
        allSounds.shuffled().take(count).forEach { sound ->
            val button = inflater.inflate(R.layout.item_sound_button, container, false) as Button
            button.text = sound.name
            button.setOnClickListener { playSound(sound.resId) }
            container.addView(button)
        }
    }

    private fun setupRandomPhrase(textView: TextView, arrayResId: Int) {
        val phrases = resources.getStringArray(arrayResId)
        textView.text = phrases[Random.nextInt(phrases.size)]
    }

    private fun playSound(soundResourceId: Int) {
        stopSound()
        mediaPlayer = MediaPlayer.create(requireContext(), soundResourceId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopSound()
    }

    private fun navigateToChat() {
        startActivity(Intent(requireContext(), ChatActivity::class.java))
    }
}