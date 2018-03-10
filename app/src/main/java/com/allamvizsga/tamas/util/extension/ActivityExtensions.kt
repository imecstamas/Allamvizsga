package com.allamvizsga.tamas.util.extension

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
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

fun AppCompatActivity.runWithPermission(permission: String, permissionRequestCode: Int, method: () -> Unit, permissionRationale: () -> Unit) {
    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            permissionRationale.invoke()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
        }
    } else {
        method.invoke()
    }
}