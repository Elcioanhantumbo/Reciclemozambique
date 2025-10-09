package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityScheduleCollectionBinding
import com.google.android.material.card.MaterialCardView

class ScheduleCollectionActivity : BaseBottomActivity() {

    private lateinit var binding: ActivityScheduleCollectionBinding
    override val bottomNav get() = binding.bottomNavigation
    override val selectedItemId = R.id.nav_agenda

    private var selectedMaterial: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wireBottomNav()
        setupCards()
        binding.btnNext.setOnClickListener { goNext() }
    }

    /** Visual “selecionado” = borda visível */
    private fun MaterialCardView.setCheckedState(checked: Boolean) {
        isChecked = checked
        strokeWidth = if (checked) resources.getDimensionPixelSize(R.dimen.stroke_2dp) else 0
    }

    private fun setupCards() = with(binding) {
        val all = listOf(cardPlastic, cardGlass, cardPaper, cardMetal, cardElectronics, cardOther)

        // Marca como checkáveis via código (evita erros no XML)
        all.forEach { it.isCheckable = true }

        fun select(card: MaterialCardView, material: String) {
            all.forEach { it.setCheckedState(it == card) }
            selectedMaterial = material

            inputOther.error = null
            inputOther.visibility = if (material == "other") View.VISIBLE else View.GONE
            if (material == "other") editOther.requestFocus()
        }

        cardPlastic.setOnClickListener     { select(cardPlastic,     "plastic") }
        cardGlass.setOnClickListener       { select(cardGlass,       "glass") }
        cardPaper.setOnClickListener       { select(cardPaper,       "paper") }
        cardMetal.setOnClickListener       { select(cardMetal,       "metal") }
        cardElectronics.setOnClickListener { select(cardElectronics, "electronics") }
        cardOther.setOnClickListener       { select(cardOther,       "other") }

        // estado padrão
        cardPaper.performClick()
    }

    private fun goNext() {
        val base = selectedMaterial ?: run {
            Toast.makeText(this, R.string.field_required, Toast.LENGTH_SHORT).show()
            return
        }

        val finalMaterial = if (base == "other") {
            val typed = binding.editOther.text?.toString()?.trim().orEmpty()
            if (typed.isEmpty()) {
                binding.inputOther.error = getString(R.string.field_required)
                return
            }
            typed
        } else base

        startActivity(
            Intent(this, ScheduleDateActivity::class.java)
                .putExtra("selectedMaterial", finalMaterial)
        )
    }
}
