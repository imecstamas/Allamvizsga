package com.allamvizsga.tamas.util.binding

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by tamas on 1/22/18.
 */
@BindingAdapter("android:src")
fun setImageSrc(imageView: ImageView, url: String) {
    Glide.with(imageView).load(url).into(imageView)
}