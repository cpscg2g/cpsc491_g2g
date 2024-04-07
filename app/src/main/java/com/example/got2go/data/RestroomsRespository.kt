package com.example.got2go.data

import androidx.lifecycle.MutableLiveData

interface RestroomsRepository {
    fun getNearbyRestrooms(
        coordinates: Coordinates,
        radius: Double = 0.01,
        limit: Int = 10
    ): MutableMap<String, Restroom>

    fun getRestroomById(id: String): MutableLiveData<Restroom>
    fun addRestroom(
        userID: String,
        name: String,
        address: Address,
        coordinates: Coordinates,
        status: String
    )

    fun updateRestroom(
        id: String,
        userID: String,
        name: String?,
        address: Address?,
        coordinates: Coordinates?,
        status: String?
    )
    fun deleteRestroom(id: String)
}
