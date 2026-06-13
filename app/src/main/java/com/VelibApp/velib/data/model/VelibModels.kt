package com.VelibApp.velib.data.model


data class StationInformationResponse(val data: StationInformationData)
data class StationInformationData(val stations: List<StationInformation>)
data class StationInformation(
    val station_id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int
)

data class StationStatusResponse(val data: StationStatusData)
data class StationStatusData(val stations: List<StationStatus>)
data class StationStatus(
    val station_id: String,
    val num_bikes_available: Int,
    val num_docks_available: Int,
    val is_renting: Int,
    val is_returning: Int,
    val last_reported: Long
)

data class Station(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val bikesAvailable: Int,
    val docksAvailable: Int
)