package com.allamvizsga.tamas.walklist

import android.arch.lifecycle.ViewModel
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkListViewModel(walkRepository: WalkRepository) : ViewModel() {

    private val disposable: Disposable

    init {
        disposable = walkRepository.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ walks ->
                println("Walks: $walks")
            }, { error ->
                error.printStackTrace()
            })
    }

    override fun onCleared() {
        disposable.dispose()
    }
}