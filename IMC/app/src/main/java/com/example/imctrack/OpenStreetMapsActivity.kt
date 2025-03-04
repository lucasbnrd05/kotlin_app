package com.example.imctrack

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView


class OpenStreetMapsActivity : AppCompatActivity() {
    private val TAG = "OpenStreetMapsActivity"
    private lateinit var map: MapView

    val gymkhanaCoords = listOf(
        GeoPoint(40.38779608214728, -3.627687914352839), // Tennis
        GeoPoint(40.38788595319803, -3.627048250272035), // Futsal outdoors
        GeoPoint(40.3887315224542, -3.628643539758645), // Fashion and design
        GeoPoint(40.38926842612264, -3.630067893975619), // Topos
        GeoPoint(40.38956358584258, -3.629046081389352), // Teleco
        GeoPoint(40.38992125672989, -3.6281366497769714), // ETSISI
        GeoPoint(40.39037466191718, -3.6270256763598447), // Library
        GeoPoint(40.389855884803005, -3.626782180787362) // CITSEM
    )
    val gymkhanaNames = listOf(
        "Tennis",
        "Futsal outdoors",
        "Fashion and design school",
        "Topography school",
        "Telecommunications school",
        "ETSISI",
        "Library",
        "CITSEM"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_street_map)

        Configuration.getInstance().userAgentValue = "com.example.imctrack"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        val bundle = intent.getBundleExtra("locationBundle")
        var geoPoint = bundle?.getParcelable<GeoPoint>("location")

        if (geoPoint != null) {
            Log.d(TAG, "GeoPoint received: ${geoPoint.latitude}, ${geoPoint.longitude}")
        } else {
            Log.e(TAG, "GeoPoint is null!")
            geoPoint = GeoPoint(40.389683644051864, -3.627825356970311)  // Si null, utiliser Paris par défaut
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(19.0)
        map.controller.setCenter(geoPoint)

        /*val marker = Marker(map)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        //marker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass) as BitmapDrawable
        //val customIcon = ContextCompat.getDrawable(this, R.drawable.my_marker_icon)
        //marker.icon = customIcon
        //marker.title = "My current location"
        //map.overlays.add(marker)


        marker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_delete) as BitmapDrawable
        marker.title = "My current location"
        map.overlays.add(marker)*/

        addCustomMarker(geoPoint, "My Current Location", android.R.drawable.ic_delete)
        addGymkhanaMarkers(map, gymkhanaCoords, gymkhanaNames, this)
        addRouteMarkers(map, gymkhanaCoords, gymkhanaNames, this)


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
    fun addGymkhanaMarkers(map: MapView, coords: List<GeoPoint>, names: List<String>, context: Context) {
        for (i in coords.indices) {
            val marker = Marker(map)
            marker.position = coords[i]
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title = names[i]

            val textView = TextView(context).apply {
                text = names[i]
                setTextColor(Color.BLACK)
                setTypeface(null, Typeface.BOLD)
                textSize = 14f
                setPadding(10, 10, 10, 10)
                setBackgroundColor(Color.WHITE)
            }

            marker.infoWindow = BasicInfoWindow(org.osmdroid.library.R.layout.bonuspack_bubble, map)

            map.overlays.add(marker)
        }
    }

    fun addRouteMarkers(map: MapView, coords: List<GeoPoint>, names: List<String>, context: Context) {
        val polyline = Polyline()
        polyline.setPoints(coords)
        for (i in coords.indices) {
            val marker = Marker(map)
            marker.position = coords[i]
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title = names[i]
            map.overlays.add(marker)
        }
        map.overlays.add(polyline)
    }

}
