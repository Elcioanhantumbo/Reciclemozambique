package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.ui.ScheduleCollectionActivity

class AgendaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redireciona direto para ScheduleCollectionActivity
        val intent = Intent(this, ScheduleCollectionActivity::class.java)
        startActivity(intent)
        finish() // Fecha a tela atual
    }
}
