package com.allamvizsga.tamas.feature.walk.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableInt
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.feature.shared.SnackbarState
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkDetailViewModel(private val walkRepository: WalkRepository) : ViewModel() {

    private var disposable: Disposable? = null

    var buttonIconRes = ObservableInt(R.drawable.ic_play_arrow_black_24dp)
    val walk = MutableLiveData<Walk>()
    val startEnabled get() = walk.value != null
    var walkAlreadyStarted = false
    val snackbarState = SnackbarState()

    fun getWalkDetail(walkId: String) {
        disposable = walkRepository.getById(walkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    walk.value = it
                    if (walkRepository.getStartedWalkId() == it.id) {
                        walkAlreadyStarted = true
                        //If this walk has been already started, change the button icon
                        buttonIconRes.set(R.drawable.ic_stop_black_24dp)
                    }
                }, {
                    it.printStackTrace()
                })
    }

    /**
     * This method is called when the location fences were registered successfully
     */
    fun startWalk() {
        buttonIconRes.set(R.drawable.ic_stop_black_24dp)
        walk.value?.id?.let { walkRepository.saveStartedWalk(it) }
        snackbarState.apply {
            messageRes = R.string.walk_started
        }.build()
        walkAlreadyStarted = true
    }

    /**
     * This method is called when a walk is started and the user wants to stop it manually
     */
    fun stopWalk() {
        buttonIconRes.set(R.drawable.ic_play_arrow_black_24dp)
        walkRepository.stopWalk()
        walkAlreadyStarted = false
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}