package com.allamvizsga.tamas.feature.station

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.storage.repository.StationRepository
import com.allamvizsga.tamas.storage.repository.WalkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StationArViewModel(private val walkRepository: WalkRepository, private val stationRepository: StationRepository) : ViewModel() {

    private val _nextStation = MutableLiveData<Station>()
    val nextStation: LiveData<Station?> get() = _nextStation
    val isNextStationAvailable = ObservableBoolean(false)

    init {
        walkRepository.getNextStation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _nextStation.value = it
                    isNextStationAvailable.set(it != null)
                }, {})
    }

    fun getRegisteredStationId() = stationRepository.getRegisteredStationId()

    fun stopWalk() {
        walkRepository.stopWalk()
    }
}