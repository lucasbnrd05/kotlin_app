package com.example.imctrack.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Parcelize
@Entity(tableName = "coordinates")
data class CoordinatesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val latitude: Double,
    val longitude: Double
) : Parcelable