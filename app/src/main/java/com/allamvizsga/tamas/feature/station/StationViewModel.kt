package com.allamvizsga.tamas.feature.station

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableInt
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.storage.repository.StationRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StationViewModel(private val stationsRepository: StationRepository) : ViewModel() {

    private var disposable: Disposable? = null
    private var seekBarUpdater: Disposable? = null
    private var mediaPlayer: MediaPlayerHolder? = null
    private var audioUrl: String? = null
    val fabResource = ObservableInt(R.drawable.ic_play_arrow_black_24dp)
    val progressPosition = ObservableInt(0)
    val maxProgress = ObservableInt(0)

    fun getStationById(id: String) {
        disposable = stationsRepository.getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ station ->
                    audioUrl = station.audioUrl
                    initPlayer(station.audioUrl)
                }, {
                    it.printStackTrace()
                })
    }

    private fun initPlayer(audioUrl: String, playIfReady: Boolean = false) {
        mediaPlayer = MediaPlayerHolder(audioUrl).apply {
            onPreparedListener = { duration ->
                maxProgress.set(duration)
                if (playIfReady) {
                    play(this)
                }
            }
            onCompletedListener = {
                seekBarUpdater?.dispose()
                fabResource.set(R.drawable.ic_play_arrow_black_24dp)
                mediaPlayer = null
            }
        }
    }

    fun onButtonClicked() {
        mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause()
                fabResource.set(R.drawable.ic_play_arrow_black_24dp)
                seekBarUpdater?.dispose()
            } else {
                play(mediaPlayer)
            }
        } ?: audioUrl?.let {
            initPlayer(it, true)
        }
    }

    private fun play(mediaPlayer: MediaPlayerHolder) {
        mediaPlayer.play()
        fabResource.set(R.drawable.ic_pause_black_24dp)
        seekBarUpdater = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribe({
                    mediaPlayer.let {
                        progressPosition.set(it.getCurrentPosition())
                    }
                })
    }

    fun onProgressChanged(position: Int, fromUser: Boolean) {
        if (fromUser) {
            mediaPlayer?.seekTo(position)
        }
    }

    override fun onCleared() {
        disposable?.dispose()
        seekBarUpdater?.dispose()
        mediaPlayer?.release()
    }
}