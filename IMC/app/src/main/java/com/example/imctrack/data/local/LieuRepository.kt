package com.example.imctrack.data.local

import com.example.imctrack.data.local.CoordinatesEntity
import com.example.imctrack.data.local.ICoordinatesDao

class LieuRepository(private val coordinatesDao: ICoordinatesDao) {

    suspend fun insertLieu(lieu: CoordinatesEntity) {
        coordinatesDao.insert(lieu)
    }

    suspend fun getLieux(): List<CoordinatesEntity> {
        return coordinatesDao.getAll()
    }

    suspend fun updateLieu(lieu: CoordinatesEntity) {
        coordinatesDao.updateCoordinate(lieu)
    }

    suspend fun deleteLieu(lieu: CoordinatesEntity) {
        coordinatesDao.delete(lieu)
    }
}