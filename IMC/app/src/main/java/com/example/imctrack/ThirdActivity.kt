package com.example.imctrack

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        // Récupérer les données passées par l'Intent
        val latitude = intent.getStringExtra("LATITUDE")
        val longitude = intent.getStringExtra("LONGITUDE")

        // Initialiser les TextViews
        val latitudeTextView: TextView = findViewById(R.id.tvLatitude)
        val longitudeTextView: TextView = findViewById(R.id.tvLongitude)

        // Afficher les données dans les TextViews
        latitudeTextView.text = "Latitude: $latitude"
        longitudeTextView.text = "Longitude: $longitude"
    }
}
