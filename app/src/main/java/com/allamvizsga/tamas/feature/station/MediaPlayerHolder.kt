package com.allamvizsga.tamas.feature.station

import android.media.MediaPlayer

class MediaPlayerHolder(audioUrl: String) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    lateinit var onPreparedListener: (duration: Int) -> Unit
    lateinit var onCompletedListener: () -> Unit

    init {
        mediaPlayer.setDataSource(audioUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPreparedListener.invoke(mediaPlayer.duration)
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            onCompletedListener.invoke()
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun release() {
        mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer.isPlaying
        } catch (exception: IllegalStateException) {
            false
        }
    }

    fun getCurrentPosition() = mediaPlayer.currentPosition
}