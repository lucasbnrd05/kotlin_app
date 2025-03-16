package com.example.imctrack.data.local

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LieuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LieuRepository

    init {
        val coordinatesDao = AppDatabase.getDatabase(application).coordinatesDao()
        repository = LieuRepository(coordinatesDao)
    }

    fun insertLieu(name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLieu(CoordinatesEntity(name = name, latitude = latitude, longitude = longitude))
        }
    }

    fun getLieux(callback: (List<CoordinatesEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val lieux = repository.getLieux()
            callback(lieux)
        }
    }

    fun updateLieu(coordinates: CoordinatesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLieu(coordinates)
        }
    }

    fun deleteLieu(lieu: CoordinatesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLieu(lieu)
        }
    }
}