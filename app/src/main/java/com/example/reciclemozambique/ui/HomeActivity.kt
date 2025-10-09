package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseBottomActivity() {
    private lateinit var binding: ActivityHomeBinding
    override val bottomNav get() = binding.bottomNavigation
    override val selectedItemId: Int? = null   // Home não está no menu

    private val urlCollection = "https://picsum.photos/seed/collection/400/300"
    private val urlTip = "https://picsum.photos/seed/tip/400/300"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        wireBottomNav()
        setGreeting()
        Glide.with(this).load(urlCollection).placeholder(R.drawable.ic_placeholder).into(binding.imageCollection)
        Glide.with(this).load(urlTip).placeholder(R.drawable.ic_placeholder).into(binding.imageTip)

        binding.buttonNotifications.setOnClickListener { Toast.makeText(this, R.string.notifications, Toast.LENGTH_SHORT).show() }
        binding.imageCollection.setOnClickListener { startActivity(Intent(this, ScheduleCollectionActivity::class.java)) }
        binding.imageTip.setOnClickListener { startActivity(Intent(this, GuideActivity::class.java)) }
    }

    private fun setGreeting() {
        val u = FirebaseAuth.getInstance().currentUser
        val nome = u?.displayName ?: u?.email ?: ""
        if (nome.isNotEmpty()) binding.textGreeting.text = getString(R.string.greeting).replace("Sofia", nome)
        val current = 1200
        binding.pointsProgressBar.progress = current
        binding.currentPointsText.text = current.toString()
        binding.pointsProgressText.text = "$current/2000"
    }
}
