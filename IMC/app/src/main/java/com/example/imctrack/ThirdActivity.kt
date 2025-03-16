package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.imctrack.data.local.AppDatabase
import com.example.imctrack.data.local.CoordinatesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ThirdActivity : AppCompatActivity() {

    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var buttonUpdate: Button
    private lateinit var buttonDelete: Button
    private lateinit var tvName: TextView // TextView pour le nom du lieu

    private var location: CoordinatesEntity? = null // Variable pour stocker l'objet CoordinatesEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        // Initialisation des vues
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonDelete = findViewById(R.id.buttonDelete)
        tvName = findViewById(R.id.tvName) // Initialisation du TextView pour le nom

        // Configurer la navigation en bas
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_third)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

        // Récupérer les données de l'Intent
        handleIntentData(intent)

        // Configurer le bouton pour mettre à jour les coordonnées
        buttonUpdate.setOnClickListener {
            updateCoordinates()
        }

        // Configurer le bouton pour supprimer les coordonnées
        buttonDelete.setOnClickListener {
            deleteCoordinates()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentData(intent)
        setIntent(intent)
    }

    private fun handleIntentData(intent: Intent) {
        intent?.let {
            location = it.getParcelableExtra<CoordinatesEntity>("LOCATION")
            if (location != null) {
                // Afficher les données dans les TextViews et EditTexts
                tvName.text = "Name: ${location?.name}" // Afficher le nom du lieu
                tvLatitude.text = "Latitude: ${location?.latitude}"
                tvLongitude.text = "Longitude: ${location?.longitude}"
                etLatitude.setText(location?.latitude.toString())
                etLongitude.setText(location?.longitude.toString())
            } else {
                Log.e("ThirdActivity", "Location data is null")
                Toast.makeText(this, "Location data is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCoordinates() {
        location?.let { currentLocation ->
            lifecycleScope.launch {
                try {
                    val newLatitude = etLatitude.text.toString().toDouble()
                    val newLongitude = etLongitude.text.toString().toDouble()

                    val db = AppDatabase.getDatabase(applicationContext)
                    val lieuDao = db.coordinatesDao()

                    // Mise à jour des valeurs de l'objet Location
                    val updatedLocation = currentLocation.copy(latitude = newLatitude, longitude = newLongitude)

                    // Mise à jour dans la base de données
                    withContext(Dispatchers.IO) {
                        lieuDao.updateCoordinate(updatedLocation)
                    }

                    // Mettre à jour l'affichage
                    tvLatitude.text = "Latitude: $newLatitude"
                    tvLongitude.text = "Longitude: $newLongitude"
                    Toast.makeText(this@ThirdActivity, "Coordinates updated", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Log.e("ThirdActivity", "Error updating coordinates: ${e.message}", e)
                }
            }
        } ?: run {
            Toast.makeText(this, "No location to update", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCoordinates() {
        location?.let { currentLocation ->
            lifecycleScope.launch {
                try {
                    val db = AppDatabase.getDatabase(applicationContext)
                    val lieuDao = db.coordinatesDao()

                    withContext(Dispatchers.IO) {
                        lieuDao.delete(currentLocation)
                    }

                    // Effacer les EditTexts et les TextViews
                    etLatitude.text.clear()
                    etLongitude.text.clear()
                    tvLatitude.text = "Latitude: "
                    tvLongitude.text = "Longitude: "
                    tvName.text = "Name: " // Effacer le nom du lieu
                    Toast.makeText(this@ThirdActivity, "Coordinates deleted", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("ThirdActivity", "Error deleting coordinates: ${e.message}", e)
                }
            }
        } ?: run {
            Toast.makeText(this, "No location to delete", Toast.LENGTH_SHORT).show()
        }
    }
}