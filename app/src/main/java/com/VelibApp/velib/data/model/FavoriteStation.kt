package com.VelibApp.velib.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteStation(
    @PrimaryKey val stationId: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int
)