package com.VelibApp.velib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.VelibApp.velib.R
import com.VelibApp.velib.data.model.Station
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.*

class NearbyBottomSheet(
    private val stations: List<Station>,
    private val userLat: Double,
    private val userLon: Double
) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_nearby, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seekBar = view.findViewById<SeekBar>(R.id.seekBarRadius)
        val tvRadius = view.findViewById<TextView>(R.id.tvRadius)
        val listView = view.findViewById<ListView>(R.id.listNearby)

        var radiusMeters = 500

        fun updateList() {
            val nearby = stations.filter { station ->
                distanceMeters(userLat, userLon, station.lat, station.lon) <= radiusMeters
            }.sortedBy { distanceMeters(userLat, userLon, it.lat, it.lon) }

            val items = nearby.map { station ->
                val dist = distanceMeters(userLat, userLon, station.lat, station.lon).toInt()
                "${station.name}\n🚲 ${station.bikesAvailable} vélos | ${dist}m"
            }
            listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
            tvRadius.text = "Périmètre : ${radiusMeters}m (${nearby.size} stations)"
        }

        seekBar.max = 1900
        seekBar.progress = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                radiusMeters = 100 + progress
                updateList()
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        updateList()
    }

    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}