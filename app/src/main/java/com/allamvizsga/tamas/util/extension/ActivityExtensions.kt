package com.allamvizsga.tamas.util.extension

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View

fun AppCompatActivity.setUpToolbar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun AppCompatActivity.startActivityWithTransition(intent: Intent, view: View) {
    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, view.transitionName).toBundle())
}