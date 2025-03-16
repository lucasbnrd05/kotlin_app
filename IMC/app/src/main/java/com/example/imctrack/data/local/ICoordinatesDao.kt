package com.example.imctrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface ICoordinatesDao {
    @Insert
    suspend fun insert(coordinates: CoordinatesEntity)

    @Insert
    suspend fun insertAll(coordinates: List<CoordinatesEntity>)

    @Query("SELECT * FROM coordinates")
    suspend fun getAll(): List<CoordinatesEntity>

    @Query("SELECT COUNT(*) FROM coordinates")
    suspend fun getCount(): Int

    @Delete
    suspend fun delete(coordinates: CoordinatesEntity)

    @Update
    suspend fun updateCoordinate(coordinates: CoordinatesEntity)
}