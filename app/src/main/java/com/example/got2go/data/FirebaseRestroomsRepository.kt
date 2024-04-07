package com.example.got2go.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
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
    ): MutableMap<String, Restroom> {
        val restrooms = mutableMapOf<String, Restroom>()
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
                for (restroomSnapshot in snapshot.children) {
                    val restroom = restroomSnapshot.getValue(Restroom::class.java)
                    if (restroom != null) {
                        restrooms[restroomSnapshot.key!!] = restroom
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG,
                    "Error getting data for restrooms with coordinates: $coordinates",
                    error.toException()
                )
            }
        })

        return restrooms
    }

    override fun getRestroomById(id: String): MutableLiveData<Restroom> {
        var restroom: Restroom? = null
        database.child("restrooms").child(id).get().addOnSuccessListener {
            restroom = it.getValue(Restroom::class.java)
        }.addOnFailureListener {
            Log.e(TAG, "Error getting data for restroom with ID: $id", it)
        }
        return MutableLiveData(restroom)
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
                Log.d(TAG, "Restroom added with ID: $restroomID")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error adding restroom with ID: $restroomID", it)
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
                    Log.d(TAG, "Restroom updated with ID: $id")
                }
                .addOnFailureListener { e: Exception ->
                    Log.e(TAG, "Error updating restroom with ID: $id", e)
                }

        }.addOnFailureListener {
            Log.e(TAG, "Error getting data for restroom with ID: $id", it)
        }
    }

    override fun deleteRestroom(id: String) {
        database.child("restrooms").child(id).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Restroom deleted with ID: $id")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error deleting restroom with ID: $id", it)
            }
    }

}