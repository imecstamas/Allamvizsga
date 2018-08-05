package com.allamvizsga.tamas.util.binding

import android.databinding.BindingAdapter
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import com.allamvizsga.tamas.feature.shared.SnackbarState
import com.allamvizsga.tamas.model.Answer
import com.bumptech.glide.Glide

/**
 * Created by tamas on 1/22/18.
 */
@BindingAdapter("app:backgroundColorRes")
fun View.setBackgroundColorRes(@ColorRes colorRes: Int) {
    setBackgroundColor(ContextCompat.getColor(context, colorRes))
}

@BindingAdapter("android:src")
fun ImageView.setImageSource(url: String?) {
    Glide.with(this).load(url).into(this)
}

@BindingAdapter("android:src")
fun ImageView.setImageSource(@DrawableRes drawableId: Int) {
    setImageResource(drawableId)
}

@BindingAdapter("snackbarState")
fun ViewGroup.setSnackbarState(snackbarState: SnackbarState) {
    if (snackbarState.visible) {
        val snackbar = Snackbar.make(this, snackbarState.messageRes, snackbarState.duration)
        if (snackbarState.actionRes != 0 && snackbarState.action != null) {
            snackbar.setAction(snackbarState.actionRes, snackbarState.action)
        }
        snackbar.show()
        snackbarState.clear()
    }
}

@BindingAdapter("answers")
fun RadioGroup.setAnswers(answers: List<Answer>) {
    answers.forEachIndexed { index, answer ->
        val radioButton = RadioButton(context)
        radioButton.id = index
        radioButton.text = answer.text
        addView(radioButton)
    }
}