package com.allamvizsga.tamas.feature.walk.detail

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WalkDetailViewModel(private val walkRepository: WalkRepository) : ViewModel() {

    private var disposable: Disposable? = null
    private var stations: List<Station>? = null

    val imageUrl = ObservableField<String>()

    fun getWalkDetail(walkId: String) {
        disposable = walkRepository.getById(walkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stations = it.stations
                }, {
                    it.printStackTrace()
                })
    }

    fun startWalk() {
        stations?.forEach {
            //TODO register geofences for stations
        }
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}