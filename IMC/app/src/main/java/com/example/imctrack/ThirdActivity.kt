package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        // Récupérer les données passées par l'Intent
        var latitude = intent.getStringExtra("LATITUDE")
        var longitude = intent.getStringExtra("LONGITUDE")

// Si les données sont nulles, tu peux afficher un message de débogage
        if (latitude == null || longitude == null) {

            latitude = 40.389683644051864.toString()
            longitude = (-3.627825356970311).toString()
        }

        // Initialiser les TextViews
        val latitudeTextView: TextView = findViewById(R.id.tvLatitude)
        val longitudeTextView: TextView = findViewById(R.id.tvLongitude)

        // Afficher les données dans les TextViews
        latitudeTextView.text = "Latitude: $latitude"
        longitudeTextView.text = "Longitude: $longitude"


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

    }
}
