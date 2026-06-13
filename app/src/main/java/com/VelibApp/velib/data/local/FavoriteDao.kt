package com.VelibApp.velib.data.local

import androidx.room.*
import com.VelibApp.velib.data.model.FavoriteStation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteStation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: FavoriteStation)

    @Delete
    suspend fun delete(station: FavoriteStation)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE stationId = :id)")
    suspend fun isFavorite(id: String): Boolean
}