package com.example.sleeptheory01

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Usamos un Handler para esperar 2 segundos antes de abrir SecondActivity
        Handler().postDelayed({
            val intent = Intent(this, startActivity::class.java)
            startActivity(intent)
            finish()  // Opcional, para evitar que el usuario regrese a esta actividad
        }, 2000)  // 2000 milisegundos = 2 segundos
    }
}