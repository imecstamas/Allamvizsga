package com.allamvizsga.tamas.feature.shared

import android.databinding.BaseObservable
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

class SnackbarState(
    @StringRes var messageRes: Int, var duration: Int = Snackbar.LENGTH_LONG, @StringRes var actionRes: Int = 0,
    var action: View.OnClickListener? = null, var visible: Boolean = false
) :
    BaseObservable() {
    fun build() {
        visible = true
        notifyChange()
    }
}