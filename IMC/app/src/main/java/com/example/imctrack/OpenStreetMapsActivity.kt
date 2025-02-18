package com.example.imctrack

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

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
    }
}
