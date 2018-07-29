package com.allamvizsga.tamas.feature.walk.detail

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.SharedElementCallback
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.component.FenceReceiver
import com.allamvizsga.tamas.databinding.WalkDetailActivityBinding
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.allamvizsga.tamas.util.extension.runWithPermission
import com.allamvizsga.tamas.util.extension.setUpToolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials
import com.estimote.proximity_sdk.proximity.ProximityObserver
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.inject

class WalkDetailActivity : BaseActivity() {

    private var pendingIntent: PendingIntent? = null
    private lateinit var binding: WalkDetailActivityBinding
    private lateinit var viewModel: WalkDetailViewModel
    private val walkRepository: WalkRepository by inject()

    private var observationHandler: ProximityObserver.Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        binding = DataBindingUtil.setContentView(this, R.layout.walk_detail_activity)
        binding.setLifecycleOwner(this)
        viewModel = getViewModel<WalkDetailViewModel>().also { viewModel ->
            intent.getParcelableExtra<Walk>(WALK).also { walk ->
                walk.id?.let {
                    viewModel.getWalkDetail(it)
                }
                viewModel.walk.value = walk
                scheduleStartPostponeEnterTransitionOnLoad(binding.imageView, walk.imageUrl)
            }
        }

        binding.viewModel = viewModel
        setUpToolbar(binding.toolbar)
        binding.button.setOnClickListener {
            if (viewModel.walkAlreadyStarted) {
                //We need to stop the walk
                unregisterLocationFence()
                viewModel.stopWalk()
            } else {
                //We need to start the walk
                //                startActivity(StationDetailActivity.getStartIntent(this, viewModel.walk.value!!.stations!![0].id!!))
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
                        startActivityForResult(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            BLUETOOTH_ENABLE_REQUEST_CODE
                        )
                    }
                }
            }
        }
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                Handler().postDelayed({
                    binding.button.show()
                }, 800)
            }
        })
    }

    override fun supportFinishAfterTransition() {
        binding.button.visibility = View.GONE
       finish()
    }

    private fun showPermissionRationale() {
        viewModel.snackbarState.apply {
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

    //region LOCATION FENCES
    private fun startLocationServices() {
        unregisterLocationFence()
        registerLocationFence()
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationFence() {
        // Register new fences
        val builder = FenceUpdateRequest.Builder()

        viewModel.walk.value?.stations?.forEach { station ->
            station.coordinate.apply {
                builder.addFence(station.id, LocationFence.entering(latitude, longitude, RADIUS), getPendingIntent())
            }
        }

        Awareness.getFenceClient(this).updateFences(builder.build())
            .addOnSuccessListener {
                viewModel.startWalk()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failure Location", Toast.LENGTH_SHORT).show()
                it.printStackTrace()
            }
    }

    /**
     * Unregister previously registered fences
     */
    private fun unregisterLocationFence() {
        FenceUpdateRequest.Builder().apply {
            walkRepository.getStartedWalk()?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    it.stations?.forEach {
                        removeFence(it.id)
                    }
                }, {})
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

    override fun onDestroy() {
        //TODO check if we want to stop here the observation
        observationHandler?.stop()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices()
                } else {
                    viewModel.snackbarState.apply {
                        messageRes = R.string.location_disabled
                    }.build()
                }
            }
        }
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

    private fun scheduleStartPostponeEnterTransitionOnLoad(imageView: ImageView, url: String) {
        Glide.with(imageView).load(url).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }
        }).into(imageView)
    }

    companion object {

        private const val LOCATION_PERMISSION_REQUEST_CODE = 435
        private const val BLUETOOTH_ENABLE_REQUEST_CODE = 123
        private const val RADIUS = 100.0

        private const val APP_TOKEN = "c0280bc13a0e76e121f2f78f58088706"
        private const val APP_ID = "allamvizsga-7j2"

        private const val WALK = "walk"

        fun getStartIntent(context: Context, walk: Walk): Intent =
            Intent(context, WalkDetailActivity::class.java).putExtra(WALK, walk)
    }
}