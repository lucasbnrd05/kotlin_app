package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.imctrack.data.local.AppDatabase
import com.example.imctrack.data.local.CoordinatesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.Gravity
import android.widget.LinearLayout

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Configurer la Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_third)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Naviguer vers MainActivity
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

        // Obtenez le TableLayout
        val tableLayout: TableLayout = findViewById(R.id.tlCoordinates)

        // Afficher dynamiquement les lignes au tableau
        lifecycleScope.launch {
            try {
                Log.d("SecondActivity", "Starting data loading...")
                val db = AppDatabase.getDatabase(this@SecondActivity)
                Log.d("SecondActivity", "Database instance obtained")
                val lieuDao = db.coordinatesDao()
                Log.d("SecondActivity", "LieuDao instance obtained")
                val locations = withContext(Dispatchers.IO) { lieuDao.getAll() }
                Log.d("SecondActivity", "Number of locations: ${locations.size}")
                locations.forEach {
                    Log.d("SecondActivity", "Location: Name=${it.name}, Latitude=${it.latitude}, Longitude=${it.longitude}")
                }

                // Ajoute les en-têtes au tableau
                addTableHeader(tableLayout)

                // Ajouter dynamiquement les lignes au tableau
                locations.forEachIndexed { index, location ->
                    val tableRow = TableRow(this@SecondActivity)

                    // Crée et ajoute les TextViews pour chaque cellule
                    val nameTextView = createTextView(location.name)
                    val latitudeTextView = createTextView(location.latitude.toString())
                    val longitudeTextView = createTextView(location.longitude.toString())

                    tableRow.addView(nameTextView)
                    tableRow.addView(latitudeTextView)
                    tableRow.addView(longitudeTextView)

                    // Ajouter un listener de clic à chaque ligne
                    tableRow.setOnClickListener {
                        // Passe l'ID à ThirdActivity
                        val intent = Intent(this@SecondActivity, ThirdActivity::class.java)
                        intent.putExtra("LOCATION", location)  // Transmets l'objet CoordinatesEntity
                        // Réutilise l'instance de ThirdActivity si elle existe
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }

                    tableLayout.addView(tableRow)
                }
            } catch (e: Exception) {
                Log.e("SecondActivity", "Error loading data", e)
                Toast.makeText(this@SecondActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTableHeader(tableLayout: TableLayout) {
        // Ajoute les en-têtes au tableau
        val tableHeader = TableRow(this)

        val nameHeader = createHeaderTextView("Name")
        val latitudeHeader = createHeaderTextView("Latitude")
        val longitudeHeader = createHeaderTextView("Longitude")

        tableHeader.addView(nameHeader)
        tableHeader.addView(latitudeHeader)
        tableHeader.addView(longitudeHeader)

        tableLayout.addView(tableHeader)
    }

    private fun createTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(16, 16, 16, 16)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
        }
    }

    private fun createHeaderTextView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 18f
            setTypeface(android.graphics.Typeface.DEFAULT_BOLD)
            setPadding(16, 16, 16, 16)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
        }
    }
}