package com.allamvizsga.tamas.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Station(
        val id: String? = null,
        val title: String,
        val coordinate: Coordinate,
        val description: String,
        val imageUrl: String,
        val audioUrl: String,
        val question: Question
) : Parcelable