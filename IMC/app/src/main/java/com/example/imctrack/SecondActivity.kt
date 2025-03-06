package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Liste des lieux avec leurs coordonnées
        val locations = listOf(
            "Tennis" to "40.38779608214728, -3.627687914352839",
            "Futsal outdoors" to "40.38788595319803, -3.627048250272035",
            "Fashion and design school" to "40.3887315224542, -3.628643539758645",
            "Topography school" to "40.38926842612264, -3.630067893975619",
            "Telecommunications school" to "40.38956358584258, -3.629046081389352",
            "ETSISI" to "40.38992125672989, -3.6281366497769714",
            "Library" to "40.39037466191718, -3.6270256763598447",
            "CITSEM" to "40.389855884803005, -3.626782180787362"
        )

        // Obtenez le TableLayout
        val tableLayout: TableLayout = findViewById(R.id.tlCoordinates)

        // Ajouter dynamiquement les lignes au tableau
        locations.forEach { (name, coords) ->
            val (latitude, longitude) = coords.split(", ")

            // Crée une nouvelle ligne de tableau
            val tableRow = TableRow(this)

            // Crée et ajoute les TextViews pour chaque cellule
            val dateTextView = TextView(this)
            dateTextView.text = "05/03/2025"  // Remplacez par la date dynamique si nécessaire
            dateTextView.setPadding(16, 16, 16, 16)
            dateTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            tableRow.addView(dateTextView)

            val nameTextView = TextView(this)
            nameTextView.text = name
            nameTextView.setPadding(16, 16, 16, 16)
            nameTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            tableRow.addView(nameTextView)

            val latitudeTextView = TextView(this)
            latitudeTextView.text = latitude
            latitudeTextView.setPadding(16, 16, 16, 16)
            latitudeTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            tableRow.addView(latitudeTextView)

            val longitudeTextView = TextView(this)
            longitudeTextView.text = longitude
            longitudeTextView.setPadding(16, 16, 16, 16)
            longitudeTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            tableRow.addView(longitudeTextView)

            // Ajouter la ligne au TableLayout
            tableLayout.addView(tableRow)

            // Ajouter un listener de clic à chaque ligne
            tableRow.setOnClickListener {
                // Passer les données (latitude, longitude) à ThirdActivity
                val intent = Intent(this, ThirdActivity::class.java)
                intent.putExtra("LATITUDE", latitude)
                intent.putExtra("LONGITUDE", longitude)
                startActivity(intent)
            }
        }

    }
}




// Classe pour stocker les données des lieux
data class LocationData(
    val date: String,
    val name: String,        // Nom du lieu
    val latitude: String,
    val longitude: String
)