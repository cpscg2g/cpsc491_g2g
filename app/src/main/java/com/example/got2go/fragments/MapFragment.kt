package com.example.got2go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.got2go.R
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap

class MapFragment
    : Fragment() {

    private lateinit var sortButton: Button
    private lateinit var filterSpinner: Spinner

    // mapbox-specific variables
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var onMapReady: (MapboxMap) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // return inflater.inflate(R.layout.fragment_map, container, false)
        mapView = MapView(
            inflater.context,
            MapInitOptions(inflater.context, textureView = true)
        )
        return mapView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //var btnMetal: ImageButton = view.findViewById(R.id.btnMetal)
        //btnMetal.setOnClickListener(View.OnClickListener { showMetalDialog() })

        // initialize the mapboxMap component
        mapboxMap = mapView.mapboxMap
        if (::onMapReady.isInitialized) {
            onMapReady.invoke(mapboxMap)
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
            popupMenu.setOnMenuItemClickListener { menuItem ->
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

    private fun showMetalDialog() {
        val fm = fragmentManager
        val fragmentMetal: DialogFragmentSample =
            DialogFragmentSample.Companion.newInstance("Some Title")
        fragmentMetal.show(fm!!, "fragment_metal")
    }

    fun getMapAsync(callback: (MapboxMap) -> Unit) = if (::mapboxMap.isInitialized) {
        callback.invoke(mapboxMap)
    } else this.onMapReady = callback

    fun getMapView(): MapView {
        return mapView
    }
}
