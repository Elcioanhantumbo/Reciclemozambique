package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityRewardsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RewardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRewardsBinding

    // URLs das imagens do HTML (carregue com Glide)
    private val badge1Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuAi2_2AvyRV9Lp6gHrpq-XFzGRJFml3lNkU4PdbzLcqcZ31ftpnl4GTIwVW6Meibyr0Fs1W7zuG4cx3z6UqgTJxSyoeQDmC5QTlq6AgnQi0OIn4xAPceK0OxblKge5AgfJKt8kCJ64XnrP9C_PZVbPKlI63A1NZxj3WTIggEEBq93IWn8gIAMrEitLMVq8EpJ-z24kEq6L_nnmkqg5wF9O0agdYFHhNapMN7ulF9dW9YcasfQJsXdK0WvoDGHGn7TPGjhwLqLyXhO0"
    private val badge2Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuDH1Y2oG6nY0xNGTBjV9cQ-ZvoEIlS5WVDGHQVHiq6h-7eHxIByVl8NWTiyjzOEXDjqrFO7Y3g5Pzdc4Ev39u8ZrtNW5evgprQFTUAcBnSgqpsaCaGwFRIuRUIIf5QByxZmx7WXFo2SVtirBOA7t0aOTTdChZsFF2kjeGiuPSWoQgWVoMwU_edNWQ-_1vWoANf0FpzWJkIFtsendjIGWRirJWYwpjGfGMQtz3r_31j-wehnFpfGbmYamaK60BTcN_dQc4LhOYSl-ds"
    private val rewardUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDX9p6vE_bFjE7dchXyPVEgrCbQnbKbPF_iUspLxU2VTwcjNNaoIdzZ2lZTkTEMvIR9gX9blyNe3lTE2invTgJoc5pHplYwLLBa9cr7WmykohkCbxiDzFzbuG3mlUQlmmqaL3j3hrlV5R6qROlsFBKJhtU4uMgLudPgXmsgysZysTqTIF0E6h41Eog7JEZjMqZS67MZwYJj2Z5H9Df9nWqemQou-4B_aWfEZeOjtvYE15_SijQn0xBEeFsNkApS_PMopJ5oztmvue0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Carregar imagens com Glide
        loadImages()

        // Listeners para botões
        binding.buttonBack.setOnClickListener {
            finish()  // Volta para tela anterior (ex: Home)
        }

        binding.buttonBadgeDetails1.setOnClickListener {
            // Abra tela de detalhes do badge (crie BadgeDetailsActivity)
            Toast.makeText(this, "Ver detalhes: Iniciante Verde", Toast.LENGTH_SHORT).show()
        }

        // Repita para buttonBadgeDetails2
        // binding.buttonBadgeDetails2.setOnClickListener { ... }

        binding.buttonRedeem.setOnClickListener {
            // Resgate recompensa (ex: verifique pontos, envie para Firebase)
            Toast.makeText(this, "Resgatando desconto de 10%...", Toast.LENGTH_SHORT).show()
            // Exemplo: val intent = Intent(this, RedeemActivity::class.java); startActivity(intent)
        }

        // Bottom Navigation: Configurar navegação
        setupBottomNavigation()
    }

    private fun loadImages() {
        // Badge 1
        Glide.with(this)
            .load(badge1Url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageBadge1)

        // Badge 2 (adicione ID no XML)
        Glide.with(this)
            .load(badge2Url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageBadge2)  // Assuma ID no XML

        // Recompensa
        Glide.with(this)
            .load(rewardUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageReward)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_guide -> {
                    val intent = Intent(this, GuideActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_agenda -> {
                    val intent = Intent(this, AgendaActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_rewards -> true  // Já está em Rewards
                R.id.nav_profile -> {
                    // Crie ProfileActivity
                    Toast.makeText(this, "Abrindo Perfil...", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}