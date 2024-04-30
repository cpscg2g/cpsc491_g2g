@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.example.got2go.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.got2go.R
import com.example.got2go.map.LocationPermissionsHandler
import com.example.got2go.map.MapViewModel
import com.example.got2go.map.UiState
//import com.mapbox.common.location.AccuracyLevel
//import com.mapbox.common.location.DeviceLocationProvider
//import com.mapbox.common.location.IntervalSettings
//import com.mapbox.common.location.Location
//import com.mapbox.common.location.LocationProviderRequest
//import com.mapbox.common.location.LocationService
//import com.mapbox.common.location.LocationServiceFactory
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
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

//internal class LocationListener(
//    private val context: Context,
//    private val callback: (Location) -> Unit
//) {
//    private val locationService: LocationService = LocationServiceFactory.getOrCreate()
//    private lateinit var locationProvider: DeviceLocationProvider
//    fun enable() {
//        val request = LocationProviderRequest.Builder()
//            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).build())
//            .displacement(0F)
//            .accuracy(AccuracyLevel.HIGHEST)
//            .build()
//        val result = locationService.getDeviceLocationProvider(request)
//        if (result.isValue) {
//            locationProvider = result.value!!
//        } else {
//            Timber.tag("LocationListener").e("Failed to get device location provider.")
//        }
//    }
//}

class MapFragment : Fragment() {
    private lateinit var sortButton: Button
    private lateinit var filterSpinner: Spinner
    private lateinit var zoomInButton: Button
    private lateinit var zoomOutButton: Button
    private lateinit var addRestroomButton: ImageButton

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsHandler: LocationPermissionsHandler
//    private lateinit var locationListener: LocationListener
    private val mapViewModel: MapViewModel by viewModels { MapViewModel.Factory }

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
//
        // the mapview is created by inflating the layout
        mapView = view.findViewById(R.id.mapView)
        mapboxMap = mapView.mapboxMap
        permissionsHandler = LocationPermissionsHandler(WeakReference(this.requireActivity()))
        mapView.mapboxMap.loadStyle(DEFAULT_STYLE)

        permissionsHandler.checkPermissions {
            mapView.mapboxMap.apply {
                this.setCamera(CameraOptions.Builder().zoom(DEFAULT_ZOOM).build())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.restroomFlow.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            Toast.makeText(requireContext(), "Loading nearest restrooms...", Toast.LENGTH_SHORT).show()
                        }
                        is UiState.Success -> {
                            Timber.d("Success: ${uiState.restrooms}")
                            Toast.makeText(requireContext(), "Nearest restrooms loaded", Toast.LENGTH_SHORT).show()
                            for (restroom in uiState.restrooms) {
                                addAnnotationToMap(restroom.toPoint()!!)
                            }
                        }
                        is UiState.Error -> {
                            Timber.e(uiState.throwable, "Error fetching nearest restrooms")
                            Toast.makeText(
                                requireContext(),
                                "There was an error with our service. Our apologies.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(DEFAULT_ZOOM).build())


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

        zoomInButton = view.findViewById(R.id.zoomInButton)
        zoomInButton.setOnClickListener {
            val currentZoom = mapView.mapboxMap.cameraState.zoom
            mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(currentZoom + 1).build())
        }

        zoomOutButton = view.findViewById(R.id.zoomOutButton)
        zoomOutButton.setOnClickListener {
            val currentZoom = mapView.mapboxMap.cameraState.zoom
            mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(currentZoom - 1).build())
        }

        addRestroomButton = view.findViewById(R.id.addRestroomButton)
        addRestroomButton.setOnClickListener {
            Toast.makeText(requireContext(), "Add Restroom Button Clicked", Toast.LENGTH_SHORT).show()
            AddRestroomDialogFragment().show(parentFragmentManager, "addRestroomDialogFragment")
        }
    }

    /* tracks the location position indicator */
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
    }

    private fun showMetalDialog() {
        val fm = fragmentManager
        val fragmentMetal: DialogFragmentSample =
            DialogFragmentSample.Companion.newInstance("Some Title")
        fragmentMetal.show(fm!!, "fragment_metal")
    }

    /* notifies the permissionsHandler of the result of the location permission request */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /* use the annotation manager of the annotationAPI to add a point to the map */
    private fun addAnnotationToMap(point: Point) {
        bitmapFromDrawableRes(this.requireContext())?.let {
            val annotationAPI = mapView.annotations
            val pointAnnotationManager = annotationAPI.createPointAnnotationManager()
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
                .withDraggable(true)
//                .withData(restroom.toMap().toJsonObject())
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    /* convert the red marker resource to a bitmap that we can pass into mapbox */
// TODO: make a custom marker for restrooms
    private fun bitmapFromDrawableRes(context: Context): Bitmap? {
        val drawable = AppCompatResources.getDrawable(context, R.drawable.red_marker) ?: return null
        val bitmap = Bitmap.createBitmap(
            64,
            80,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onStart() {
        super.onStart()
        // create a location listener to track the user's location and update
        // the nearest restrooms list past a certain threshold distance
//        locationListener = LocationListener(this.requireContext()) {
//            Timber.tag("LocationListener").d("Location: $it")
//        }
//        // enable connects the listener to a location provider
//        locationListener.enable()

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
        const val DEFAULT_ZOOM = 15.5
        const val DEFAULT_STYLE = Style.MAPBOX_STREETS
    }
}
