package com.allamvizsga.tamas.storage.repository

import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.extension.observe
import com.google.firebase.database.DatabaseReference
import io.reactivex.Single

class StationRepository(private val databaseReference: DatabaseReference) {

    fun getById(id: String): Single<Station> = Single.create { emitter ->
        databaseReference.child(STATIONS).child(id).observe({ dataSnapshot ->
            emitter.onSuccess(mapToStation(dataSnapshot))
        }, { error ->
            emitter.onError(error)
        })
    }

    companion object {
        private const val STATIONS = "stations"
    }
}