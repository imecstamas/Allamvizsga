package com.allamvizsga.tamas.feature.response

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.ResponseBinding
import com.allamvizsga.tamas.component.FenceReceiver
import com.allamvizsga.tamas.feature.station.StationDetailActivity
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.RevealAnimation
import com.allamvizsga.tamas.util.extension.runWithPermission
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials
import com.estimote.proximity_sdk.proximity.ProximityObserver
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.setProperty


class ResponseActivity : AppCompatActivity() {

    lateinit var revealAnimation: RevealAnimation
    lateinit var responseViewModel: ResponseViewModel
    private var pendingIntent: PendingIntent? = null
    private var observationHandler: ProximityObserver.Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ResponseBinding>(this, R.layout.response_activity).apply {
            setProperty(CORRECT_ANSWER, intent.getBooleanExtra(CORRECT_ANSWER, false))
            setProperty(NEXT_STATION, intent.getParcelableExtra(NEXT_STATION))
            viewModel = getViewModel<ResponseViewModel>().also {
                responseViewModel = it
            }
            revealAnimation = RevealAnimation(root, intent, this@ResponseActivity)
            navigateButton.setOnClickListener {
                registerStation()

                with(responseViewModel.station.coordinate) {
                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitude,$longitude"))
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(mapIntent)
                    }
                }
            }
            findItButton.setOnClickListener {
                registerStation()
            }
        }
    }

    override fun onDestroy() {
        //TODO check if we want to stop here the observation
        observationHandler?.stop()
        super.onDestroy()
    }

    private fun registerStation() {
        //TODO remove this
        startActivity(StationDetailActivity.getStartIntent(this, responseViewModel.station))
        runWithPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE,
                ::startLocationServices,
                ::showPermissionRationale
        )
        //Check if the Bluetooth is enabled or not
        BluetoothAdapter.getDefaultAdapter()?.let {
            if (it.isEnabled) {
                registerBeacons()
            } else {
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_ENABLE_REQUEST_CODE)
            }
        }
    }

    override fun onBackPressed() {
        revealAnimation.unRevealActivity()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices()
                } else {
                    responseViewModel.snackbarState.apply {
                        messageRes = R.string.location_disabled
                    }.build()
                }
            }
        }
    }

    //region LOCATION FENCES
    @SuppressLint("MissingPermission")
    private fun startLocationServices() {
        //Unregister the geofence for the previous station
        responseViewModel.getRegisteredStation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ station ->
                    FenceUpdateRequest.Builder().removeFence(station.id)
                }, {})

        //Register new fences
        val builder = FenceUpdateRequest.Builder()

        responseViewModel.station.coordinate.apply {
            builder.addFence(responseViewModel.station.id, LocationFence.entering(latitude, longitude, RADIUS), getPendingIntent())
        }

        Awareness.getFenceClient(this).updateFences(builder.build())
                .addOnSuccessListener {
                    responseViewModel.saveRegisteredStation()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failure Location", Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                }
    }

    private fun getPendingIntent() = pendingIntent
            ?: PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(this, FenceReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
            ).also {
                pendingIntent = it
            }
    //endregion

    private fun registerBeacons() {
        val cloudCredentials = EstimoteCloudCredentials(APP_ID, APP_TOKEN)
        val proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
                .withBalancedPowerMode()
                .withOnErrorAction { /* handle errors here */ }
                .build()


        // Kotlin
        val venueZone = proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("location", "halcyon")
                .inFarRange()
                .withOnEnterAction {
                    Toast.makeText(this, "Bent vagy!", Toast.LENGTH_SHORT).show()
                }
                .withOnExitAction {
                    Toast.makeText(this, "Kint vagy!", Toast.LENGTH_SHORT).show()
                }
                .create()

        observationHandler = proximityObserver.addProximityZone(venueZone).start()
    }

    private fun showPermissionRationale() {
        responseViewModel.snackbarState.apply {
            messageRes = R.string.location_permission_rationale
            actionRes = R.string.settings
            action = View.OnClickListener {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                })
            }
        }.build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BLUETOOTH_ENABLE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                registerBeacons()
            } else {
                //TODO add snackbar that bluetooth is disabled
            }
        }
    }

    companion object {

        private const val APP_TOKEN = "c0280bc13a0e76e121f2f78f58088706"
        private const val APP_ID = "allamvizsga-7j2"

        private const val BLUETOOTH_ENABLE_REQUEST_CODE = 123

        const val CORRECT_ANSWER = "correct_answer"
        const val NEXT_STATION = "next_station"

        private const val LOCATION_PERMISSION_REQUEST_CODE = 435
        private const val RADIUS = 100.0

        fun getStartIntent(context: Context, revealFrom: View, correctAnswer: Boolean?, nextStation: Station): Intent {
            //calculates the center of the View revealFrom you are passing
            val revealX = (revealFrom.x + revealFrom.width / 2).toInt()
            val revealY = (revealFrom.y + revealFrom.height / 2).toInt()
            return Intent(context, ResponseActivity::class.java)
                    .putExtra(CORRECT_ANSWER, correctAnswer)
                    .putExtra(NEXT_STATION, nextStation)
                    .putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX)
                    .putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY)
        }
    }
}
