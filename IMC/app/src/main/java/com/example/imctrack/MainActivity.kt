package com.example.imctrack

import SharedPreferencesHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.osmdroid.util.GeoPoint

import android.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latestLocation: Location? = null

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            getLastLocation()
        } else {
            Log.e("MainActivity", "Location permission denied.")
        }
    }
    private lateinit var userIdentifierButton: Button
    private lateinit var newUserEditText: EditText
    private lateinit var saveUserButton: Button
    private lateinit var userIdentifierSpinner: Spinner

    // Liste pour stocker les utilisateurs (ou tu peux utiliser un fichier)
    private val usersList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        setAppropriateTheme()


        val buttonNext: Button = findViewById(R.id.button)
        buttonNext.setOnClickListener {
            startActivity(Intent(this, Page2::class.java))
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()

        val buttonOsm: Button = findViewById(R.id.buttonmap)
        buttonOsm.setOnClickListener {
            val startPoint = if (latestLocation != null) {
                GeoPoint(latestLocation!!.latitude, latestLocation!!.longitude)
            } else {
                Log.d("MainActivity", "Location is null, using default coordinates (Paris).")
                GeoPoint(40.389683644051864, -3.627825356970311)  // Paris
            }

            val intent = Intent(this, OpenStreetMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("location", startPoint)
            intent.putExtra("locationBundle", bundle)
            startActivity(intent)
        }

        userIdentifierButton = findViewById(R.id.userIdentifierButton)
        newUserEditText = findViewById(R.id.newUserEditText)
        saveUserButton = findViewById(R.id.saveUserButton)
        userIdentifierSpinner = findViewById(R.id.userIdentifierSpinner)

        // On écoute le clic du bouton "Enter User ID"
        userIdentifierButton.setOnClickListener {
            // Afficher l'EditText et le bouton "Save User"
            newUserEditText.visibility = View.VISIBLE
            saveUserButton.visibility = View.VISIBLE
        }

        // Gestion du clic sur le bouton "Save User"
        saveUserButton.setOnClickListener {
            val newUserName = newUserEditText.text.toString().trim()

            if (newUserName.isNotBlank()) {
                // Ajouter l'utilisateur à la liste
                usersList.add(newUserName)

                // Sauvegarder dans un fichier si nécessaire (je vais utiliser SharedPreferences comme exemple)
                saveUserToFile(newUserName)

                // Mettre à jour le Spinner avec la liste des utilisateurs
                updateUserSpinner()

                // Réinitialiser le champ de texte et cacher l'EditText
                newUserEditText.text.clear()
                newUserEditText.visibility = View.GONE
                saveUserButton.visibility = View.GONE

                Toast.makeText(this, "User saved: $newUserName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonTheme: Button = findViewById(R.id.T) // Le bouton pour changer le thème
        buttonTheme.setOnClickListener {
            toggleTheme()
        }

    }
    // Méthode pour enregistrer l'utilisateur dans un fichier (SharedPreferences dans cet exemple)
    private fun saveUserToFile(userName: String) {
        val sharedPref = getSharedPreferences("users", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val currentUsers = sharedPref.getStringSet("user_list", mutableSetOf()) ?: mutableSetOf()
        currentUsers.add(userName)
        editor.putStringSet("user_list", currentUsers)
        editor.apply()
    }

    // Méthode pour mettre à jour le Spinner avec les utilisateurs enregistrés
    private fun updateUserSpinner() {
        val sharedPref = getSharedPreferences("users", Context.MODE_PRIVATE)
        val usersSet = sharedPref.getStringSet("user_list", mutableSetOf()) ?: mutableSetOf()

        val usersList = usersSet.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usersList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userIdentifierSpinner.adapter = adapter
    }

    // Si tu veux charger les utilisateurs au démarrage de l'activité
    override fun onResume() {
        super.onResume()
        updateUserSpinner()
    }

    private fun requestLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
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
                Log.d("MainActivity", "Location retrieved: ${latestLocation?.latitude}, ${latestLocation?.longitude}")
            } else {
                Log.e("MainActivity", "Failed to get location.")
            }
        }
    }

    private fun setAppropriateTheme() {
        // Appliquer le thème sauvegardé (clair ou sombre)
        val isDarkMode = sharedPreferencesHelper.getTheme()
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun toggleTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        // Inverser le thème
        sharedPreferencesHelper.saveTheme(!isDarkMode)
        setAppropriateTheme()  // Appliquer le nouveau thème
    }




}
