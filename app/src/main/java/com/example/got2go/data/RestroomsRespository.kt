package com.example.got2go.data

import kotlinx.coroutines.flow.Flow

interface RestroomsRepository {
    fun getNearbyRestrooms(
        coordinates: Coordinates,
        radius: Double = 0.01,
        limit: Int = 10
    ): Flow<List<Restroom>>
    fun addRestroom(
        name: String,
        address: Address,
        coordinates: Coordinates,
        status: String
    )
    fun updateRestroomName(id: String, name: String)
    fun updateRestroomStatus(id: String, status: String)
    fun updateRestroomAddress(id: String, address: Address)
}
