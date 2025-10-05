package com.example.reciclemozambique.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciclemozambique.R
import com.example.reciclemozambique.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Mapa
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Bottom Sheet
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Listeners
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.editSearch.setOnClickListener {
            Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show()
        }

        binding.buttonZoomIn.setOnClickListener {
            if (::mMap.isInitialized) mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }

        binding.buttonZoomOut.setOnClickListener {
            if (::mMap.isInitialized) mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }

        binding.buttonLocation.setOnClickListener {
            Toast.makeText(this, "Centering on your location", Toast.LENGTH_SHORT).show()
        }

        binding.buttonFilterAll.setOnClickListener {
            Toast.makeText(this, "Filter: All", Toast.LENGTH_SHORT).show()
        }

        binding.buttonFilterPlastic.setOnClickListener {
            Toast.makeText(this, "Filter: Plastic", Toast.LENGTH_SHORT).show()
        }

        binding.buttonFilterGlass.setOnClickListener {
            Toast.makeText(this, "Filter: Glass", Toast.LENGTH_SHORT).show()
        }
    }

    //  Método obrigatório da interface OnMapReadyCallback
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Exemplo: centralizar em Maputo
        val startLocation = LatLng(-25.9692, 32.5732)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 12f))
    }


}
