package com.allamvizsga.tamas.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Walk(
        val id: String? = null,
        val title: String,
        val description: String,
        val imageUrl: String,
        var stations: List<Station>? = null
) : Parcelable