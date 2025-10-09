// app/src/main/java/com/example/reciclemozambique/ui/RewardsActivity.kt
package com.example.reciclemozambique.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.data.PointsStore
import com.example.reciclemozambique.databinding.ActivityRewardsBinding

class RewardsActivity : BaseBottomActivity() {
    private lateinit var binding: ActivityRewardsBinding
    override val bottomNav get() = binding.bottomNavigation
    override val selectedItemId = R.id.nav_rewards

    private val badge1Url = "https://picsum.photos/seed/b1/256"
    private val badge2Url = "https://picsum.photos/seed/b2/256"
    private val rewardUrl = "https://picsum.photos/seed/reward/600/300"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.rewards_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        wireBottomNav()
        bindUi()
    }

    private fun bindUi() = with(binding) {
        val total = PointsStore.totalPoints(this@RewardsActivity)
        totalPointsValue.text = getString(R.string.total_points_value).replace("1200", total.toString())

        val pct = (total % 1000) / 1000f
        nextBadgeProgress.layoutParams =
            (nextBadgeProgress.layoutParams as LinearLayout.LayoutParams).apply {
                weight = (pct * 100).coerceIn(0f, 100f)
            }
        nextBadgeProgress.requestLayout()

        renderHistory()

        Glide.with(this@RewardsActivity).load(badge1Url).placeholder(R.drawable.ic_placeholder).into(imageBadge1)
        Glide.with(this@RewardsActivity).load(badge2Url).placeholder(R.drawable.ic_placeholder).into(imageBadge2)
        Glide.with(this@RewardsActivity).load(rewardUrl).placeholder(R.drawable.ic_placeholder).into(imageReward)

        buttonBadgeDetails1.setOnClickListener {
            startActivity(
                android.content.Intent(this@RewardsActivity, RecyclingLearnActivity::class.java)
                    .putExtra("title", "Iniciante verde")
                    .putExtra("content",
                        getString(R.string.badge_why_default, "Iniciante verde") + "\n\n" +
                                getString(R.string.badge_how_default) + "\n\n" +
                                getString(R.string.badge_tips_default)
                    )
            )
        }
        buttonBadgeDetails2.setOnClickListener {
            startActivity(
                android.content.Intent(this@RewardsActivity, RecyclingLearnActivity::class.java)
                    .putExtra("title", "Coleta consciente")
                    .putExtra("content",
                        getString(R.string.badge_why_default, "Coleta consciente") + "\n\n" +
                                getString(R.string.badge_how_default) + "\n\n" +
                                getString(R.string.badge_tips_default)
                    )
            )
        }

        // >>> NOVO: “Resgatar” = creditar pontos (claim)
        buttonRedeem.setOnClickListener { showClaimPointsDialog() }
    }

    private fun showClaimPointsDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_claim_points, null)
        val etPoints = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPoints)
        val etReason = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etReason)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            .setOnClickListener { dialog.dismiss() }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAdd)
            .setOnClickListener {
                val pts = etPoints.text?.toString()?.trim()?.toIntOrNull() ?: 0
                val reason = etReason.text?.toString()?.trim().orEmpty()

                if (pts <= 0) {
                    etPoints.error = getString(R.string.field_required)
                    return@setOnClickListener
                }
                if (reason.isEmpty()) {
                    etReason.error = getString(R.string.field_required)
                    return@setOnClickListener
                }

                PointsStore.addPoints(this, pts, reason)
                Toast.makeText(this, getString(R.string.points_added_ok), Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                // Atualiza total e histórico
                binding.totalPointsValue.text = getString(R.string.total_points_value)
                    .replace("1200", PointsStore.totalPoints(this).toString())
                renderHistory()
            }

        dialog.show()
    }

    private fun renderHistory() = with(binding) {
        historyContainer.removeAllViews()
        val inflater = LayoutInflater.from(this@RewardsActivity)
        PointsStore.history(this@RewardsActivity).forEach { h ->
            val card = inflater.inflate(R.layout.item_points_history, historyContainer, false)
            card.findViewById<TextView>(R.id.textTitle).text = h.title
            card.findViewById<TextView>(R.id.textDate).text = h.date
            card.findViewById<TextView>(R.id.textPoints).apply {
                text = (if (h.points >= 0) "+" else "") + h.points.toString()
                setTextColor(getColor(if (h.points >= 0) R.color.primary else R.color.divider_dark))
            }
            card.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_recycling)
            historyContainer.addView(card)
        }
    }
}
