package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityGuideBinding
import com.example.reciclemozambique.ui.UserProfileActivity

class GuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuideBinding

    // URLs de exemplo
    private val imageItemUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCxQ_zfOg3f775zk67uL37cQDAGZ8c-YfE5NU2R5VT092PrIa4LsULmxF-8rGgsbk_tfMEVbre0i3e7ou9Ppu6owcfnzTZXFbmGT9HRSaH7JcjQa25bpxWq3-URmfGiDa2BMT2BBg64j0H0AAuSyCUL3c9Ux_RctI7jEdmoG-PdlTQiNmMrade5nY1SMQxLzLSN6JM0DeTFlytaXJ_TK11G15n3mt5dZWfHYqJHfH5LAPxqW0haDvm7oQWORPfMQV5s-ONFmZ823qE"
    private val tip1Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuA6tKfQMpIxZ7-Xj-ZWwYrGV9DyQL7mci26p4knsNa7z-01VExpDCOshi3d7dnNDxdmwjpPuMvx6maMqB_8if4CRXUvhpxfLPRlLUdb5UNyhJ_gzNQzjIygF17OuekW-Etft6yK_UPwDExxNx0R92qLN8nZHBWz6Mw023tdduVnrlOR7NOph57HUF67gAFROkp2jbt2BglGh7k0QHW46_mrU5werlw8HKwMcqbWIsw4zs8AAuKJlXskAKSrZz7Hjlz-2hpEXVeIm8A"
    private val tip2Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuCaDqysCGfyOLx4YwalNBg7ixIBZya3cq5tIOM3RkMs9ow3GXIcn7C-nzY7vC84zRkFndBuZ-AP71nhtmsJj0gEP_WBFZ0Wc0NA97o7sCLi7CZMvaUh8_k8y8FSurd1KrtRrWCqbALWlGOPG4unoEWJsY7wnhXPgol_ZSC9WPpFNsqRHoKpR9CyPP6vGSEYY4yWuH46yNm-8edebl3huZzfoeeBDf3kWCk7rWCbbpnYeOtP2sjJLT90A7M14WYwStobPS2HsHLIUyA"
    private val tip3Url = "https://lh3.googleusercontent.com/aida-public/AB6AXuB36rWNMaBkEFoYC-ujnZSkS7thfKM6lUZYNACbzjnh3T0noTSkd5ZnbxrWPAfLlpcPzPasUwG21IWY_w3Gh0buj7u3spKKHTITucSPicducvt_MjgHy0x8Vx8pb2Fus0xBdsQW93Y9GEmkQjcphqelRWYftXZK9x8F62Zp8tkTAbDeskCfSzHmOR3ENlUPx7d0AheS9AChAm_aeuIonGPCzEE7TPfidHhlmYPxLjfLFlI2wB0oERqIAc6oLAyGA7_t33b6u0GuzXU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Imagens
        loadImages()

        // Voltar
        binding.buttonBack.setOnClickListener { finish() }

        // Busca simples
        binding.editSearch.setOnEditorActionListener { v, _, _ ->
            val q = v.text.toString().trim()
            Toast.makeText(
                this,
                if (q.isNotEmpty()) "Buscando por: $q" else "Digite um termo de busca",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        // Ação instruções
        binding.buttonInstructions.setOnClickListener {
            Toast.makeText(this, "Abrindo instruções de descarte...", Toast.LENGTH_SHORT).show()
        }

        // Bottom nav
        setupBottomNavigation()

        // Destaque ícone "Guia"
        highlightGuideIcon()
    }

    private fun loadImages() {
        // Item principal
        Glide.with(this)
            .load(imageItemUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imageItem)

        // Dicas
        Glide.with(this).load(tip1Url)
            .placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)
            .into(binding.imageTip1)

        Glide.with(this).load(tip2Url)
            .placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)
            .into(binding.imageTip2)

        Glide.with(this).load(tip3Url)
            .placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)
            .into(binding.imageTip3)
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
                    // Não faz nada, pois já estamos nesta tela.
                    true
                }
                R.id.nav_agenda -> {
                    startActivity(Intent(this, ScheduleCollectionActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    // **CORRIGIDO**: Sintaxe do Intent ajustada
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // **CORRIGIDO**: Função movida para dentro da classe
    private fun highlightGuideIcon() {
        val guideItem = binding.bottomNavigation.menu.findItem(R.id.nav_guide)
        // Opcional: Para destacar o ícone da tela atual
        guideItem.isChecked = true
    }
}
