package com.VelibApp.velib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.VelibApp.velib.R
import com.VelibApp.velib.data.local.AppDatabase
import com.VelibApp.velib.data.model.FavoriteStation
import com.VelibApp.velib.data.model.Station
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class StationBottomSheet(private val station: Station) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_station, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvStationName).text = station.name
        view.findViewById<TextView>(R.id.tvBikes).text = station.bikesAvailable.toString()
        view.findViewById<TextView>(R.id.tvDocks).text = station.docksAvailable.toString()

        val btnFavorite = view.findViewById<Button>(R.id.btnFavorite)
        val dao = AppDatabase.getInstance(requireContext()).favoriteDao()

        // Vérifier si déjà en favoris
        lifecycleScope.launch {
            val isFav = dao.isFavorite(station.id)
            btnFavorite.text = if (isFav) "❌ Retirer des favoris" else "⭐ Ajouter aux favoris"
        }

        btnFavorite.setOnClickListener {
            lifecycleScope.launch {
                val isFav = dao.isFavorite(station.id)
                if (isFav) {
                    dao.delete(FavoriteStation(
                        stationId = station.id,
                        name = station.name,
                        lat = station.lat,
                        lon = station.lon,
                        capacity = station.capacity
                    ))
                    btnFavorite.text = "⭐ Ajouter aux favoris"
                    Toast.makeText(requireContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show()
                } else {
                    dao.insert(FavoriteStation(
                        stationId = station.id,
                        name = station.name,
                        lat = station.lat,
                        lon = station.lon,
                        capacity = station.capacity
                    ))
                    btnFavorite.text = "❌ Retirer des favoris"
                    Toast.makeText(requireContext(), "Ajouté aux favoris !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}