package com.allamvizsga.tamas.feature.station

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.StationArActivityBinding
import com.allamvizsga.tamas.feature.quiz.QuizActivity
import com.allamvizsga.tamas.feature.walk.list.WalkListActivity
import com.allamvizsga.tamas.model.Station
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import org.koin.android.viewmodel.ext.android.getViewModel

@RequiresApi(Build.VERSION_CODES.N)
class StationArActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    private var videoRenderable: ModelRenderable? = null
    private lateinit var player: SimpleExoPlayer
    private var videoWidth: Float = 0F
    private var videoHeight: Float = 0F
    private var needsToBeAdded: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<StationArActivityBinding>(this, R.layout.station_ar_activity).apply {
            val stationViewModel = getViewModel<StationArViewModel>()
            viewModel = stationViewModel
            replay.setOnClickListener {
                player.seekTo(0)
                player.playWhenReady = true
            }
            nextStation.setOnClickListener {
                stationViewModel.nextStation.value.let { nextStation ->
                    if (nextStation != null) {
                        startActivity(QuizActivity.getStartIntent(this@StationArActivity, nextStation))
                    } else {
                        //TODO stop walk/unregister fence/remove from shared prefs
                        startActivity(WalkListActivity.getStartIntent(this@StationArActivity))
                    }
                }
            }
        }
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        // Create an ExternalTexture for displaying the contents of the video.
        val texture = ExternalTexture()

        // Create an Android MediaPlayer to capture the video on the external texture's surface.
        // 1. Create a default TrackSelector
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), null)
        // This is the MediaSource representing the media to be played.
        val audioUrl = intent.getParcelableExtra<Station>(STATION).audioUrl
        val audioSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(audioUrl))
        // The video file shouldn't have any audio tracks
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.test))
        // Prepare the player with the source.
        player.prepare(MergingMediaSource(audioSource, videoSource))
        player.setVideoSurface(texture.surface)
        player.addListener(object : EventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    binding.buttons.visibility = View.VISIBLE
                } else {
                    binding.buttons.visibility = View.GONE
                }
            }
        })

        // Create a renderable with a material that has a parameter of type 'samplerExternal' so that
        // it can display an ExternalTexture. The material also has an implementation of a chroma key
        // filter.
        ModelRenderable.builder()
                .setSource(this, R.raw.chroma_key_video)
                .build()
                .thenAccept { renderable ->
                    videoRenderable = renderable
                    renderable.material.setExternalTexture("videoTexture", texture)
                    renderable.material.setFloat4("keyColor", CHROMA_KEY_COLOR)
                }
                .exceptionally { _ ->
                    val toast = Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }

        player.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                videoWidth = width.toFloat()
                videoHeight = height.toFloat()
            }

            override fun onRenderedFirstFrame() {
            }
        })

        arFragment.arSceneView.scene.addOnUpdateListener {
            //get the frame from the scene for shorthand
            val frame = arFragment.arSceneView.arFrame
            if (frame != null) {
                //get the trackables to ensure planes are detected
                val var3 = frame.getUpdatedTrackables(Plane::class.java).iterator()
                while (var3.hasNext()) {
                    val plane = var3.next() as Plane

                    //If a plane has been detected & is being tracked by ARCore
                    if (plane.trackingState == TrackingState.TRACKING) {

                        //Hide the plane discovery helper animation
//                        arFragment.planeDiscoveryController.hide()

                        if (needsToBeAdded) {
                            needsToBeAdded = false
                            //Perform a hit test at the center of the screen to place an object without tapping
                            val hitTest = frame.hitTest(screenCenter().x, screenCenter().y)

                            //iterate through all hits
                            val hitTestIterator = hitTest.iterator()
                            val hitResult = hitTestIterator.next()

                            // Create the Anchor.
                            val anchor = hitResult.createAnchor()
                            val anchorNode = AnchorNode(anchor)
                            anchorNode.setParent(arFragment.arSceneView.scene)

                            // Create a node to render the video and add it to the anchor.
                            //          Node videoNode = new Node();
                            val videoNode = TransformableNode(arFragment.transformationSystem)
                            videoNode.setParent(anchorNode)
                            //Alter the real world position to ensure object renders on the table top. Not somewhere inside.
                            videoNode.worldPosition = Vector3(anchor.pose.tx(),
                                    anchor.pose.compose(Pose.makeTranslation(0f, 0.05f, 0f)).ty(),
                                    anchor.pose.tz())

                            // Set the scale of the node so that the aspect ratio of the video is correct.
                            videoNode.localScale = Vector3(
                                    VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f)

                            // Start playing the video when the first node is placed.
                            player.playWhenReady = true


                            // Wait to set the renderable until the first frame of the  video becomes available.
                            // This prevents the renderable from briefly appearing as a black quad before the video
                            // plays.
                            texture.surfaceTexture.setOnFrameAvailableListener { _: SurfaceTexture ->
                                videoNode.renderable = videoRenderable
                                texture.surfaceTexture.setOnFrameAvailableListener(null)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun screenCenter(): Vector3 {
        val vw = findViewById<View>(android.R.id.content)
        return Vector3(vw.width / 2f, vw.height / 2f, 0f)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {

        // The color to filter out of the video.
        private val CHROMA_KEY_COLOR = Color(0.1843f, 1.0f, 0.098f)

        // Controls the height of the video in world space.
        private const val VIDEO_HEIGHT_METERS = 0.85f

        private const val STATION = "station"
        private const val STATION_ID = "station_id"

        fun getStartIntent(context: Context, station: Station? = null, stationId: String? = null): Intent =
                Intent(context, StationArActivity::class.java).putExtra(STATION_ID, stationId).putExtra(STATION, station)
    }
}
