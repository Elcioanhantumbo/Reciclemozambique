package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityScheduleCollectionBinding

class ScheduleCollectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleCollectionBinding
    private var selectedMaterial: String = "paper"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioGroupMaterials.setOnCheckedChangeListener { _, checkedId ->
            selectedMaterial = when (checkedId) {
                R.id.radioPlastic -> "plastic"
                R.id.radioGlass -> "glass"
                R.id.radioPaper -> "paper"
                R.id.radioMetal -> "metal"
                R.id.radioElectronics -> "electronics"
                R.id.radioOther -> "other"
                else -> "paper"
            }
            Toast.makeText(this, "Selecionado: $selectedMaterial", Toast.LENGTH_SHORT).show()
        }

        binding.buttonBack.setOnClickListener { finish() }

        binding.buttonNext.setOnClickListener {
            val intent = Intent(this, ScheduleDateActivity::class.java)  // <— aqui
            intent.putExtra("selectedMaterial", selectedMaterial)
            startActivity(intent)
        }
    }
}
