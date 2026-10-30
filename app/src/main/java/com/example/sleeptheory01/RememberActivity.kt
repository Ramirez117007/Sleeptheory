package com.example.sleeptheory01

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RememberActivity : AppCompatActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remember)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReminders)
        val btnAddReminder = findViewById<Button>(R.id.btnAddReminder)

        // Configurar RecyclerView
        reminderAdapter = ReminderAdapter(reminders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reminderAdapter

        // Agregar recordatorio
        btnAddReminder.setOnClickListener {
            showAddReminderDialog()
        }
    }

    private fun showAddReminderDialog() {
        val input = EditText(this)
        input.hint = "Escribe el recordatorio"

        AlertDialog.Builder(this)
            .setTitle("Nuevo Recordatorio")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val text = input.text.toString()
                if (text.isNotBlank()) {
                    reminders.add(Reminder(text))
                    reminderAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}