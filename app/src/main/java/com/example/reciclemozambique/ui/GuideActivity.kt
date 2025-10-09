package com.example.reciclemozambique.ui

import android.Manifest
import android.os.Bundle
import android.content.Intent
import android.location.Location
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityGuideBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import androidx.work.*
import java.util.concurrent.TimeUnit

class GuideActivity : BaseBottomActivity() {
    private lateinit var binding: ActivityGuideBinding
    override val bottomNav get() = binding.bottomNavigation
    override val selectedItemId = R.id.nav_guide

    private lateinit var fused: FusedLocationProviderClient
    private var countdown: android.os.CountDownTimer? = null

    private data class CollectionPoint(
        val name: String, val lat: Double, val lng: Double,
        val weekday: Int, val hour: Int, val minute: Int
    )

    // MOCK: troque por dados do Firestore quando quiser
    private val points = listOf(
        CollectionPoint("EcoPonto Central", -25.9650, 32.5800, Calendar.TUESDAY, 10, 0),
        CollectionPoint("Recicla Mais",    -25.9700, 32.5600, Calendar.FRIDAY,  9, 30)
    )

    // imagens existentes
    private val imageItemUrl = "https://picsum.photos/seed/item/400/300"
    private val tip1Url = "https://picsum.photos/seed/t1/300/300"
    private val tip2Url = "https://picsum.photos/seed/t2/300/300"
    private val tip3Url = "https://picsum.photos/seed/t3/300/300"

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { g ->
        val ok = g[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                g[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (ok) loadNearest() else toast("Permita a localização para recomendar o ponto mais próximo")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wireBottomNav()
        fused = LocationServices.getFusedLocationProviderClient(this)

        loadImages()
        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonInstructions.setOnClickListener { toast("Abrindo instruções…") }

        binding.buttonNotify.setOnClickListener {
            nextPickupTime()?.let { t -> scheduleReminder(t, 30) }
        }

        requestLocation()
    }

    private fun requestLocation() {
        permLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun loadNearest() {
        fused.lastLocation
            .addOnSuccessListener { loc ->
                val location = loc ?: return@addOnSuccessListener
                val nearest = points.minBy { distanceMeters(location, it) }
                val distKm = distanceMeters(location, nearest) / 1000.0
                binding.textNearestName.text = nearest.name
                binding.textNearestDistance.text = String.format(Locale.getDefault(), "%.1f km", distKm)

                val next = nextPickupTime(nearest)!!
                val df = SimpleDateFormat("EEE, HH:mm", Locale.getDefault())
                binding.textNextPickup.text = "Próxima coleta: ${df.format(Date(next))}"
                startCountdown(next)
            }
            .addOnFailureListener { toast(it.localizedMessage) }
    }

    private fun nextPickupTime(cp: CollectionPoint? = null): Long? {
        val p = cp ?: points.firstOrNull() ?: return null
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, p.hour)
            set(Calendar.MINUTE, p.minute)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            while (get(Calendar.DAY_OF_WEEK) != p.weekday || timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, p.hour); set(Calendar.MINUTE, p.minute)
            }
        }
        return target.timeInMillis
    }

    private fun startCountdown(targetMs: Long) {
        countdown?.cancel()
        val remaining = (targetMs - System.currentTimeMillis()).coerceAtLeast(0)
        countdown = object : android.os.CountDownTimer(remaining, 1000) {
            override fun onTick(ms: Long) {
                val h = ms / 3600000
                val m = (ms % 3600000) / 60000
                val s = (ms % 60000) / 1000
                binding.textCountdown.text = String.format(Locale.getDefault(), "Faltam %02d:%02d:%02d", h, m, s)
            }
            override fun onFinish() { binding.textCountdown.text = "Coleta agora" }
        }.start()
    }

    private fun scheduleReminder(targetMs: Long, minutesBefore: Int) {
        val delay = (targetMs - System.currentTimeMillis() - minutesBefore * 60_000L).coerceAtLeast(5_000L)
        val data = workDataOf(
            "title" to "Coleta de resíduos",
            "text" to "A coleta começará em $minutesBefore minutos."
        )
        val req = OneTimeWorkRequestBuilder<CollectionReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(req)
        toast("Lembrete agendado")
    }

    private fun distanceMeters(loc: Location, p: CollectionPoint): Float {
        val res = FloatArray(1)
        Location.distanceBetween(loc.latitude, loc.longitude, p.lat, p.lng, res)
        return res[0]
    }

    private fun loadImages() {
        Glide.with(this).load(imageItemUrl).placeholder(R.drawable.ic_placeholder).into(binding.imageItem)
        Glide.with(this).load(tip1Url).placeholder(R.drawable.ic_placeholder).into(binding.imageTip1)
        Glide.with(this).load(tip2Url).placeholder(R.drawable.ic_placeholder).into(binding.imageTip2)
        Glide.with(this).load(tip3Url).placeholder(R.drawable.ic_placeholder).into(binding.imageTip3)
    }

    private fun toast(msg: String?) =
        Toast.makeText(this, msg ?: getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
}
