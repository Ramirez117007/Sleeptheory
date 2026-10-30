package com.example.sleeptheory01

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnQuestion = view.findViewById<Button>(R.id.btnQuestion)
//        val btnRemember = view.findViewById<Button>(R.id.btnRemember)
        val btnSounds = view.findViewById<Button>(R.id.btnSounds)
        val btnAcerca = view.findViewById<Button>(R.id.btnAcerca)

        // Configura los listeners
        btnQuestion.setOnClickListener { navigateToQuestion() }
//        btnRemember.setOnClickListener { navigateToRemember() }
        btnSounds.setOnClickListener { navigateToSounds() }
        btnAcerca.setOnClickListener { navigateToAcerca() }
    }

    private fun navigateToQuestion() {
        val intent = Intent(requireContext(), QuestionActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRemember() {
        val intent = Intent(requireContext(), RememberActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSounds() {
        val intent = Intent(requireContext(), SonidoActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAcerca() {
        val intent = Intent(requireContext(), AcercaActivity::class.java)
        startActivity(intent)
    }


}
