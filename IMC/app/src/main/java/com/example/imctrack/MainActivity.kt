package com.example.imctrack

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

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latestLocation: Location? = null

    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
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
}
