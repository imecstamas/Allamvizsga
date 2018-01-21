package com.allamvizsga.tamas.storage.repository

import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.util.extension.observe
import com.google.firebase.database.DatabaseReference
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class WalkRepository(private val databaseReference: DatabaseReference) {

    /**
     * Method will return all the Walks without the Stations
     */
    fun getAll(): Single<List<Walk>> = Single.create { emitter ->
        databaseReference.child(WALKS).observe({ dataSnapShot ->
            val walks = mutableListOf<Walk>()
            dataSnapShot.children.forEach {
                walks.add(mapToWalk(it))
            }
            emitter.onSuccess(walks)
        }, { error ->
            emitter.onError(error)

        })
    }

    /**
     * Method will return a Walk by id and the whole Station list
     */
    fun getById(id: String): Single<Walk> = Single.zip(getStationsByWalkId(id), getWalkById(id), BiFunction { stations, walk ->
        walk.stations = stations
        walk
    })

    private fun getStationsByWalkId(id: String): Single<List<Station>> = Single.create { emitter ->
        databaseReference.let { databaseReference ->
            databaseReference.child(WALKS).child(id).child(STATIONS).observe({ dataSnapshot ->
                val stations = mutableListOf<Station>()
                val size = dataSnapshot.childrenCount
                dataSnapshot.children.forEach {
                    databaseReference.child(STATIONS).child(it.key).observe({
                        stations.add(mapToStation(it))
                        if (size.toInt() == stations.size) {
                            emitter.onSuccess(stations)
                        }
                    }, { error ->
                        emitter.onError(error)
                    })
                }
            }, { error ->
                emitter.onError(error)
            })
        }
    }

    private fun getWalkById(id: String): Single<Walk> = Single.create { emitter ->
        databaseReference.child(WALKS).child(id).observe({ dataSnapshot ->
            emitter.onSuccess(mapToWalk(dataSnapshot))
        }, { error ->
            emitter.onError(error)
        })
    }

    fun saveWalk(walk: Walk) {
        val stations = walk.stations

        val stationIds = mutableListOf<String>()
        stations?.forEach {
            databaseReference.child(STATIONS).apply {
                val id = push().key
                child(id).setValue(it)
                stationIds.add(id)
            }
        }

        databaseReference.child(WALKS).apply {
            val id = push().key
            child(id).setValue(mapEntityToDto(walk))
            stationIds.forEach { child(id).child(STATIONS).child(it).setValue(true) }
        }
    }

    companion object {
        private const val WALKS = "walks"
        private const val STATIONS = "stations"
    }
}