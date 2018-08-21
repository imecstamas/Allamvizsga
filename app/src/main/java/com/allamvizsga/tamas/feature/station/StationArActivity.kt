package com.allamvizsga.tamas.feature.station

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.model.Station
import com.google.android.exoplayer2.ExoPlayerFactory
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
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


@RequiresApi(Build.VERSION_CODES.N)
class StationArActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    private var videoRenderable: ModelRenderable? = null
    private lateinit var player: SimpleExoPlayer
    private var videoWidth: Float = 0F
    private var videoHeight: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station_ar_activity)
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

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, _: Plane, _: MotionEvent ->

            videoRenderable?.let {
                arFragment.setOnTapArPlaneListener(null)
                // Create the Anchor.
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment.arSceneView.scene)

                // Create a node to render the video and add it to the anchor.
                //          Node videoNode = new Node();
                val videoNode = TransformableNode(arFragment.transformationSystem)
                videoNode.setParent(anchorNode)

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

        fun getStartIntent(context: Context, station: Station): Intent =
                Intent(context, StationArActivity::class.java).putExtra(STATION, station)
    }
}
