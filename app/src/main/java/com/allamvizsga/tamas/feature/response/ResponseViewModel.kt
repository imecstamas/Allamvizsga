package com.allamvizsga.tamas.feature.response

import android.arch.lifecycle.ViewModel
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.model.Station

class ResponseViewModel(correctAnswer: Boolean, nextStation: Station) : ViewModel() {

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
}