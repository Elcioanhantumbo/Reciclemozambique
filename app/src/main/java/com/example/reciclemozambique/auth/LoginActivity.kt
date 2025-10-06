package com.example.reciclemozambique.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.ui.GuideActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)  // necessário

        // Fluxo simples: ir para a Guide ao clicar em "Log In"
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
            finish()
        }
    }
}
