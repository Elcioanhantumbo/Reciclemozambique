// app/src/main/java/com/example/reciclemozambique/ui/BaseBottomActivity.kt
package com.example.reciclemozambique.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseBottomActivity : AppCompatActivity() {

    /** Referência ao BottomNavigation da tela (chame wireBottomNav() após setContentView). */
    protected abstract val bottomNav: BottomNavigationView

    /**
     * Item selecionado desta tela. Para telas fora do menu (ex.: Home),
     * deixe `null` e o menu não será marcado.
     */
    open val selectedItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /** Conecta o menu inferior às telas. Chamar no onCreate() depois do setContentView(). */
    protected fun wireBottomNav() {
        // Marca o item atual (quando existir)
        selectedItemId?.let { id ->
            if (bottomNav.selectedItemId != id) bottomNav.selectedItemId = id
        }

        bottomNav.setOnItemSelectedListener { item ->
            // Se já estamos na mesma aba, não faz nada
            if (selectedItemId != null && item.itemId == selectedItemId) return@setOnItemSelectedListener true

            when (item.itemId) {
                R.id.nav_map     -> start(MapActivity::class.java)
                R.id.nav_guide   -> start(GuideActivity::class.java)
                R.id.nav_agenda  -> start(ScheduleCollectionActivity::class.java)
                R.id.nav_rewards -> start(RewardsActivity::class.java)
                R.id.nav_profile -> start(UserProfileActivity::class.java)
                else -> false
            }
        }

        // Opcional: re-selecionar a mesma aba (ex.: rolar pro topo)
        bottomNav.setOnItemReselectedListener { /* noop por enquanto */ }
    }

    private fun <T : AppCompatActivity> start(cls: Class<T>): Boolean {
        // Evita empilhar múltiplas cópias no back stack e remove “flash” de animação
        startActivity(
            Intent(this, cls)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        overridePendingTransition(0, 0)
        finish()
        overridePendingTransition(0, 0)
        return true
    }
}
