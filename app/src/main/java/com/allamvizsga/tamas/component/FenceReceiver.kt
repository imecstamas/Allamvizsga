package com.allamvizsga.tamas.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class FenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        FenceJobIntentService.enqueueWork(context, intent)
    }
}