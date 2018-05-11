package com.allamvizsga.tamas.feature.walk.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableInt
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.halcyonmobile.statelayout.StateLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkListViewModel(walkRepository: WalkRepository) : ViewModel() {

    private val _walks = MutableLiveData<List<Walk>>()
    val walks: LiveData<List<Walk>> get() = _walks
    val screenState = ObservableInt(StateLayout.State.LOADING)
    private val disposable: Disposable

    init {
        disposable = walkRepository.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ walks ->
                if (walks.isEmpty()) {
                    screenState.set(StateLayout.State.EMPTY)
                } else {
                    _walks.value = walks
                    screenState.set(StateLayout.State.NORMAL)
                }
            }, { error ->
                error.printStackTrace()
                screenState.set(StateLayout.State.ERROR)
            })
    }

    override fun onCleared() {
        disposable.dispose()
    }
}