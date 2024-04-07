package com.example.got2go.map

import com.example.got2go.restroom.Coordinates

interface MapMarkerManager {
    fun addMarker(restroomID: String, coordinates: Coordinates)
    fun removeMarker(restroomID: String)
    fun updateMarker(restroomID: String, coordinates: Coordinates)
    fun getMarker(restroomID: String): Coordinates?
}

class RestroomMarkerManager : MapMarkerManager {
    override fun addMarker(restroomID: String, coordinates: Coordinates) {
        TODO("Not yet implemented")
    }

    override fun removeMarker(restroomID: String) {
        TODO("Not yet implemented")
    }

    override fun updateMarker(restroomID: String, coordinates: Coordinates) {
        TODO("Not yet implemented")
    }

    override fun getMarker(restroomID: String): Coordinates? {
        TODO("Not yet implemented")
    }

}