@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.example.got2go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.got2go.R
import com.example.got2go.utils.LocationPermissionsHandler
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.ref.WeakReference

class MapFragment : Fragment() {
    private lateinit var sortButton: Button
    private lateinit var filterSpinner: Spinner

    private lateinit var mapView: MapView
    private var styleUri = Style.STANDARD

    private lateinit var permissionsHandler: LocationPermissionsHandler

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

        mapView = view.findViewById(R.id.mapView)
        permissionsHandler = LocationPermissionsHandler(WeakReference(this.requireActivity()))
        permissionsHandler.checkPermissions {
            mapView.apply {
                mapboxMap.setCamera(CameraOptions.Builder().zoom(15.5).build())
            }
        }
        mapView.mapboxMap.loadStyle(styleUri)

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

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.mapboxMap.flyTo(CameraOptions.Builder().center(it).build())
    }

    private fun showMetalDialog() {
        val fm = fragmentManager
        val fragmentMetal: DialogFragmentSample =
            DialogFragmentSample.Companion.newInstance("Some Title")
        fragmentMetal.show(fm!!, "fragment_metal")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.updateSettings {
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
    }

    override fun onStop() {
        super.onStop()
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }
}
