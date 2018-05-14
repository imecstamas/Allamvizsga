package com.allamvizsga.tamas.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    fun saveStartedWalkId(walkId: String) {
        sharedPreferences.edit().putString(STARTED_WALK_ID, walkId).apply()
    }

    fun stopWalk() {
        sharedPreferences.edit().remove(STARTED_WALK_ID).apply()
    }

    fun getStartedWalkId(): String? = sharedPreferences.getString(STARTED_WALK_ID, null)

    companion object {
        private const val STARTED_WALK_ID = "started_walk_id"
    }
}