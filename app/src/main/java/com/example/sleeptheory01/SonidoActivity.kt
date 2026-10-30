package com.example.sleeptheory01

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SonidoActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sonido)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnPlayRain = findViewById<Button>(R.id.btnPlayRain)
        val btnPlayOcean = findViewById<Button>(R.id.btnPlayOcean)
        val btnPlayWhite = findViewById<Button>(R.id.btnPlayWhite)
        val btnPlayBird = findViewById<Button>(R.id.btnPlayBird)
        val btnPlayNatural = findViewById<Button>(R.id.btnPlayNatural)
        val btnPlayDS2 = findViewById<Button>(R.id.btnPlayDS2)
        val btnPlayHalo = findViewById<Button>(R.id.btnPlayHalo)
        val btnPlayRE7 = findViewById<Button>(R.id.btnPlayRE7)
        val btnStop = findViewById<Button>(R.id.btnStop)


        btnPlayRain.setOnClickListener {
            playSound(R.raw.rain)
        }

        btnPlayOcean.setOnClickListener {
            playSound(R.raw.ocean)
        }
        btnPlayWhite.setOnClickListener {
            playSound(R.raw.white)
        }
        btnPlayBird.setOnClickListener {
            playSound(R.raw.bird)
        }
        btnPlayNatural.setOnClickListener {
            playSound(R.raw.natural)
        }
        btnPlayDS2.setOnClickListener {
            playSound(R.raw.ds2)
        }
        btnPlayHalo.setOnClickListener {
            playSound(R.raw.halo)
        }
        btnPlayRE7.setOnClickListener {
            playSound(R.raw.re7)
        }
        btnStop.setOnClickListener {
            stopSound()
        }
    }
    private fun playSound(soundResourceId: Int) {
        // Detiene cualquier sonido que esté sonando antes de iniciar uno nuevo
        stopSound()
        mediaPlayer = MediaPlayer.create(this, soundResourceId)
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
}