package com.allamvizsga.tamas.feature.station

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableInt
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.storage.repository.StationRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StationViewModel(private val stationsRepository: StationRepository) : ViewModel() {

    private var disposable: Disposable? = null
    private var station: Station? = null
    private var mediaPlayer: MediaPlayerHolder? = null
    val fabResource = ObservableInt(R.drawable.ic_play_arrow_black_24dp)

    fun getStationById(id: String) {
        disposable = stationsRepository.getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    station = it
                }, {
                    it.printStackTrace()
                })
    }

    fun onButtonClicked() {
        mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause()
                fabResource.set(R.drawable.ic_play_arrow_black_24dp)
            } else {
                mediaPlayer.play()
                fabResource.set(R.drawable.ic_pause_black_24dp)
            }
        } ?: station?.let { station ->
            mediaPlayer = MediaPlayerHolder(station.audioUrl)
            fabResource.set(R.drawable.ic_pause_black_24dp)
        }
    }

    override fun onCleared() {
        disposable?.dispose()
        mediaPlayer?.release()
    }
}