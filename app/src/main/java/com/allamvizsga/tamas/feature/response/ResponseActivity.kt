package com.allamvizsga.tamas.feature.response

import android.annotation.SuppressLint
import android.app.PendingIntent
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
import com.allamvizsga.tamas.feature.station.StationArActivity
import com.allamvizsga.tamas.feature.station.StationDetailActivity
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.RevealAnimation
import com.allamvizsga.tamas.util.extension.isDeviceSupported
import com.allamvizsga.tamas.util.extension.runWithPermission
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf


class ResponseActivity : AppCompatActivity() {

    lateinit var revealAnimation: RevealAnimation
    lateinit var responseViewModel: ResponseViewModel
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ResponseBinding>(this, R.layout.response_activity).apply {
            viewModel = getViewModel<ResponseViewModel> { parametersOf(intent.getBooleanExtra(CORRECT_ANSWER, false), intent.getParcelableExtra(NEXT_STATION)) }.also {
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

    private fun registerStation() {
//        //TODO remove this
//        if (isDeviceSupported()) {
//            startActivity(StationArActivity.getStartIntent(this, responseViewModel.station))
//        } else {
//            startActivity(StationDetailActivity.getStartIntent(this, responseViewModel.station))
//        }
        runWithPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE,
                ::startLocationServices,
                ::showPermissionRationale
        )
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

    companion object {

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
