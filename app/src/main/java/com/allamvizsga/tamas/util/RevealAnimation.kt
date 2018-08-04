package com.allamvizsga.tamas.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator


class RevealAnimation(private val view: View, intent: Intent, private val activity: Activity) {

    private var revealX: Int = 0
    private var revealY: Int = 0

    init {

        //when you're android version is at leat Lollipop it starts the reveal activity
        if (intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            view.visibility = View.INVISIBLE

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)

            val viewTreeObserver = view.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX, revealY)
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            view.visibility = View.VISIBLE
        }
    }

    fun revealActivity(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(view.width, view.height) * 1.1).toFloat()

            // create the animator for this view (the start radius is zero)
            val circularReveal = ViewAnimationUtils.createCircularReveal(view, x, y, 0f, finalRadius)
            circularReveal.duration = 300
            circularReveal.interpolator = AccelerateInterpolator()

            // make the view visible and start the animation
            view.visibility = View.VISIBLE
            circularReveal.start()
        } else {
            activity.finish()
        }
    }

    fun unRevealActivity() {
        val finalRadius = (Math.max(view.width, view.height) * 1.1).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                view, revealX, revealY, finalRadius, 0f)

        circularReveal.duration = 300
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.INVISIBLE
                activity.finish()
                activity.overridePendingTransition(0, 0)
            }
        })

        circularReveal.start()
    }

    companion object {
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }
}