package com.allamvizsga.tamas.feature.walklist

import android.databinding.ObservableField
import com.allamvizsga.tamas.model.Walk

/**
 * Created by tamas on 1/22/18.
 */
class WalkItemViewModel {
    val walk = ObservableField<Walk>()

    fun setWalk(walk: Walk) {
        this.walk.set(walk)
    }

}