package com.VelibApp.velib.data.repository


import com.VelibApp.velib.data.model.Station
import com.VelibApp.velib.data.network.RetrofitClient

class VelibRepository {
    suspend fun getAllStations(): List<Station> {
        val infoResponse = RetrofitClient.api.getStationInformation()
        val statusResponse = RetrofitClient.api.getStationStatus()

        val statusMap = statusResponse.data.stations.associateBy { it.station_id }

        return infoResponse.data.stations.mapNotNull { info ->
            val status = statusMap[info.station_id] ?: return@mapNotNull null
            Station(
                id = info.station_id.toString(),
                name = info.name,
                lat = info.lat,
                lon = info.lon,
                capacity = info.capacity,
                bikesAvailable = status.num_bikes_available,
                docksAvailable = status.num_docks_available
            )
        }
    }
}