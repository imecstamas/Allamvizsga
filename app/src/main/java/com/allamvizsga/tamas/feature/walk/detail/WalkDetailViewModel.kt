package com.allamvizsga.tamas.feature.walk.detail

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.feature.shared.SnackbarState
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkDetailViewModel(private val walkRepository: WalkRepository, val walk: Walk) : ViewModel() {

    private val disposable: Disposable

    var buttonIconRes = ObservableInt(R.drawable.ic_play_arrow_black_24dp)
    var walkWithStations: Walk? = null
    val startEnabled = ObservableBoolean(false)
    var walkAlreadyStarted = false
    val snackbarState = SnackbarState()

    init {
        disposable = walkRepository.getById(walk.id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    walkWithStations = it
                    startEnabled.set(true)
                    if (walkRepository.getStartedWalkId() == it.id) {
                        walkAlreadyStarted = true
                        //If this walkWithStations has been already started, change the button icon
                        buttonIconRes.set(R.drawable.ic_stop_black_24dp)
                    }
                }, {
                    snackbarState.apply {
                        messageRes = R.string.there_was_a_problem_downloading_the_content
                    }.build()
                })
    }

    /**
     * This method is called when the location fences were registered successfully
     */
    fun startWalk() {
        buttonIconRes.set(R.drawable.ic_stop_black_24dp)
        walkWithStations?.id?.let { walkRepository.saveStartedWalk(it) }
        walkAlreadyStarted = true
    }

    /**
     * This method is called when a walkWithStations is started and the user wants to stop it manually
     */
    fun stopWalk() {
        snackbarState.apply {
            messageRes = R.string.walk_stopped
        }.build()
        buttonIconRes.set(R.drawable.ic_play_arrow_black_24dp)
        walkRepository.stopWalk()
        walkAlreadyStarted = false
    }

    override fun onCleared() {
        disposable.dispose()
    }
}