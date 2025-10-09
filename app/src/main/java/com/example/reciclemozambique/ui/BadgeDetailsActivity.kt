package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityBadgeDetailsBinding

class BadgeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBadgeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar fixa
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Extras
        val title = intent.getStringExtra("title") ?: getString(R.string.badge_name_1)
        val level = intent.getStringExtra("level") ?: getString(R.string.badge_level_1)
        val desc  = intent.getStringExtra("desc")  ?: getString(R.string.badge_desc_1)
        val image = intent.getStringExtra("imageUrl")

        binding.toolbar.title = title
        binding.textTitle.text = title
        binding.textLevel.text = level
        binding.textShort.text = desc

        if (!image.isNullOrBlank()) {
            Glide.with(this).load(image)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.imageBadge)
        } else {
            binding.imageBadge.setImageResource(R.drawable.ic_placeholder)
        }

        // conteúdo padrão
        binding.textWhy.text = getString(R.string.badge_why_default, title)
        binding.textHow.text = getString(R.string.badge_how_default)
        binding.textTips.text = getString(R.string.badge_tips_default)

        // CTA: Participar (leva ao agendamento)
        binding.buttonParticipate.setOnClickListener {
            startActivity(Intent(this, ScheduleCollectionActivity::class.java))
        }
    }
}
