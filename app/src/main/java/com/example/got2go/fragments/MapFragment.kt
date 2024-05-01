@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.example.got2go.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.got2go.R
import com.example.got2go.data.Coordinates
import com.example.got2go.data.Restroom
import com.example.got2go.data.RestroomData
import com.example.got2go.fragments.dialogs.AddRestroomDialogFragment
import com.example.got2go.map.LocationPermissionsHandler
import com.example.got2go.map.MapViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.DefaultLocationProvider
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import java.lang.ref.WeakReference

class MapFragment : Fragment() {
    private lateinit var sortButton: Button
    private lateinit var filterSpinner: Spinner
    private lateinit var zoomInButton: Button
    private lateinit var zoomOutButton: Button
    private lateinit var addRestroomButton: ImageButton
    private lateinit var selectedRestroom: Restroom
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsHandler: LocationPermissionsHandler
    private lateinit var location: Coordinates
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private val restroomsMapping = mutableMapOf<String, PointAnnotation>()
    private val mapViewModel: MapViewModel by activityViewModels { MapViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val btnMetal: ImageButton = view.findViewById(R.id.btnMetal)
//        btnMetal.setOnClickListener(View.OnClickListener { showMetalDialog() })
        mapView = view.findViewById(R.id.mapView)
        mapboxMap = mapView.mapboxMap
        mapView.mapboxMap.loadStyle(DEFAULT_STYLE)

        permissionsHandler = LocationPermissionsHandler(WeakReference(this.requireActivity()))
        permissionsHandler.checkPermissions {
            Timber.d("MapFragment", "Permissions granted")
        }
        mapViewModel.coordinates.observe(viewLifecycleOwner) { coordinates ->
            location = coordinates
        }
        mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(DEFAULT_ZOOM).build())
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

// Add restrooms to the map
        RestroomData.forEach { restroom ->
            restroom.coordinates?.let { coordinates ->
                val point = Point.fromLngLat(coordinates.longitude, coordinates.latitude)
                val annotation = addAnnotationToMap(point)
                if (annotation != null) {
                    restroomsMapping[restroom.id!!] = annotation
                }
                pointAnnotationManager.addClickListener {  p ->
                    selectedRestroom = RestroomData.find { it.id == p.id }!!

                    true
                }
            }
        }

//
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                mapViewModel.getNearbyRestrooms()
//                mapViewModel.restroomFlow.collect { uiState ->
//                    uiState.restrooms.forEach { (id, restroom) ->
//                        restroom.coordinates?.let { coordinates ->
//                            Toast.makeText(
//                                requireContext(),
//                                "Restroom: $id at ${coordinates.latitude}, ${coordinates.longitude}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            val point = Point.fromLngLat(coordinates.longitude, coordinates.latitude)
//                            val annotation = restroomsMapping[id]
//                            if (annotation != null) {
//                                annotation.geometry = point
//                                pointAnnotationManager.update(annotation)
//                            } else {
//                                val newAnnotation = addAnnotationToMap(point)
//                                if (newAnnotation != null) {
//                                    restroomsMapping[id] = newAnnotation
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

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
            AddRestroomDialogFragment().show(parentFragmentManager, "addRestroomDialogFragment")
        }
    }

    /* tracks the location position indicator */
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener { point ->
        if (location.latitude == point.latitude() && location.longitude == point.longitude()) {
            return@OnIndicatorPositionChangedListener
        }
        Toast.makeText(
            requireContext(),
            "Location changed to: ${point.latitude()}, ${point.longitude()}",
            Toast.LENGTH_SHORT
        ).show()
        mapViewModel.setCoordinates(point.latitude(), point.longitude())
        mapView.mapboxMap.setCamera(CameraOptions.Builder().center(point).build())
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
    private fun addAnnotationToMap(point: Point): PointAnnotation? {
        bitmapFromDrawableRes(this.requireContext())?.let {
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
                .withDraggable(true)
//                .withData(restroom.toMap().toJsonObject())
            return pointAnnotationManager.create(pointAnnotationOptions)
        }
        return null
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
            pulsingEnabled = true
            puckBearingEnabled = true
        }
        val provider = mapView.location.getLocationProvider() as DefaultLocationProvider
        provider.addOnCompassCalibrationListener {
                Toast.makeText(requireContext(), "Compass calibration", Toast.LENGTH_SHORT).show()
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
