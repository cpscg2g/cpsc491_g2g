package com.example.got2go.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.got2go.R
import com.example.got2go.fragments.MapFragment
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style

class MapActivity: AppCompatActivity() {
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initFragmentStyle(
            R.id.mapView,
            Style.MAPBOX_STREETS,
            CameraOptions.Builder()
                .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                .zoom(12.0)
                .build()
        )
    }

    private fun initFragmentStyle(fragmentID: Int, styleID: String, cameraOptions: CameraOptions) {
        val fragment = supportFragmentManager.findFragmentById(fragmentID) as MapFragment
        fragment.getMapAsync {
            it.setCamera(cameraOptions)
            it.loadStyle(styleID)
        }
    }

    companion object {
        private const val LATITUDE = 33.8823
        private const val LONGITUDE = -117.92141
    }
}