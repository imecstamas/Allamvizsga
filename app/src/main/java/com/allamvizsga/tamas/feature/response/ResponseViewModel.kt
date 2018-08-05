package com.allamvizsga.tamas.feature.response

import android.arch.lifecycle.ViewModel
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.feature.shared.SnackbarState
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.storage.repository.StationRepository

class ResponseViewModel(correctAnswer: Boolean, val station: Station, private val stationRepository: StationRepository) : ViewModel() {

    val snackbarState = SnackbarState()

    @DrawableRes
    val icon = if (correctAnswer) {
        R.drawable.ic_correct_answer_108dp
    } else {
        R.drawable.ic_wrong_answer_108dp
    }

    @StringRes
    val responseText = if (correctAnswer) {
        R.string.correct_answer
    } else {
        R.string.incorrect_answer
    }

    @ColorRes
    val backgroundColor = if (correctAnswer) {
        R.color.positive
    } else {
        R.color.negative
    }

    val navigateButtonEnabled = correctAnswer

    fun saveRegisteredStation() {
        //TODO show something that registration was successfull
        snackbarState.messageRes = R.string.walk_started
        snackbarState.build()
        stationRepository.saveRegisteredStation(station.id!!)
    }

    fun getRegisteredStation() = stationRepository.getRegisteredStation()
}