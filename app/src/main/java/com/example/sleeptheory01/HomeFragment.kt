package com.example.sleeptheory01

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflar el diseño para este fragmento
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Referencia a los botones desde el diseño inflado
        val btnChat = view.findViewById<Button>(R.id.btnChat)
        val btnPlayBird = view.findViewById<Button>(R.id.btnPlayBird2)
        val btnPlayNatural = view.findViewById<Button>(R.id.btnPlayNatural2)
        val btnStop = view.findViewById<Button>(R.id.btnStop)

        // Configurar las frases aleatorias
        setupRandomPhrase(view.findViewById(R.id.phraseTextView), R.array.random_phrases)
        setupRandomPhrase(view.findViewById(R.id.randomPhraseTextView), R.array.random_exercises)

        // Configurar listeners para los botones
        btnChat.setOnClickListener { navigateToChat() }
        btnPlayBird.setOnClickListener { playSound(R.raw.bird) }
        btnPlayNatural.setOnClickListener { playSound(R.raw.natural) }
        btnStop.setOnClickListener { stopSound() }

        return view
    }

    private fun setupRandomPhrase(textView: TextView, arrayResId: Int) {
        val phrases = resources.getStringArray(arrayResId)
        val randomPhrase = phrases[Random.nextInt(phrases.size)]
        textView.text = randomPhrase
    }

    private fun playSound(soundResourceId: Int) {
        // Detiene cualquier sonido que esté sonando antes de iniciar uno nuevo
        stopSound()
        mediaPlayer = MediaPlayer.create(requireContext(), soundResourceId)
        mediaPlayer?.isLooping = true // Repite el sonido en bucle
        mediaPlayer?.start()
    }

    private fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSound() // Asegúrate de liberar recursos
    }


    private fun navigateToChat() {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        startActivity(intent)
    }
}