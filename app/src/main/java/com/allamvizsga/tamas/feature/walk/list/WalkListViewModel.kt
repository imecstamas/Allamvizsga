package com.allamvizsga.tamas.feature.walk.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkListViewModel(walkRepository: WalkRepository) : ViewModel() {

    private val _walks = MutableLiveData<List<Walk>>()
    val walks: LiveData<List<Walk>> get() = _walks
    private val disposable: Disposable

    init {
        disposable = walkRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ walks ->
                    _walks.value = walks
                }, { error ->
                    error.printStackTrace()
                })
    }

    override fun onCleared() {
        disposable.dispose()
    }
}