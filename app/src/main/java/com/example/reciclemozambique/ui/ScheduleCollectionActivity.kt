package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityScheduleCollectionBinding

class ScheduleCollectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleCollectionBinding
    private var selectedMaterial: String = "paper"  // Default: Paper (pré-selecionado)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener para RadioGroup (atualiza seleção)
        binding.radioGroupMaterials.setOnCheckedChangeListener { _, checkedId ->
            selectedMaterial = when (checkedId) {
                R.id.radioPlastic -> "plastic"
                R.id.radioGlass -> "glass"
                R.id.radioPaper -> "paper"
                R.id.radioMetal -> "metal"
                R.id.radioElectronics -> "electronics"
                R.id.radioOther -> "other"
                else -> "paper"  // Default
            }
            // Opcional: Atualize visual ou salve em SharedPreferences para fluxo multi-etapa
            Toast.makeText(this, "Selecionado: $selectedMaterial", Toast.LENGTH_SHORT).show()
        }

        // Botão Voltar
        binding.buttonBack.setOnClickListener {
            finish()  // Volta para tela anterior (ex: Agenda ou Home)
        }

        // Botão Next
        binding.buttonNext.setOnClickListener {
            if (selectedMaterial.isNotEmpty()) {
                // Salve seleção (ex: em Bundle ou SharedPreferences para próximo passo)
                val intent = Intent(this, ScheduleDateActivity::class.java)  // Próximo passo: Tela de data/horário (crie placeholder)
                intent.putExtra("selectedMaterial", selectedMaterial)
                startActivity(intent)
                // finish()  // Opcional: Fecha esta tela se for fluxo linear
            } else {
                Toast.makeText(this, "Selecione um tipo de material", Toast.LENGTH_SHORT).show()
            }
        }
    }
}