package com.example.got2go.restroom

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
)

data class Address(
    val address: String?,
    val city: String?,
    val state: String?,
    val zip: String?,
    val country: String?,
)

@IgnoreExtraProperties
data class Restroom(
    val userID: String? = "",
    var name: String? = "",
    val address: Address? = Address("", "", "", "", ""),
    val coordinates: Coordinates? = Coordinates(0.0, 0.0),
    val status: String? = "open",
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userID" to userID,
            "name" to name,
            "address" to address,
            "coordinates" to coordinates,
            "status" to status,
        )
    }
}