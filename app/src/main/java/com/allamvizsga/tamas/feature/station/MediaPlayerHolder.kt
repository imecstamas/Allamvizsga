package com.allamvizsga.tamas.feature.station

import android.media.MediaPlayer

class MediaPlayerHolder(audioUrl: String) {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    init {
        mediaPlayer.setDataSource(audioUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play()
        }
        mediaPlayer.setOnCompletionListener {
            release()
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
}