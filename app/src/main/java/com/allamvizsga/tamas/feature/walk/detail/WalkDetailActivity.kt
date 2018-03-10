package com.allamvizsga.tamas.feature.walk.detail

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.component.FenceReceiver
import com.allamvizsga.tamas.databinding.WalkDetailActivityBinding
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.util.extension.runWithPermission
import com.allamvizsga.tamas.util.extension.setUpToolbar
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.LocationFence
import org.koin.android.architecture.ext.getViewModel

class WalkDetailActivity : BaseActivity() {

    private var pendingIntent: PendingIntent? = null
    private lateinit var viewModel: WalkDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<WalkDetailActivityBinding>(this, R.layout.walk_detail_activity).also { binding ->
            viewModel = getViewModel<WalkDetailViewModel>().apply {
                imageUrl.set(intent.getStringExtra(IMAGE_URL))
                getWalkDetail(intent.getStringExtra(WALK_ID))
            }
            binding.viewModel = viewModel
            setUpToolbar(binding.toolbar)
            binding.button.setOnClickListener {
                runWithPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE, ::registerLocationFence, ::showPermissionRationale)
            }
        }
    }

    private fun getPendingIntent() = pendingIntent
            ?: PendingIntent.getBroadcast(this, 0, Intent(this, FenceReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT).also {
                pendingIntent = it
            }

    private fun showPermissionRationale() {
        TODO("Show permission rationale!")
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationFence() {
        val builder = FenceUpdateRequest.Builder()

        viewModel.stations?.forEach { station ->
            station.coordinate.apply {
                builder.addFence(FenceReceiver.FENCE_KEY_IN, LocationFence.`in`(latitude, longitude, RADIUS, 0), getPendingIntent())
                        .addFence(FenceReceiver.FENCE_KEY_ENTERING, LocationFence.entering(latitude, longitude, RADIUS), getPendingIntent())
            }
        }

        Awareness.getFenceClient(this).updateFences(builder.build())
                .addOnSuccessListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerLocationFence()
                } else {
                    TODO("Denied! Disable functionality")
                }
            }
        }
    }

    companion object {

        private const val PERMISSION_REQUEST_CODE = 435
        private const val RADIUS = 100.0

        private const val WALK_ID = "walk_id"
        private const val IMAGE_URL = "image_url"

        fun getStartIntent(context: Context, walkId: String, imageUrl: String): Intent =
                Intent(context, WalkDetailActivity::class.java).putExtra(WALK_ID, walkId).putExtra(IMAGE_URL, imageUrl)
    }
}
