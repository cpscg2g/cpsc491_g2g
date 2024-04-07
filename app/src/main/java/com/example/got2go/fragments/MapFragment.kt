@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.example.got2go.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.got2go.R
import com.example.got2go.map.MapMarkerManager
import com.example.got2go.map.MapViewModel
import com.example.got2go.map.RestroomMarkerManager
import com.example.got2go.map.LocationPermissionsHandler
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MapFragment : Fragment() {
    private lateinit var sortButton: Button
    private lateinit var filterSpinner: Spinner

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private lateinit var permissionsHandler: LocationPermissionsHandler
    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var restroomMarkerManager: MapMarkerManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // queue the restroom data request and store the data in the viewmodel

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val btnMetal: ImageButton = view.findViewById(R.id.btnMetal)
//        btnMetal.setOnClickListener(View.OnClickListener { showMetalDialog() })
        viewLifecycleOwner.lifecycleScope.launch {
            restroomMarkerManager = RestroomMarkerManager()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.uiState.collect { uiState ->
                    // update the map with the new restrooms
                    uiState.restrooms.forEach { (restroomID, restroom) ->
                        restroomMarkerManager.run {
                            addMarker(restroomID, restroom.coordinates!!)
                        }
                    }
                }
            }
            mapViewModel.loadRestrooms()
        }

        // the mapview is created by inflating the layout
        mapView = view.findViewById(R.id.mapView)
        mapboxMap = mapView.mapboxMap

        permissionsHandler = LocationPermissionsHandler(WeakReference(this.requireActivity()))
        mapView.mapboxMap.loadStyle(styleUri)
        permissionsHandler.checkPermissions {
            mapView.mapboxMap.apply {
                this.setCamera(CameraOptions.Builder().zoom(DEFAULT_ZOOM).build())
            }

        }

        mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(DEFAULT_ZOOM).build())
        addAnnotationToMap(Point.fromLngLat(ECS_LONGITUDE, ECS_LATITUDE))

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
        mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
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

    private fun addAnnotationToMap(point: Point) {
        bitmapFromDrawableRes(this.requireContext(), R.drawable.red_marker)?.let {
            val annotationAPI = mapView.annotations
            pointAnnotationManager = annotationAPI.createPointAnnotationManager()
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceID: Int): Bitmap? {
        return convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceID))
    }

    private fun convertDrawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onStart() {
        super.onStart()
        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.updateSettings {
            puckBearing = PuckBearing.HEADING
            puckBearingEnabled = true
            enabled = true
            locationPuck = LocationPuck2D(
                bearingImage = ImageHolder.from(R.drawable.blue_dot),
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

    companion object {
        const val ECS_LONGITUDE = -117.88266
        const val ECS_LATITUDE = 33.88231
        const val DEFAULT_ZOOM = 15.5
        private var styleUri = Style.MAPBOX_STREETS
    }
}
