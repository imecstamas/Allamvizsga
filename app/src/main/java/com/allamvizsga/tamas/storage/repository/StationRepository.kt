package com.allamvizsga.tamas.storage.repository

import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.storage.preference.SharedPreferencesManager
import com.allamvizsga.tamas.util.extension.observe
import com.google.firebase.database.DatabaseReference
import io.reactivex.Single

class StationRepository(private val databaseReference: DatabaseReference, private val sharedPreferencesManager: SharedPreferencesManager) {

    fun getById(id: String?): Single<Station> = Single.create { emitter ->
        id?.let {
            databaseReference.child(STATIONS).child(it).observe({ dataSnapshot ->
                emitter.onSuccess(mapToStation(dataSnapshot))
            }, { error ->
                emitter.onError(error)
            })
        }
    }

    fun saveRegisteredStation(stationId: String) {
        sharedPreferencesManager.saveRegisteredStation(stationId)
    }

    fun getRegisteredStationId() = sharedPreferencesManager.getRegisteredStationId()

    companion object {
        private const val STATIONS = "stations"
    }
}