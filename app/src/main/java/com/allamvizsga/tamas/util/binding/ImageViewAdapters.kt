package com.allamvizsga.tamas.util.binding

import android.databinding.BindingAdapter
import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by tamas on 1/22/18.
 */
@BindingAdapter("android:src")
fun ImageView.setImageSource(url: String?) {
    Glide.with(this).load(url).into(this)
}

@BindingAdapter("android:src")
fun ImageView.setImageSource(@DrawableRes drawableId: Int) {
    setImageResource(drawableId)
}