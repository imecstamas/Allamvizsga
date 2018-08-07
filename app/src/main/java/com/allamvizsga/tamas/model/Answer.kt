package com.allamvizsga.tamas.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Answer(val text: String, val correct: Boolean) : Parcelable