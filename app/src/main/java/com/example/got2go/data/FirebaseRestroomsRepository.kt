package com.example.got2go.data

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.UUID.randomUUID

class FirebaseRestroomsRepository : RestroomsRepository {
    private var database: DatabaseReference = Firebase.database.reference
    private val restrooms = mutableMapOf<String, Restroom>()
    override fun getNearbyRestrooms(
        coordinates: Coordinates,
        radius: Double,
        limit: Int
    ): Flow<List<Restroom>> {
        return callbackFlow {
            val topRestroomsByCoordinates = database
                .child("restrooms")
                .orderByChild("coordinates/latitude")
                .startAfter(coordinates.latitude - radius)
                .endBefore(coordinates.latitude + radius)
                .orderByChild("coordinates/longitude")
                .startAfter(coordinates.longitude - radius)
                .endBefore(coordinates.longitude + radius)
                .limitToFirst(limit)

            topRestroomsByCoordinates.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val restroom = child.getValue<Restroom>()
                    if (restroom == null) {
                        Timber.tag(TAG).w("Restroom is null")
                        continue
                    }
                    restrooms[restroom.id] = restroom
                    trySend(restrooms.values.toList()).isSuccess
                }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.tag(TAG).e(error.toException(), "Error getting restrooms")
                    close(error.toException())
                }
            })
        }
    }

    override fun addRestroom(
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

    override fun updateRestroomName(id: String, name: String) {
        database.child("restrooms").child(id).child("name").setValue(name)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom name updated with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error updating restroom name with ID: %s", id)
            }
    }

    override fun updateRestroomStatus(id: String, status: String) {
        database.child("restrooms").child(id).child("status").setValue(status)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom status updated with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error updating restroom status with ID: %s", id)
            }
    }

    override fun updateRestroomAddress(id: String, address: Address) {
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