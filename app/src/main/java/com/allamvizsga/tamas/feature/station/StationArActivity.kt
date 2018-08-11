package com.allamvizsga.tamas.feature.station

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.model.Station
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
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station_ar_activity)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        // Create an ExternalTexture for displaying the contents of the video.
        val texture = ExternalTexture()

        // Create an Android MediaPlayer to capture the video on the external texture's surface.
        mediaPlayer = MediaPlayer.create(this, R.raw.chicken_chroma)
        mediaPlayer.setSurface(texture.surface)
        mediaPlayer.isLooping = true

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
                .exceptionally { throwable ->
                    val toast = Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->

            videoRenderable?.let {
                // Create the Anchor.
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment.arSceneView.scene)

                // Create a node to render the video and add it to the anchor.
                //          Node videoNode = new Node();
                val videoNode = TransformableNode(arFragment.transformationSystem)
                videoNode.setParent(anchorNode)

                // Set the scale of the node so that the aspect ratio of the video is correct.
                val videoWidth = mediaPlayer.videoWidth.toFloat()
                val videoHeight = mediaPlayer.videoHeight.toFloat()
                videoNode.localScale = Vector3(
                        VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f)

                // Start playing the video when the first node is placed.
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()

                    // Wait to set the renderable until the first frame of the  video becomes available.
                    // This prevents the renderable from briefly appearing as a black quad before the video
                    // plays.
                    texture
                            .surfaceTexture
                            .setOnFrameAvailableListener { surfaceTexture: SurfaceTexture ->
                                videoNode.renderable = videoRenderable
                                texture.surfaceTexture.setOnFrameAvailableListener(null)
                            }
                } else {
                    videoNode.renderable = videoRenderable
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
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
