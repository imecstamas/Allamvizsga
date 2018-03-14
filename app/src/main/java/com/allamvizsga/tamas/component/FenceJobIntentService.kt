package com.allamvizsga.tamas.component

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import com.allamvizsga.tamas.storage.repository.StationRepository
import com.google.android.gms.awareness.fence.FenceState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class FenceJobIntentService : JobIntentService() {

    private val stationRepository: StationRepository by inject()

    override fun onHandleWork(intent: Intent) {
        val fenceState = FenceState.extract(intent)
        if (fenceState.currentState == FenceState.TRUE) {
            //the fenceKey is the id of the Station
            stationRepository.getById(fenceState.fenceKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ station ->
                        NotificationHelper.sendNotification(this, station = station)
                    }, {
                        NotificationHelper.sendNotification(this, stationId = fenceState.fenceKey)
                    })
        }
    }

    companion object {

        private const val JOB_ID = 123

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, FenceJobIntentService::class.java, JOB_ID, intent)
        }
    }
}