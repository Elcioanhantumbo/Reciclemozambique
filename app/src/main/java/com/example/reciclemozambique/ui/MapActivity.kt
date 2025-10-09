// app/src/main/java/com/example/reciclemozambique/ui/MapActivity.kt
package com.example.reciclemozambique.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MapActivity : BaseBottomActivity() {

    private lateinit var binding: ActivityMapBinding
    override val bottomNav get() = binding.bottomNavigation
    override val selectedItemId = R.id.nav_map

    private lateinit var map: MapView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var navHidden = false

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { g ->
        val ok = g[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                g[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (ok) ensureLocationEnabled() else toast("Permita a localização para melhor experiência.")
    }

    private val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            enableMyLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // bottom nav compartilhado
        wireBottomNav()

        map = binding.osmMap
        map.setMultiTouchControls(true)
        val maputo = GeoPoint(-25.9692, 32.5732)
        map.controller.setZoom(12.0)
        map.controller.setCenter(maputo)

        map.overlays.add(Marker(map).apply {
            position = maputo
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Maputo"
            snippet = "Exemplo de ponto"
        })

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonZoomIn.setOnClickListener { map.controller.setZoom(map.zoomLevelDouble + 1) }
        binding.buttonZoomOut.setOnClickListener { map.controller.setZoom(map.zoomLevelDouble - 1) }
        binding.buttonLocation.setOnClickListener { centerOnMyLocation() }

        val gesture = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean { toggleBottomNav(); return false }
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, dx: Float, dy: Float): Boolean {
                hideBottomNav(); return false
            }
        })
        map.setOnTouchListener { _, ev -> gesture.onTouchEvent(ev) }

        setupSearchUi()
        requestLocation()
    }

    private fun requestLocation() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            ensureLocationEnabled()
        } else {
            permLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun ensureLocationEnabled() {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L).build()
        val settingsReq = LocationSettingsRequest.Builder()
            .addLocationRequest(req).setAlwaysShow(true).build()
        LocationServices.getSettingsClient(this).checkLocationSettings(settingsReq)
            .addOnSuccessListener { enableMyLocation() }
            .addOnFailureListener { ex ->
                if (ex is ResolvableApiException) {
                    val intent = IntentSenderRequest.Builder(ex.resolution).build()
                    locationSettingsLauncher.launch(intent)
                } else toast("Ative o GPS nas configurações do dispositivo.")
            }
    }

    private fun enableMyLocation() {
        if (myLocationOverlay != null) return
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map).apply {
            enableMyLocation()
        }
        map.overlays.add(myLocationOverlay)
    }

    private fun centerOnMyLocation() {
        myLocationOverlay?.myLocation?.let {
            map.controller.setCenter(it)
            map.controller.setZoom(16.0)
        } ?: run {
            toast("Aguardando localização…")
            myLocationOverlay?.enableFollowLocation()
        }
    }

    // --------- Busca (Nominatim) ---------
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private data class PlaceSuggestion(val label: String, val lat: Double, val lon: Double)
    private lateinit var suggestionsAdapter: ArrayAdapter<String>
    private val suggestions = mutableListOf<PlaceSuggestion>()

    private fun setupSearchUi() {
        suggestionsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        binding.editSearch.setAdapter(suggestionsAdapter)

        binding.editSearch.addTextChangedListener { text ->
            val q = text?.toString()?.trim().orEmpty()
            searchRunnable?.let { handler.removeCallbacks(it) }
            if (q.length < 3) return@addTextChangedListener
            searchRunnable = Runnable { fetchSuggestions(q, panToFirst = true) }
            handler.postDelayed(searchRunnable!!, 450)
        }

        binding.editSearch.setOnItemClickListener { _, _, idx, _ ->
            val sel = suggestions.getOrNull(idx) ?: return@setOnItemClickListener
            val p = GeoPoint(sel.lat, sel.lon)
            map.controller.setCenter(p)
            map.controller.setZoom(16.0)
            val mk = Marker(map).apply {
                position = p
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = sel.label
            }
            map.overlays.add(mk)
        }
    }

    private fun fetchSuggestions(query: String, panToFirst: Boolean) {
        Thread {
            try {
                val url = URL(
                    "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=5&q=" +
                            URLEncoder.encode(query, "UTF-8")
                )
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", packageName)
                    connectTimeout = 6000; readTimeout = 6000
                }
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                val arr = JSONArray(body)

                val out = mutableListOf<PlaceSuggestion>()
                val labels = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val name = (o.optString("display_name").ifBlank { o.optString("name") }).trim()
                    val lat = o.optString("lat").toDoubleOrNull() ?: continue
                    val lon = o.optString("lon").toDoubleOrNull() ?: continue
                    out += PlaceSuggestion(name, lat, lon)
                    labels += name
                }

                runOnUiThread {
                    suggestions.clear(); suggestions.addAll(out)
                    suggestionsAdapter.clear(); suggestionsAdapter.addAll(labels)
                    suggestionsAdapter.notifyDataSetChanged()
                    if (labels.isNotEmpty()) binding.editSearch.showDropDown()
                    if (panToFirst && out.isNotEmpty()) {
                        map.controller.setCenter(GeoPoint(out[0].lat, out[0].lon))
                    }
                }
            } catch (_: Exception) { }
        }.start()
    }

    // menu animado
    private fun toggleBottomNav() { if (navHidden) showBottomNav() else hideBottomNav() }
    private fun showBottomNav() {
        binding.bottomNavigation.animate().translationY(0f).setDuration(180).start()
        navHidden = false
    }
    private fun hideBottomNav() {
        binding.bottomNavigation.animate()
            .translationY(binding.bottomNavigation.height.toFloat())
            .setDuration(180).start()
        navHidden = true
    }

    override fun onResume() { super.onResume(); map.onResume() }
    override fun onPause() { map.onPause(); super.onPause() }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
