package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout (o novo activity_home.xml unificado)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Define a Toolbar
        setSupportActionBar(binding.toolbar)
        // Opcional: Remover o título padrão da Toolbar se você já tem um TextView customizado
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configura a navegação manual para o BottomNavigationView
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_agenda -> {
                    startActivity(Intent(this, AgendaActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
