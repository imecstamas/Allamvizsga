package com.allamvizsga.tamas

import com.allamvizsga.tamas.model.Walk
import com.google.firebase.database.FirebaseDatabase

fun uploadWalk(walk: Walk) {
    val database = FirebaseDatabase.getInstance().reference
    val stations = walk.stations

    val stationIds = mutableListOf<String>()
    stations.forEach {
        database.child("stations").apply {
            val id = push().key
            child(id).setValue(it)
            stationIds.add(id)
        }
    }

    database.child("walks").apply {
        val id = push().key
        child(id).setValue(WalkDTO(walk.title, walk.description, walk.imageUrl))
        stationIds.forEach { child(id).child("stations").child(it).setValue(true) }
    }
}

class WalkDTO(val title: String, val description: String, val imageUrl: String)