package com.VelibApp.velib.data.network


import com.VelibApp.velib.data.model.StationInformationResponse
import com.VelibApp.velib.data.model.StationStatusResponse
import retrofit2.http.GET

interface VelibApiService {
    @GET("station_information.json")
    suspend fun getStationInformation(): StationInformationResponse

    @GET("station_status.json")
    suspend fun getStationStatus(): StationStatusResponse
}