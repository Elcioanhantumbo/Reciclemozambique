package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.auth.LoginActivity  // Exemplo; ajuste se necessário
import com.example.reciclemozambique.databinding.ActivityGuideBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class GuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuideBinding

    // URLs das imagens do HTML (carregue com Glide)
    private val imageItemUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCxQ_zfOg3f775zk67uL37cQDAGZ8c-YfE5NU2R5VT092PrIa4LsULmxF-8rGgsbk_tfMEVbre0i3e7ou9Ppu6owcfnzTZXFbmGT9HRSaH7JcjQa25bpxWq3-URmfGiDa2BMT2BBg64j0H0AAuSyCUL3c9Ux_RctI7jEdmoG-PdlTQiNmMrade5nY1SMQxLzLSN6JM0DeTFlytaXJ_TK11G15n3mt5dZWfHYqJHfH5LAPxqW0haDvm7oQWORPfMQV5s-ONFmZ823qE"
    private val tip1Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuA6tKfQMpIxZ7-Xj-ZWwYrGV9DyQL7mci26p4knsNa7z-01VExpDCOshi3d7dnNDxdmwjpPuMvx6maMqB_8if4CRXUvhpxfLPRlLUdb5UNyhJ_gzNQzjIygF17OuekW-Etft6yK_UPwDExxNx0R92qLN8nZHBWz6Mw023tdduVnrlOR7NOph57HUF67gAFROkp2jbt2BglGh7k0QHW46_mrU5werlw8HKwMcqbWIsw4zs8AAuKJlXskAKSrZz7Hjlz-2hpEXVeIm8A"
    private val tip2Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuCaDqysCGfyOLx4YwalNBg7ixIBZya3cq5tIOM3RkMs9ow3GXIcn7C-nzY7vC84zRkFndBuZ-AP71nhtmsJj0gEP_WBFZ0Wc0NA97o7sCLi7CZMvaUh8_k8y8FSurd1KrtRrWCqbALWlGOPG4unoEWJsY7wnhXPgol_ZSC9WPpFNsqRHoKpR9CyPP6vGSEYY4yWuH46yNm-8edebl3huZzfoeeBDf3kWCk7rWCbbpnYeOtP2sjJLT90A7M14WYwStobPS2HsHLIUyA"
    private val tip3Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuB36rWNMaBkEFoYC-ujnZSkS7thfKM6lUZYNACbzjnh3T0noTSkd5ZnbxrWPAfLlpcPzPasUwG21IWY_w3Gh0buj7u3spKKHTITucSPicducvt_MjgHy0x8Vx8pb2Fus0xBdsQW93Y9GEmkQjcphqelRWYftXZK9x8F62Zp8tkTAbDeskCfSzHmOR3ENlUPx7d0AheS9AChAm_aeuIonGPCzEE7TPfidHhlmYPxLjfLFlI2wB0oERqIAc6oLAyGA7_t33b6u0GuzXU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Carregar imagens com Glide
        loadImages()

        // Listeners
        binding.buttonBack.setOnClickListener {
            finish()  // Volta para tela anterior (ex: Home ou Map)
        }

        binding.editSearch.setOnClickListener {
            val query = binding.editSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                Toast.makeText(this, "Buscando por: $query", Toast.LENGTH_SHORT).show()
                // Aqui: Implemente busca real (ex: filtre lista de resíduos)
            } else {
                Toast.makeText(this, "Digite um termo de busca", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonInstructions.setOnClickListener {
            // Navegue para tela de instruções detalhadas (crie uma nova Activity se necessário)
            Toast.makeText(this, "Abrindo instruções de descarte...", Toast.LENGTH_SHORT).show()
            // Exemplo: val intent = Intent(this, InstructionsActivity::class.java); startActivity(intent)
        }

        // Bottom Navigation: Configurar navegação entre telas
        setupBottomNavigation()

        // Destaque visual no ícone "Guia" (círculo de fundo)
        highlightGuideIcon()
    }

    private fun loadImages() {
        // Imagem do item principal
        Glide.with(this)
            .load(imageItemUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageItem)

        // Dicas rápidas
        Glide.with(this)
            .load(tip1Url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageTip1)  // Assuma ID no XML

        Glide.with(this)
            .load(tip2Url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageTip2)  // Adicione ID no XML para dica 2

        Glide.with(this)
            .load(tip3Url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageTip3)  // Adicione ID no XML para dica 3
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
                R.id.nav_guide -> true  // Já está na Guia
                R.id.nav_agenda -> {
                    // Crie AgendaActivity
                    Toast.makeText(this, "Abrindo Agenda...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_rewards -> {
                    // Crie RewardsActivity
                    Toast.makeText(this, "Abrindo Recompensas...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    // Crie ProfileActivity
                    Toast.makeText(this, "Abrindo Perfil...", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun highlightGuideIcon() {
        // Customiza o ícone "Guia" com fundo circular (usando drawable layered)
        val guideItem = binding.bottomNavigation.menu.findItem(R.id.nav_guide)
        val iconWithBackground = ContextCompat.getDrawable(this, R.drawable.ic_menu_book_with_circle)  // Crie este drawable
        guideItem.icon = iconWithBackground
    }
}