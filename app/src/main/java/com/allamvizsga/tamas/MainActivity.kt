package com.allamvizsga.tamas

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.allamvizsga.tamas.component.GeofenceBroadcastReceiver
import com.allamvizsga.tamas.databinding.ActivityMainBinding
import com.allamvizsga.tamas.feature.walk.list.WalkListActivity
import com.allamvizsga.tamas.model.Coordinate
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val walkRepository: WalkRepository by inject()
    private lateinit var geofencingClient: GeofencingClient
    private var geofencePendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofencingClient.removeGeofences(getGeofencePendingIntent())
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            uploadButton.setOnClickListener {
                walkRepository.saveWalk(
                        Walk(
                                title = "Kolozsvari seta 3",
                                description = "Kicsi seta kicsi seta 3",
                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699",
                                stations = arrayListOf(
                                        Station(
                                                title = "Allomas",
                                                coordinate = Coordinate(42.56, -38.98),
                                                description = "Allomas leiras",
                                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"
                                        ),
                                        Station(
                                                title = "Allomas2",
                                                coordinate = Coordinate(42.56, -38.98),
                                                description = "Allomas leiras",
                                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"
                                        ),
                                        Station(
                                                title = "Allomas 3",
                                                coordinate = Coordinate(42.56, -38.98),
                                                description = "Allomas leiras",
                                                imageUrl = "https://firebasestorage.googleapis.com/v0/b/allamvizsga-4e26b.appspot.com/o/pictures%2Fregi.jpg?alt=media&token=2b585a16-42d8-4218-9801-0b121232a699"
                                        )
                                )
                        )
                )
            }

            walksButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, WalkListActivity::class.java))
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                TODO("Show dialog explaining why is needed")

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        } else {
            // Permission has already been granted
            addGeofence()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                addGeofence()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener { Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { it.printStackTrace() }
                .addOnCompleteListener { Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show() }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofences(listOf(Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("Itthon")
                // The radius is in meters
                .setCircularRegion(46.7632567, 23.5973731, 200f)
                .setExpirationDuration(6000000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()))
        return builder.build()
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent as PendingIntent
        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return geofencePendingIntent as PendingIntent
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
