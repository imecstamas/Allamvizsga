package com.allamvizsga.tamas.feature.response

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.ResponseBinding
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.util.RevealAnimation
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.ext.android.setProperty


class ResponseActivity : AppCompatActivity() {

    lateinit var revealAnimation: RevealAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ResponseBinding>(this, R.layout.response_activity).apply {
            setProperty(CORRECT_ANSWER, intent.getBooleanExtra(CORRECT_ANSWER, false))
            val station = intent.getParcelableExtra<Station>(NEXT_STATION)
            setProperty(NEXT_STATION, station)
            viewModel = getViewModel<ResponseViewModel>()
            revealAnimation = RevealAnimation(root, intent, this@ResponseActivity)
            navigateButton.setOnClickListener {
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${station.coordinate.latitude},${station.coordinate.longitude}"))
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                }
            }
        }
    }

    override fun onBackPressed() {
        revealAnimation.unRevealActivity()
    }

    fun registerGeofence() {
        
    }

    companion object {

        const val CORRECT_ANSWER = "correct_answer"
        const val NEXT_STATION = "next_station"

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
