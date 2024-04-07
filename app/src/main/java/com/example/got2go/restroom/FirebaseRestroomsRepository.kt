package com.example.got2go.restroom

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.UUID.randomUUID


class FirebaseRestroomsRepository : RestroomsRepository {
    private val TAG = "FirebaseRestroomsRepository"
    private lateinit var database: DatabaseReference
    fun initDatabaseReference() {
        database = Firebase.database.reference
    }

    override fun getNearbyRestrooms(
        coordinates: Coordinates,
        radius: Double,
        limit: Int
    ): Flow<List<Restroom>> = callbackFlow {
        val topRestroomsByCoordinates = database
            .child("restrooms")
            .orderByChild("coordinates/latitude")
            .startAfter(coordinates.latitude - radius)
            .endBefore(coordinates.latitude + radius)
            .orderByChild("coordinates/longitude")
            .startAfter(coordinates.longitude - radius)
            .endBefore(coordinates.longitude + radius)
            .limitToFirst(limit)

        val subscription = topRestroomsByCoordinates.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restrooms = snapshot.children.mapNotNull { it.getValue(Restroom::class.java) }
                trySend(restrooms).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose {
            topRestroomsByCoordinates.removeEventListener(subscription)
        }
    }

    override fun getRestroomById(id: String): Flow<Restroom?> = callbackFlow {
        val restrooms = database.child("restrooms").child(id)
        val subscription = restrooms.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restroom = snapshot.getValue(Restroom::class.java)
                trySend(restroom).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose {
            restrooms.removeEventListener(subscription)
        }
    }

    override fun addRestroom(
        userID: String,
        name: String,
        address: Address,
        coordinates: Coordinates,
        status: String
    ) {
        val restroom = Restroom(userID, name, address, coordinates, status)
        val restroomID = randomUUID().toString()
        database.child("restrooms").child(restroomID).setValue(restroom)
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom added with ID: %s", restroomID)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error adding restroom with ID: %s", restroomID)
            }
    }

    override fun updateRestroom(
        id: String,
        userID: String,
        name: String?,
        address: Address?,
        coordinates: Coordinates?,
        status: String?
    ) {
        database.child("restrooms").child(id).get().addOnSuccessListener {
            val current = it.getValue(Restroom::class.java)

            val restroom = Restroom(
                userID,
                name ?: current?.name,
                address ?: current?.address,
                coordinates ?: current?.coordinates,
                status ?: current?.status
            )
            database.child("restrooms").child(id).setValue(restroom)
                .addOnSuccessListener {
                    Timber.tag(TAG).d("Restroom updated with ID: %s", id)
                }
                .addOnFailureListener { e: Exception ->
                    Timber.tag(TAG).e(e, "Error updating restroom with ID: %s", id)
                }

        }.addOnFailureListener {
            Timber.tag(TAG).e(it, "Error getting data for restroom with ID: %s", id)
        }
    }

    override fun deleteRestroom(id: String, userID: String) {
        database.child("restrooms").child(id).removeValue()
            .addOnSuccessListener {
                Timber.tag(TAG).d("Restroom deleted with ID: %s", id)
            }
            .addOnFailureListener {
                Timber.tag(TAG).e(it, "Error deleting restroom with ID: %s", id)
            }
    }
}