package com.example.imctrack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.imctrack.data.local.AppDatabase
import com.example.imctrack.data.local.CoordinatesEntity
import com.example.imctrack.data.local.ICoordinatesDao
import com.example.imctrack.data.local.LieuViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.osmdroid.util.GeoPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private val lieuViewModel: LieuViewModel by viewModels()
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

        // Initialiser la base de données Room dans un thread secondaire (arrière-plan)
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isDatabaseInitialized = sharedPreferences.getBoolean("isDatabaseInitialized", false)

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                Log.d("Database", "Database initialized")
                val lieuDao = db.coordinatesDao()

                // Insérer les données initiales si la base de données n'est pas initialisée
                if (!isDatabaseInitialized) {
                    lieuDao.getAll().forEach { lieuDao.delete(it) }
                    val initialData = listOf(
                        CoordinatesEntity(name = "Tennis", latitude = 40.38779608214728, longitude = -3.627687914352839),
                        CoordinatesEntity(name = "Futsal outdoors", latitude = 40.38788595319803, longitude = -3.627048250272035),
                        CoordinatesEntity(name = "Fashion and design", latitude = 40.3887315224542, longitude = -3.628643539758645),
                        CoordinatesEntity(name = "Topos", latitude = 40.38926842612264, longitude = -3.630067893975619),
                        CoordinatesEntity(name = "Teleco", latitude = 40.38956358584258, longitude = -3.629046081389352),
                        CoordinatesEntity(name = "ETSISI", latitude = 40.38992125672989, longitude = -3.6281366497769714),
                        CoordinatesEntity(name = "Library", latitude = 40.39037466191718, longitude = -3.6270256763598447),
                        CoordinatesEntity(name = "CITSEM", latitude = 40.389855884803005, longitude = -3.626782180787362)
                    )
                    lieuDao.insertAll(initialData)
                    Log.d("MainActivity", "Données initiales insérées")

                    // Mettre à jour la variable partagée
                    sharedPreferences.edit().putBoolean("isDatabaseInitialized", true).apply()
                }

                // Récupérer les lieux enregistrés (en option)
                val lieux = lieuDao.getAll()
                lieux.forEach {
                    Log.d("MainActivity", "Lieu récupéré: $it")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erreur lors de l'accès à la base de données : ${e.message}")
            }
        }

        // Gestion de la sélection dans la BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    // Ouvrir la carte (OpenStreetMaps)
                    val startPoint = latestLocation?.let {
                        GeoPoint(it.latitude, it.longitude)
                    } ?: GeoPoint(40.389669725, -3.627825356970311)

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

                R.id.info -> {
                    Log.d("MainActivity", "Info item clicked, starting SecondActivity...")
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    Log.d("MainActivity", "SecondActivity started.")
                    true
                }

                else -> false
            }
        }

        objectivesSection = findViewById(R.id.objectives_container)
        addGoalButton = findViewById(R.id.add_goal_button)

        // Charger les objectifs enregistrés
        loadGoals()

        addGoalButton.setOnClickListener {
            showAddGoalDialog()
        }

        // Ajouter un bouton pour mettre à jour les données
        val updateDataButton = Button(this).apply {
            text = "Update Data"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            setOnClickListener {
                lifecycleScope.launch {
                    //récupérer la clé Api
                    val apiKey = sharedPreferences.getString("API_KEY", "")
                    updateDataFromApi(apiKey)
                    Toast.makeText(this@MainActivity, "Data Updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
        objectivesSection.addView(updateDataButton)
    }


    interface ApiService {
        @GET("search.json?engine=google_maps")
        suspend fun getSearchResults(
            @Query("api_key") apiKey: String,
            @Query("q") q: String,
            @Query("ll") ll: String
        ): SerpApiResponse
    }

    data class SerpApiResponse(
        val local_results: List<LocalResult>?
    )

    data class LocalResult(
        val title: String?,
        val gps_coordinates: GpsCoordinates?
    )

    data class GpsCoordinates(
        val latitude: Double?,
        val longitude: Double?
    )

    // Ajoute cette fonction pour mettre à jour les données depuis l'API
    private suspend fun updateDataFromApi(apiKey: String?) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("MainActivity", "API Key is null or empty, cannot update database.")
            Toast.makeText(this, "API Key not found. Please enter it in Settings.", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("MainActivity", "API Key: $apiKey")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://serpapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        try {
            val ll = "@40.389669725,-3.62835390,14z" // Localisation hardcodée
            val q = "gym" // Requête de recherche

            Log.d("MainActivity", "Calling API...")
            val response = withContext(Dispatchers.IO) {
                apiService.getSearchResults(apiKey = apiKey, q = q, ll = ll)
            }
            Log.d("MainActivity", "API Response: $response")

            val db = AppDatabase.getDatabase(applicationContext)
            val lieuDao = db.coordinatesDao()

            val localResults = response.local_results
            Log.d("MainActivity", "Local Results: $localResults")
            if (localResults != null) {
                Log.d("MainActivity", "Number of local results: ${localResults.size}")

                // Supprimer toutes les anciennes données
                withContext(Dispatchers.IO) {
                    lieuDao.getAll().forEach { lieuDao.delete(it) }
                }

                // Insérer les nouvelles données
                for (result in localResults) {
                    if (result.gps_coordinates?.latitude != null && result.gps_coordinates.longitude != null) {
                        val newLocation = CoordinatesEntity(
                            name = result.title ?: "Nom inconnu", // Utiliser le nom de l'API
                            latitude = result.gps_coordinates.latitude,
                            longitude = result.gps_coordinates.longitude
                        )
                        Log.d("MainActivity", "Inserting: $newLocation")
                        try {
                            withContext(Dispatchers.IO) {
                                lieuDao.insert(newLocation)
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error inserting data into the database: ${e.message}", e)
                        }
                    } else {
                        Log.w("MainActivity", "Skipping result with null latitude or longitude: ${result.title}")
                    }
                }
                Log.d("MainActivity", "Database updated successfully.")
                Toast.makeText(this, "Database updated successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Log.w("MainActivity", "No local results found in the API response.")
                Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("MainActivity", "Error updating database: ${e.message}", e)
            Log.e("MainActivity", "Error updating database: ${e.stackTraceToString()}", e)
            Toast.makeText(this, "Error updating database", Toast.LENGTH_SHORT).show()
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

            R.id.action_logout -> {
                logoutUser()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
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