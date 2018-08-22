package com.allamvizsga.tamas.util.extension

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Window
import android.widget.Toast
import com.google.ar.core.ArCoreApk
import java.util.*


fun AppCompatActivity.setUpToolbar(toolbar: Toolbar, displayUp: Boolean = true) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(displayUp)
}

fun AppCompatActivity.startActivityWithTransition(intent: Intent, vararg sharedViews: View?) {
    val pairs =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) buildSharedElements(*sharedViews) else null
    if (pairs != null) {
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs).toBundle())
    } else {
        startActivity(intent)
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
private fun AppCompatActivity.buildSharedElements(vararg targetViews: View?): Array<Pair<View, String>>? {
    val list = ArrayList<Pair<View, String>>()

    // loop through the targetViews and add the shared element transition to the list
    for (targetView in targetViews) {
        if (targetView == null) {
            return null
        }
        if (targetView.visibility != View.VISIBLE) {
            continue
        }
        if (targetView.transitionName == null) {
            throw IllegalArgumentException("A transition name must be supplied for $targetView")
        } else {
            list.add(Pair(targetView, targetView.transitionName))
        }
    }
    findViewById<View?>(android.R.id.statusBarBackground)?.let {
        list.add(Pair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME))
    }
    findViewById<View?>(android.R.id.navigationBarBackground)?.let {
        list.add(Pair(it, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
    }
    return list.toTypedArray()
}

fun AppCompatActivity.runWithPermission(
        permission: String,
        permissionRequestCode: Int,
        method: () -> Unit,
        permissionRationale: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            permissionRationale.invoke()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
        }
    } else {
        method.invoke()
    }
}

fun Context.isDeviceSupported(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Toast.makeText(this, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show()
        return false
    }
    val openGlVersionString = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
    if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
        Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
        return false
    }
    //TODO probably we need to check the isTransient too
    return ArCoreApk.getInstance().checkAvailability(this).isSupported
}

private const val MIN_OPENGL_VERSION = 3.0
