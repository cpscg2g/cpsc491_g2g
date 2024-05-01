package com.example.got2go.data

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import timber.log.Timber
import java.util.UUID.randomUUID

class RestroomsRepository {
    private var database: DatabaseReference = Firebase.database.reference
    fun getNearbyRestrooms(
        coordinates: Coordinates,
        radius: Double,
        limit: Int
    ): List<Restroom> {
        val restrooms = mutableListOf<Restroom>()
        database.child("restrooms")
            .orderByChild("coordinates/latitude")
            .startAt(coordinates.latitude - radius)
            .endAt(coordinates.latitude + radius)
            .limitToFirst(limit)
            .get().addOnSuccessListener { result ->
                for (data in result.children) {
                    val restroom = data.getValue<Restroom>()
                    if (restroom != null) {
                        restrooms.add(restroom)
                    }
                }
            } .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error getting restrooms")
            }
        return restrooms
    }
    fun addRestroom(
        name: String,
        address: Address,
        coordinates: Coordinates,
        status: String
    ) {
        val id = randomUUID().toString()
        val restroom = Restroom(
            id,
            name,
            address,
            coordinates,
            status
        )
        database.child("restrooms").child(id).setValue(restroom)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom added with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error adding restroom with ID: %s", id)
            }
    }

    fun updateRestroomName(id: String, name: String) {
        database.child("restrooms").child(id).child("name").setValue(name)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom name updated with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error updating restroom name with ID: %s", id)
            }
    }

    fun updateRestroomStatus(id: String, status: String) {
        database.child("restrooms").child(id).child("status").setValue(status)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom status updated with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error updating restroom status with ID: %s", id)
            }
    }

    fun updateRestroomAddress(id: String, address: Address) {
        database.child("restrooms").child(id).child("address").setValue(address)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom address updated with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error updating restroom address with ID: %s", id)
            }
    }

    companion object {
        private const val TAG = "FirebaseRestroomsRepository"
    }
}