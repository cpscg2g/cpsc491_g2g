package com.example.got2go.map

import android.app.Activity
import android.widget.Toast
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import java.lang.ref.WeakReference

class LocationPermissionsHandler(private val activityRef: WeakReference<Activity>) {
    private class LocationPermissionListener(
        private val activityRef: WeakReference<Activity>,
        private val onMapReady: () -> Unit
    ) : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {
            activityRef.get()?.let {
                Toast.makeText(
                    it,
                    EXPLANATION_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onPermissionResult(granted: Boolean) {
            activityRef.get()?.let {
                if (granted) {
                    onMapReady()
                } else {
                    it.finish()
                }
            }
        }

        companion object {
            const val EXPLANATION_MESSAGE =
                "Got2Go cannot function properly without access to your location.\nPlease enable location access. Thank you!"
        }
    }

    private lateinit var permissionsManager: PermissionsManager

    fun checkPermissions(onMapReady: () -> Unit) {
        activityRef.get()?.let { activity: Activity ->
            if (PermissionsManager.areLocationPermissionsGranted(activity)) {
                onMapReady()
            } else {
                val permissionsListener = LocationPermissionListener(activityRef, onMapReady)
                permissionsManager = PermissionsManager(permissionsListener)
                permissionsManager.requestLocationPermissions(activity)
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
