package com.example.got2go.restroom

import kotlinx.coroutines.flow.Flow

interface RestroomsRepository {
    fun getNearbyRestrooms(
        coordinates: Coordinates,
        radius: Double,
        limit: Int
    ): Flow<List<Restroom>>

    fun getRestroomById(id: String): Flow<Restroom?>
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

    fun deleteRestroom(id: String, userID: String)
}

interface UserRepository {
    fun getLocation(): Coordinates
}