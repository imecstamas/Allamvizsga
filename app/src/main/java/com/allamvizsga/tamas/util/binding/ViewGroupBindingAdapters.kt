package com.allamvizsga.tamas.util.binding

import android.databinding.BindingAdapter
import android.support.design.widget.Snackbar
import android.view.ViewGroup
import com.allamvizsga.tamas.feature.shared.SnackbarState

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