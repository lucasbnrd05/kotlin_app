package com.example.imctrack

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.imctrack.data.local.AppDatabase
import com.example.imctrack.data.local.CoordinatesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.graphics.drawable.BitmapDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import com.google.android.material.bottomnavigation.BottomNavigationView

class OpenStreetMapsActivity : AppCompatActivity() {

    private val TAG = "OpenStreetMapsActivity"
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_street_map)

        Configuration.getInstance().userAgentValue = "com.example.imctrack"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        val bundle = intent.getBundleExtra("locationBundle")
        var geoPoint = bundle?.getParcelable<GeoPoint>("location")

        if (geoPoint == null) {
            Log.e(TAG, "GeoPoint is null!")
            geoPoint = GeoPoint(40.389683644051864, -3.627825356970311)  // Si null, utiliser Paris par défaut
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(19.0)
        map.controller.setCenter(geoPoint)

        // Ajoute un marqueur à la position actuelle (hardcodée)
        addCustomMarker(geoPoint, "My Current Location", R.drawable.ic_here)


        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(this@OpenStreetMapsActivity)
                val lieuDao = db.coordinatesDao()
                val locations = withContext(Dispatchers.IO) { lieuDao.getAll() }

                val geoPoints = mutableListOf<GeoPoint>()
                locations.forEach { location ->
                    val point = GeoPoint(location.latitude, location.longitude)
                    geoPoints.add(point)
                    addLocationMarker(map, point, location.name, this@OpenStreetMapsActivity)
                }
                Log.d(TAG, "Markers added from database")
                addRouteMarkers(map, geoPoints,this@OpenStreetMapsActivity)

            } catch (e: Exception) {
                Log.e(TAG, "Error loading locations from database", e)
            }
        }

        // Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_menu)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_back -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_zoom_in -> {
                    map.controller.zoomIn() // Zoom avant
                    true
                }
                R.id.action_zoom_out -> {
                    map.controller.zoomOut() // Zoom arrière
                    true
                }
                else -> false
            }
        }

    }

    private fun addCustomMarker(position: GeoPoint, title: String, iconResId: Int) {
        val marker = Marker(map)
        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = ContextCompat.getDrawable(this, iconResId)
        marker.title = title

        marker.infoWindow = BasicInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, map)

        map.overlays.add(marker)
    }

    private fun addLocationMarker(map: MapView, position: GeoPoint, name: String, context: Context) {
        val marker = Marker(map)
        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_run)
        marker.title = name
        marker.infoWindow = BasicInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, map)
        map.overlays.add(marker)
    }

    fun addRouteMarkers(map: MapView, coords: List<GeoPoint>, context: Context) {
        val polyline = Polyline()
        polyline.setPoints(coords)
        map.overlays.add(polyline)
    }
}