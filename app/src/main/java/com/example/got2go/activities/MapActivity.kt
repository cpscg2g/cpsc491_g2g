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
    // we use the layout for the activity and place a fragment content 
    // placeholder in the xml file, Currently Style is a sensible default
    // but we may want to add a custom style from Mapbox Studio
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
    // a helper function to set the map to sensible defaults
    // currently some values are hardcoded but those will get
    // replaced by the proper data sources later
    private fun initFragmentStyle(fragmentID: Int, styleID: String, cameraOptions: CameraOptions) {
        mapFragment = supportFragmentManager.findFragmentById(fragmentID) as MapFragment
        mapFragment.getMapAsync {
            it.setCamera(cameraOptions)
            it.loadStyle(styleID)
        }
    }
    // we can hold static constants in this companion object
    companion object {
        private const val LATITUDE = 33.8823
        private const val LONGITUDE = -117.92141
    }
}
