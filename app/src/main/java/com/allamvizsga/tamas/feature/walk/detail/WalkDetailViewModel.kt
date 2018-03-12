package com.allamvizsga.tamas.feature.walk.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkDetailViewModel(private val walkRepository: WalkRepository) : ViewModel() {

    private var disposable: Disposable? = null

    val walk = MutableLiveData<Walk>()

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

    override fun onCleared() {
        disposable?.dispose()
    }
}