package com.example.sleeptheory01

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider

class DiagnosticoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar la vista para el fragmento
        val view = inflater.inflate(R.layout.fragment_diagnostico, container, false)

        // Referencias a los elementos de la interfaz de usuario
        val submitButton = view.findViewById<Button>(R.id.btnSubmit)
        val sleepHoursGroup = view.findViewById<RadioGroup>(R.id.radioGroupSleepHours)
        val wakeFrequencyGroup = view.findViewById<RadioGroup>(R.id.radioGroupWakeFrequency)
        val deviceUsageGroup = view.findViewById<RadioGroup>(R.id.radioGroupDeviceUsage)
        val caffeineGroup = view.findViewById<RadioGroup>(R.id.radioGroupCaffeine)
        val roomConditionGroup = view.findViewById<RadioGroup>(R.id.radioGroupRoomCondition)
        val stressGroup = view.findViewById<RadioGroup>(R.id.radioGroupStress)

        // Configurar el evento de clic para el botón
        submitButton.setOnClickListener {
            // Obtener las respuestas seleccionadas
            val sleepHours = view.findViewById<RadioButton>(sleepHoursGroup.checkedRadioButtonId)?.text
            val wakeFrequency = view.findViewById<RadioButton>(wakeFrequencyGroup.checkedRadioButtonId)?.text
            val deviceUsage = view.findViewById<RadioButton>(deviceUsageGroup.checkedRadioButtonId)?.text
            val caffeine = view.findViewById<RadioButton>(caffeineGroup.checkedRadioButtonId)?.text
            val roomCondition = view.findViewById<RadioButton>(roomConditionGroup.checkedRadioButtonId)?.text
            val stress = view.findViewById<RadioButton>(stressGroup.checkedRadioButtonId)?.text

            // Validar que todas las preguntas tengan respuesta
            if (sleepHours != null && wakeFrequency != null && deviceUsage != null &&
                caffeine != null && roomCondition != null && stress != null
            ) {
                // Enviar las respuestas a ResultIMCActivity
                val intent = Intent(requireContext(), ResultIMCActivity::class.java)
                intent.putExtra("sleepHours", sleepHours.toString())
                intent.putExtra("wakeFrequency", wakeFrequency.toString())
                intent.putExtra("deviceUsage", deviceUsage.toString())
                intent.putExtra("caffeine", caffeine.toString())
                intent.putExtra("roomCondition", roomCondition.toString())
                intent.putExtra("stress", stress.toString())
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Por favor responde todas las preguntas.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }
}
