package com.allamvizsga.tamas.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    fun getRegisteredFenceKeys(): Set<String?>? = sharedPreferences.getStringSet(REGISTERED_FENCES, null)

    fun saveRegisteredFenceKeys(fenceKeys: Set<String?>) {
        sharedPreferences.edit().putStringSet(REGISTERED_FENCES, fenceKeys).apply()
    }

    companion object {
        private const val REGISTERED_FENCES = "registered_fences"
    }
}