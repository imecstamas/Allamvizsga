package com.allamvizsga.tamas.util.extension

import com.google.firebase.database.*

inline fun DatabaseReference.observe(
    crossinline callback: (DataSnapshot) -> Unit,
    crossinline errorCallback: (DatabaseException) -> Unit
) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            errorCallback(p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            callback(p0)
        }
    })
}