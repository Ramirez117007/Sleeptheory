package com.example.sleeptheory01

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class ResultIMCActivity : AppCompatActivity() {

    private lateinit var btnRecalculate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result_imcactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()


        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // Obtener respuestas del formulario
        val sleepHours = intent.getStringExtra("sleepHours") ?: "No especificado"
        val wakeFrequency = intent.getStringExtra("wakeFrequency") ?: "No especificado"
        val deviceUsage = intent.getStringExtra("deviceUsage") ?: "No especificado"
        val caffeine = intent.getStringExtra("caffeine") ?: "No especificado"
        val roomCondition = intent.getStringExtra("roomCondition") ?: "No especificado"
        val stress = intent.getStringExtra("stress") ?: "No especificado"

        // Calcular el puntaje de insomnio
        val insomniaScore = calculateInsomniaScore(
            sleepHours, wakeFrequency, deviceUsage, caffeine, roomCondition, stress
        )

        // Generar el análisis basado en el puntaje
        val analysis = generateAnalysis(insomniaScore)

        // Mostrar el análisis detallado
        val detailedResult = """
       
            - Horas de sueño: $sleepHours
            - Frecuencia de despertar: $wakeFrequency
            - Uso de dispositivos electrónicos: $deviceUsage
            - Consumo de cafeína: $caffeine
            - Condiciones de la habitación: $roomCondition
            - Estrés o ansiedad: $stress

            Resultado: $analysis
        """.trimIndent()

        resultTextView.text = detailedResult
    }

    private fun calculateInsomniaScore(
        sleepHours: String,
        wakeFrequency: String,
        deviceUsage: String,
        caffeine: String,
        roomCondition: String,
        stress: String
    ): Int {
        var score = 0

        // Definir puntajes
        val highRisk = 3
        val mediumRisk = 2
        val lowRisk = 1

        // Evaluar respuestas
        when (sleepHours) {
            "Menos de 5 horas" -> score += highRisk
            "5-7 horas" -> score += lowRisk
        }

        when (wakeFrequency) {
            "Muy frecuente" -> score += highRisk
            "Poco frecuente" -> score += lowRisk
        }

        when (deviceUsage) {
            "Sí, regularmente" -> score += mediumRisk
            "A veces" -> score += lowRisk
        }

        when (caffeine) {
            "Sí, regularmente" -> score += mediumRisk
            "A veces" -> score += lowRisk
        }

        when (roomCondition) {
            "No, nunca" -> score += highRisk
            "A veces" -> score += lowRisk
        }

        when (stress) {
            "Sí, regularmente" -> score += highRisk
            "A veces" -> score += lowRisk
        }

        return score
    }

    private fun generateAnalysis(score: Int): String {
        return when {
            score >= 10 -> "Parece que estás experimentando un insomnio severo. Considera buscar ayuda profesional y revisar tus hábitos de sueño."
            score in 6..9 -> "Tienes algunos síntomas de insomnio. Trabaja en mejorar tus hábitos de sueño y elimina factores que puedan estar afectando."
            else -> "Tu sueño parece estar en buen estado, pero siempre es bueno mantener hábitos saludables."
        }
    }
    private fun initListeners() {
        btnRecalculate.setOnClickListener { onBackPressed() }
    }

    private fun initComponents() {
        btnRecalculate = findViewById(R.id.btnRecalculate)
    }
}