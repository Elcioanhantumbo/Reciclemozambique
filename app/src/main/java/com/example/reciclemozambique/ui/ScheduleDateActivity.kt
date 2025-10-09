package com.example.reciclemozambique.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityScheduleDateBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class ScheduleDateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleDateBinding

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val material = intent.getStringExtra("selectedMaterial") ?: "paper"
        binding.textSelected.text = getString(R.string.select_material_type) + ": $material"

        // Regras de intervalo: hoje … hoje+30
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val max = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 30) }

        binding.datePicker.minDate = today.timeInMillis
        binding.datePicker.maxDate = max.timeInMillis

        binding.buttonBack.setOnClickListener { finish() }

        binding.buttonConfirm.setOnClickListener {
            val y = binding.datePicker.year
            val m = binding.datePicker.month     // 0..11
            val d = binding.datePicker.dayOfMonth

            // Horário padrão 09:00 (ajuste depois quando tiver faixas reais)
            val chosen = Calendar.getInstance().apply {
                set(Calendar.YEAR, y)
                set(Calendar.MONTH, m)
                set(Calendar.DAY_OF_MONTH, d)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Validações extras (caso OEM ignore min/max do DatePicker)
            if (chosen.before(today)) {
                toast("Selecione uma data a partir de hoje.")
                return@setOnClickListener
            }
            if (chosen.after(max)) {
                toast("Você só pode agendar dentro de 30 dias.")
                return@setOnClickListener
            }

            // Salva no Firestore
            val uid = auth.currentUser?.uid ?: "anon"
            val doc = hashMapOf(
                "uid" to uid,
                "material" to material,
                "scheduledAt" to Timestamp(Date(chosen.timeInMillis)),
                "status" to "scheduled",
                "createdAt" to Timestamp.now()
            )

            db.collection("schedules")
                .add(doc)
                .addOnSuccessListener {
                    // Agenda lembrete 2h antes
                    scheduleReminder(material, chosen.timeInMillis, minutesBefore = 120)
                    toast("Agendado para ${d.toString().padStart(2,'0')}/${(m+1).toString().padStart(2,'0')}/$y às 09:00")
                    finish()
                }
                .addOnFailureListener { e ->
                    toast(e.localizedMessage ?: getString(R.string.error_generic))
                }
        }
    }

    private fun scheduleReminder(material: String, targetMs: Long, minutesBefore: Int) {
        val reminderAt = targetMs - minutesBefore * 60_000L
        val delay = (reminderAt - System.currentTimeMillis()).coerceAtLeast(5_000L)

        val data = workDataOf(
            "title" to "Lembrete de coleta",
            "text" to "Sua coleta de $material é hoje às 09:00.",
        )

        val req = OneTimeWorkRequestBuilder<CollectionReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(req)
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
