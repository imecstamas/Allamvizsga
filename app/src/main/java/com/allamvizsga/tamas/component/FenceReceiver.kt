package com.allamvizsga.tamas.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.awareness.fence.FenceState

class FenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // The state information for the given fence is em
        val fenceState = FenceState.extract(intent)
        when (fenceState.fenceKey) {
            FENCE_KEY_ENTERING -> when (fenceState.currentState) {
                FenceState.TRUE -> {
                    NotificationHelper.sendNotification(context!!, "Beleptel az adott pontra!")
                    Toast.makeText(context, "Beleptel True", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Beleptel True")
                }
                FenceState.FALSE -> {
                    Toast.makeText(context, "Beleptel False", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Beleptel False")
                }
                FenceState.UNKNOWN -> {
                    Toast.makeText(context, "Beleptel Unkown", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Beleptel Unknown")
                }
            }
            FENCE_KEY_IN -> when (fenceState.currentState) {
                FenceState.TRUE -> {
                    NotificationHelper.sendNotification(context!!, "Bent vagy az adott ponton!")
                    Toast.makeText(context, "Bent vagy True", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Bent vagy True")
                }
                FenceState.FALSE -> {
                    Toast.makeText(context, "Bent vagy False", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Bent vagy False")
                }
                FenceState.UNKNOWN -> {
                    Toast.makeText(context, "Bent vagy Unknown", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Bent vagy Unknown")
                }
            }
            else -> Log.i(TAG, "Unknown fenceKey")
        }
    }

    companion object {
        const val FENCE_KEY_IN = "location_fence_in"
        const val FENCE_KEY_ENTERING = "location_fence_entering"
        private const val TAG = "FenceReceiver"
    }
}