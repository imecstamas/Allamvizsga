package com.allamvizsga.tamas.feature.walk.detail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.SharedElementCallback
import android.view.View
import android.widget.ImageView
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkDetailActivityBinding
import com.allamvizsga.tamas.feature.quiz.QuizActivity
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.util.extension.setUpToolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class WalkDetailActivity : BaseActivity() {

    private lateinit var binding: WalkDetailActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()

        binding = DataBindingUtil.setContentView(this, R.layout.walk_detail_activity)
        binding.setLifecycleOwner(this)

        val walk = intent.getParcelableExtra<Walk>(WALK)
        val viewModel = getViewModel<WalkDetailViewModel> { parametersOf(walk) }
        binding.viewModel = viewModel

        scheduleStartPostponeEnterTransitionOnLoad(binding.imageView, walk.imageUrl)

        setUpToolbar(binding.toolbar)
        binding.button.setOnClickListener {
            if (viewModel.walkAlreadyStarted) {
                //We need to stop the walkWithStations
                viewModel.stopWalk()
                //Unregister the geofence for the previous station
                viewModel.getRegisteredStationId()?.let { stationId ->
                    Awareness.getFenceClient(this).updateFences(FenceUpdateRequest.Builder().removeFence(stationId).build())
                }
            } else {
                //We need to start the walkWithStations, ask the first question
                viewModel.startWalk()
                startActivity(QuizActivity.getStartIntent(this, viewModel.walkWithStations?.stations!![0]))
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

        private const val WALK = "walkWithStations"

        fun getStartIntent(context: Context, walk: Walk): Intent =
                Intent(context, WalkDetailActivity::class.java).putExtra(WALK, walk)
    }
}