package com.example.reciclemozambique.ui // Verifique se este é o seu pacote correto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.databinding.ActivityUserProfileBinding
import com.example.reciclemozambique.ui.GuideActivity
import com.example.reciclemozambique.ui.MapActivity
import com.example.reciclemozambique.ui.RewardsActivity
import com.example.reciclemozambique.ui.ScheduleCollectionActivity

class UserProfileActivity : AppCompatActivity() {

    // 1. Declare a variável de binding
    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 2. Infla o layout usando View Binding
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ====== Dados do usuário (acessados via binding) ======
        binding.tvUserName.text = "Sofia Mendes"
        binding.tvUserEmail.text = "sofia.mendes@email.com"
        // Você precisa de um recurso chamado 'profile_placeholder' na sua pasta res/drawable
        // binding.imgProfile.setImageResource(R.drawable.profile_placeholder)

        // ====== Listeners para os botões (usando binding) ======
        binding.btnLogout.setOnClickListener {
            Toast.makeText(this, "Logout efetuado", Toast.LENGTH_SHORT).show()
            // Lógica de logout (ex: limpar sessão, ir para tela de login)
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Editar perfil", Toast.LENGTH_SHORT).show()
            // Exemplo: startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // ====== Lógica de Navegação Inferior ======
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Marcando o ícone de perfil como ativo
        binding.navProfile.isSelected = true

        // Listeners para cada item da navegação
        binding.navMap.setOnClickListener {
            // A classe correta é MapActivity
            startActivity(Intent(this, MapActivity::class.java))
            finish() // Fecha a tela atual para não empilhar
        }

        binding.navGuide.setOnClickListener {
            // A classe correta é GuideActivity
            startActivity(Intent(this, GuideActivity::class.java))
            finish()
        }

        binding.navAgenda.setOnClickListener {
            // A classe correta para agendamento é ScheduleCollectionActivity
            startActivity(Intent(this, ScheduleCollectionActivity::class.java))
            finish()
        }

        binding.navAwards.setOnClickListener {
            // A classe correta para prêmios é RewardsActivity
            startActivity(Intent(this, RewardsActivity::class.java))
            finish()
        }

        binding.navProfile.setOnClickListener {
            // Já estamos aqui, então não fazemos nada
            Toast.makeText(this, "Você já está no seu Perfil", Toast.LENGTH_SHORT).show()
        }
    }
}
