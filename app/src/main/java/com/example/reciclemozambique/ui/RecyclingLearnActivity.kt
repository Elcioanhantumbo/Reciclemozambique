package com.example.reciclemozambique.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.databinding.ActivityLearnBinding

class RecyclingLearnActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLearnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.toolbar.title = intent.getStringExtra("title") ?: "Reciclagem"
        binding.textContent.text = intent.getStringExtra("content") ?: ""
    }
}
