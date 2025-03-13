package com.example.imctrack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.osmdroid.util.GeoPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latestLocation: Location? = null
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var objectivesSection: LinearLayout
    private lateinit var addGoalButton: Button
    private val goalList = mutableListOf<String>() // Liste des objectifs

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                getLastLocation()
            } else {
                Log.e("MainActivity", "Location permission denied.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // Trouver la BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        setAppropriateTheme()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()


        // Gestion de la sélection dans la BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    // Ouvrir la carte (OpenStreetMaps)
                    val startPoint = latestLocation?.let {
                        GeoPoint(it.latitude, it.longitude)
                    } ?: GeoPoint(40.389683644051864, -3.627825356970311)

                    val intent = Intent(this, OpenStreetMapsActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("location", startPoint)
                    intent.putExtra("locationBundle", bundle)
                    startActivity(intent)
                    true
                }

                R.id.nav_next -> {
                    // Passer à la page suivante
                    val intent = Intent(this, Page2::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        // Récupérer l'utilisateur sélectionné depuis l'Intent
        val selectedUser = intent.getStringExtra("selectedUser")

        if (selectedUser != null) {
            val apiKey = getApiKey(selectedUser)

            // Afficher la clé API
            val apiKeyTextView: TextView = findViewById(R.id.api_key_text_view)
            if (apiKey.isNullOrEmpty()) {
                apiKeyTextView.text = "Clé API non trouvée"
            } else {
                apiKeyTextView.text = "Clé API: $apiKey"
            }
            apiKeyTextView.visibility = View.VISIBLE
        }

        objectivesSection = findViewById(R.id.objectives_container)
        addGoalButton = findViewById(R.id.add_goal_button)

        // Charger les objectifs enregistrés
        loadGoals()

        addGoalButton.setOnClickListener {
            showAddGoalDialog()
        }

    }


    private fun showAddGoalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_goal, null)
        val goalInput = dialogView.findViewById<EditText>(R.id.goal_input)

        AlertDialog.Builder(this)
            .setTitle("Ajouter un objectif")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { _, _ ->
                val goalText = goalInput.text.toString()
                if (goalText.isNotEmpty()) {
                    goalList.add(goalText)
                    saveGoals()
                    addGoalToList(goalText)
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun addGoalToList(goalText: String) {
        val goalLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 0) }
        }

        val goalTextView = TextView(this).apply {
            text = goalText
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val deleteButton = Button(this).apply {
            text = "✖"
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 0, 0, 0) }

            setOnClickListener {
                objectivesSection.removeView(goalLayout)
                goalList.remove(goalText)
                saveGoals()
            }
        }

        goalLayout.addView(goalTextView)
        goalLayout.addView(deleteButton)
        objectivesSection.addView(goalLayout)
    }

    private fun saveGoals() {
        sharedPreferencesHelper.saveGoals(goalList)
    }

    private fun loadGoals() {
        goalList.clear()
        goalList.addAll(sharedPreferencesHelper.getGoals())

        for (goal in goalList) {
            addGoalToList(goal)
        }
    }




    private fun getApiKey(userName: String): String? {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("API_KEY_$userName", null)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                toggleTheme()
                true
            }

            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAppropriateTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun toggleTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        sharedPreferencesHelper.saveTheme(!isDarkMode)
        setAppropriateTheme()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val themeItem = menu?.findItem(R.id.action_theme)
        val isDarkMode = sharedPreferencesHelper.getTheme()

        themeItem?.setIcon(
            if (isDarkMode) R.drawable.ic_sun else R.drawable.ic_moon
        )

        return super.onPrepareOptionsMenu(menu)
    }


    private fun requestLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation()
            }

            else -> {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener(this) { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                latestLocation = task.result
                Log.d(
                    "MainActivity",
                    "Location retrieved: ${latestLocation?.latitude}, ${latestLocation?.longitude}"
                )
            } else {
                Log.e("MainActivity", "Failed to get location.")
            }
        }
    }

}