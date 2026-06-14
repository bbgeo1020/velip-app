package com.VelibApp.velib

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.VelibApp.velib.data.model.Station
import com.VelibApp.velib.databinding.ActivityMainBinding
import com.VelibApp.velib.ui.FavoritesActivity
import com.VelibApp.velib.ui.MapViewModel
import com.VelibApp.velib.ui.NearbyBottomSheet
import com.VelibApp.velib.ui.StationBottomSheet
import com.VelibApp.velib.ui.StationsUiState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private val viewModel: MapViewModel by viewModels()
    private val markerStationMap = mutableMapOf<Marker, Station>()
    private var allStations = listOf<Station>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.loadStations()

        binding.btnFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        binding.btnNearby.setOnClickListener {
            getUserLocation { lat, lon ->
                NearbyBottomSheet(allStations, lat, lon)
                    .show(supportFragmentManager, "nearby")
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val paris = LatLng(48.8566, 2.3522)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 12f))

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is StationsUiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is StationsUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    allStations = state.stations
                    markerStationMap.clear()
                    googleMap.clear()
                    state.stations.forEach { station ->
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(station.lat, station.lon))
                                .title(station.name)
                        )
                        if (marker != null) markerStationMap[marker] = station
                    }
                }
                is StationsUiState.Error -> binding.progressBar.visibility = View.GONE
            }
        }

        googleMap.setOnMarkerClickListener { marker ->
            markerStationMap[marker]?.let { station ->
                StationBottomSheet(station).show(supportFragmentManager, "station_detail")
            }
            true
        }
    }

    private fun getUserLocation(onLocation: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocation(location.latitude, location.longitude)
                }
            }
    }
}