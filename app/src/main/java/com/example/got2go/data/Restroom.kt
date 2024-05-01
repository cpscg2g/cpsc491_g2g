package com.example.got2go.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.mapbox.geojson.Point



data class Coordinates(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Address(
    val address: String? = "",
    val city: String? = "",
    val state: String? = "",
    val zip: String? = "",
    val country: String? = "",
)

@IgnoreExtraProperties
data class Restroom(
    val id: String? ="",
    var name: String? = "",
    var address: Address? = Address(),
    var coordinates: Coordinates? = Coordinates(),
    var status: String = "open",
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "address" to address,
            "coordinates" to coordinates,
            "status" to status,
        )
    }
    @Exclude
    fun toPoint(): Point? {
        return this.coordinates?.let { Point.fromLngLat(it.longitude, it.latitude) }
    }
}
