package com.allamvizsga.tamas.feature.walk.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableInt
import android.view.View
import com.allamvizsga.tamas.feature.shared.SnackbarState
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.allamvizsga.tamas.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkDetailViewModel(private val walkRepository: WalkRepository) : ViewModel() {

    private var disposable: Disposable? = null

    var buttonIconRes = ObservableInt(R.drawable.ic_play_arrow_black_24dp)
    val walk = MutableLiveData<Walk>()
    val startEnabled get() = walk.value != null
    val openSettings = SingleLiveEvent<Any>()
    val snackbarState = SnackbarState(
        R.string.location_permission_rationale,
        actionRes = R.string.settings,
        action = View.OnClickListener { openSettings.call() })

    fun getWalkDetail(walkId: String) {
        disposable = walkRepository.getById(walkId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                walk.value = it
            }, {
                it.printStackTrace()
            })
    }

    fun fenceRegistrationSuccess() {
        buttonIconRes.set(R.drawable.ic_stop_black_24dp)
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}