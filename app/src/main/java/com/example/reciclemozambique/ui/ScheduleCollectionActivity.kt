package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityScheduleCollectionBinding

class ScheduleCollectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleCollectionBinding
    private var selectedMaterial: String = "paper" // Valor inicial definido como 'paper'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener para o botão Voltar (ID: btnBack)
        binding.btnBack.setOnClickListener { // <-- CORRIGIDO de buttonBack para btnBack
            finish()
        }

        // Listener para o botão Próximo (ID: btnNext)
        binding.btnNext.setOnClickListener { // <-- CORRIGIDO de buttonNext para btnNext
            // Primeiro, vamos descobrir qual botão está selecionado
            selectedMaterial = when {
                binding.rbPlastic.isChecked -> "plastic"
                binding.rbGlass.isChecked -> "glass"
                binding.rbPaper.isChecked -> "paper"
                binding.rbMetal.isChecked -> "metal"
                binding.rbElectronics.isChecked -> "electronics"
                binding.rbOther.isChecked -> "other"
                else -> "paper" // Padrão
            }
            Toast.makeText(this, "Selecionado: $selectedMaterial", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ScheduleDateActivity::class.java)
            intent.putExtra("selectedMaterial", selectedMaterial)
            startActivity(intent)
        }

        // Configurar clique para os RadioButtons para que um desmarque os outros
        val radioButtons = listOf(binding.rbPlastic, binding.rbGlass, binding.rbPaper, binding.rbMetal, binding.rbElectronics, binding.rbOther)
        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                // Desmarca todos os outros botões quando um é clicado
                radioButtons.filter { it != radioButton }.forEach { it.isChecked = false }
                // Garante que o clicado esteja marcado
                (it as android.widget.RadioButton).isChecked = true
            }
        }
    }
}
