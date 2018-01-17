package com.allamvizsga.tamas

import com.allamvizsga.tamas.model.Walk
import com.google.firebase.database.*
import io.reactivex.Single

class WalkRepository {

    /**
     * Method will return all the Walks without the Stations
     */
    fun getAll(): Single<List<Walk>> = Single.create { emitter ->
        FirebaseDatabase.getInstance().reference.child("walks").observe({ dataSnapShot ->
            val walks = mutableListOf<Walk>()
            dataSnapShot.children.forEach {
                val resultMap: Map<*, *> = it.value as Map<*, *>
                walks.add(Walk(it.key, resultMap["title"] as String, resultMap["description"] as String, resultMap["imageUrl"] as String))

            }
            emitter.onSuccess(walks)
        }, { error ->
            emitter.onError(error)

        })
    }

    /**
     * Method will return a Walk by id and the whole Station list
     */
    fun getById(id: String) {}
}

inline fun DatabaseReference.observe(crossinline callback: (DataSnapshot) -> Unit, crossinline errorCallback: (DatabaseException) -> Unit) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            errorCallback(p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            callback(p0)
        }
    })
}