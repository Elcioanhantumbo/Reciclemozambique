package com.example.reciclemozambique.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.databinding.ActivityScheduleDateBinding

class ScheduleDateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleDateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val material = intent.getStringExtra("selectedMaterial") ?: "paper"
        binding.textSelected.text = "Material: $material"

        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonConfirm.setOnClickListener {
            Toast.makeText(this, "Agendado para ${binding.datePicker.year}-${binding.datePicker.month + 1}-${binding.datePicker.dayOfMonth}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
