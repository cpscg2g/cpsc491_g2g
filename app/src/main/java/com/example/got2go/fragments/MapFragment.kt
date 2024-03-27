package com.example.got2go.fragments

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.got2go.R
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class MapFragment : Fragment(), PermissionsListener {
    private lateinit var sortButton: Button
    private lateinit var filterSpinner: Spinner

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val btnMetal: ImageButton = view.findViewById(R.id.btnMetal)
//        btnMetal.setOnClickListener(View.OnClickListener { showMetalDialog() })
        checkPermissions {
            mapView = view.findViewById(R.id.mapView)
            map = mapView.mapboxMap
            onMapReady()
        }

        filterSpinner = view.findViewById(R.id.filterSpinner)
        val filterItems = listOf(
            "Wheelchair Accessible",
            "No Passcode",
            "Inclusive Stall",
            "Hygiene Products",
            "Baby Station",
            "Urinals",
            "Single Stall",
            "Family Restroom"
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        filterSpinner.adapter = adapter

        sortButton = view.findViewById(R.id.sortButton)

        sortButton.setOnClickListener {
            // Create a PopupMenu
            val popupMenu = PopupMenu(requireContext(), sortButton)
            popupMenu.menuInflater.inflate(R.menu.sort_options_menu, popupMenu.menu)

            // Set item click listener for the menu items
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.sortOption1 -> {
                        // Handle sort option 1
                        true
                    }

                    R.id.sortOption2 -> {
                        // Handle sort option 2
                        true
                    }

                    else -> false
                }
            }

            // Show the popup menu
            popupMenu.show()
        }

    }

    private fun onMapReady() {
        map.loadStyle(
            Style.STANDARD
        ) {
            initLocationComponent()
        }
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        map.setCamera(CameraOptions.Builder().center(it).build())
    }
    private fun initLocationComponent(){
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            puckBearing = PuckBearing.HEADING
            puckBearingEnabled = true
            enabled = true
            locationPuck = LocationPuck2D(
                bearingImage = ImageHolder.from(R.drawable.remove_minus),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }

    private fun showMetalDialog() {
        val fm = fragmentManager
        val fragmentMetal: DialogFragmentSample =
            DialogFragmentSample.Companion.newInstance("Some Title")
        fragmentMetal.show(fm!!, "fragment_metal")
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(
            this.context,
            "You need to accept location permissions to access the restroom map.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            onMapReady()
        } else {
            activity?.finish()
        }
    }

    private fun checkPermissions(onMapReady: () -> Unit) {
        if (PermissionsManager.areLocationPermissionsGranted(this.requireContext())) {
            onMapReady()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this.requireActivity())
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
