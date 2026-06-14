package com.VelibApp.velib.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.VelibApp.velib.R
import com.VelibApp.velib.data.model.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private lateinit var progressBar: ProgressBar
    private lateinit var etSearch: EditText

    // Listes pour garder une référence sur nos données et nos marqueurs
    private var allStations: List<Station> = emptyList()
    private val markerStationMap = HashMap<Marker, Station>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        etSearch = findViewById(R.id.etSearch)

        // Initialiser la carte Google Maps
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Bouton vers l'activité des Favoris
        findViewById<View>(R.id.btnFavorites).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Bouton pour les stations proches (Coordonnées par défaut sur Paris Centre pour test)
        findViewById<View>(R.id.btnNearby).setOnClickListener {
            if (allStations.isNotEmpty()) {
                val ParisLat = 48.8566
                val ParisLon = 2.3522
                val bottomSheet = NearbyBottomSheet(allStations, ParisLat, ParisLon)
                bottomSheet.show(supportFragmentManager, "NearbyBottomSheet")
            } else {
                Toast.makeText(this, "Aucune donnée de station disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Centrer la caméra par défaut sur Paris
        val paris = LatLng(48.8566, 2.3522)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 12f))

        // Événement lors du clic sur une fenêtre d'information d'un marqueur
        googleMap.setOnInfoWindowClickListener { marker ->
            val station = markerStationMap[marker]
            if (station != null) {
                val bottomSheet = StationBottomSheet(station)
                bottomSheet.show(supportFragmentManager, "StationBottomSheet")
            }
        }

        // Configuration de l'observateur sur le ViewModel (Données Réseau)
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is StationsUiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is StationsUiState.Success -> {
                    progressBar.visibility = View.GONE
                    allStations = state.stations
                    // Mettre à jour l'affichage selon le texte actuel dans la barre de recherche
                    filterAndDisplayStations(etSearch.text.toString())
                }
                is StationsUiState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Erreur : ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Lancement des requêtes et du rafraîchissement automatique (60s)
        viewModel.loadStations()
        viewModel.startAutoRefresh()

        // Configuration de la barre de recherche (Recherche textuelle dynamique)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplayStations(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Filtre les marqueurs de stations sur la carte selon la saisie de l'utilisateur
     */
    private fun filterAndDisplayStations(query: String) {
        if (!::googleMap.isInitialized) return

        googleMap.clear()
        markerStationMap.clear()

        val lowerQuery = query.lowercase().trim()
        val filtered = allStations.filter { it.name.lowercase().contains(lowerQuery) }

        filtered.forEach { station ->
            // Fonctionnalité Bonus : Choix de la couleur selon la disponibilité
            val color = when {
                station.bikesAvailable >= 5 -> BitmapDescriptorFactory.HUE_GREEN
                station.bikesAvailable > 0  -> BitmapDescriptorFactory.HUE_ORANGE
                else                        -> BitmapDescriptorFactory.HUE_RED
            }

            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(station.lat, station.lon))
                    .title(station.name)
                    .snippet("🚲 ${station.bikesAvailable} vélos | 🅿️ ${station.docksAvailable} d样cks")
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            )

            if (marker != null) {
                markerStationMap[marker] = station
            }
        }

        // Si l'utilisateur trouve un résultat unique et précis, la caméra zoome dessus automatiquement
        if (filtered.size == 1) {
            val uniqueStation = filtered[0]
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(uniqueStation.lat, uniqueStation.lon), 15f
                )
            )
        }
    }
}