package com.example.imctrack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.osmdroid.util.GeoPoint

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latestLocation: Location? = null
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userIdentifierButton: Button
    private lateinit var saveUserButton: Button
    private lateinit var buttonTheme: Button
    private lateinit var deleteUsersButton: Button
    private lateinit var newUserEditText: EditText
    private lateinit var userIdentifierSpinner: Spinner
    private var usersList = mutableListOf<String>()

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
            val startPoint = latestLocation?.let {
                GeoPoint(it.latitude, it.longitude)
            } ?: GeoPoint(40.389683644051864, -3.627825356970311)

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
        buttonTheme = findViewById(R.id.T)
        deleteUsersButton = findViewById(R.id.deleteUsersButton)

        userIdentifierButton.setOnClickListener {
            newUserEditText.visibility = View.VISIBLE
            saveUserButton.visibility = View.VISIBLE
        }

        saveUserButton.setOnClickListener {
            val newUserName = newUserEditText.text.toString().trim()
            if (newUserName.isNotEmpty()) {
                sharedPreferencesHelper.addUser(newUserName)
                updateUserSpinner()
                newUserEditText.text.clear()
                newUserEditText.visibility = View.GONE
                saveUserButton.visibility = View.GONE
                Toast.makeText(this, "User saved: $newUserName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        deleteUsersButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        buttonTheme.setOnClickListener {
            toggleTheme()
        }
    }

    private fun updateUserSpinner() {
        val usersSet = sharedPreferencesHelper.getUsers()
        usersList = usersSet.toMutableList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usersList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userIdentifierSpinner.adapter = adapter
    }

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
        val isDarkMode = sharedPreferencesHelper.getTheme()
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun toggleTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        sharedPreferencesHelper.saveTheme(!isDarkMode)
        setAppropriateTheme()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete all users")
            .setMessage("Are you sure you want to delete all users?")
            .setPositiveButton("Yes") { _, _ ->
                sharedPreferencesHelper.clearUsers()
                updateUserSpinner()
                Toast.makeText(this, "All users deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
