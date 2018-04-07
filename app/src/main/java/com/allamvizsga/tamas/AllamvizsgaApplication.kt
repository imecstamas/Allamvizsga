package com.allamvizsga.tamas

import android.app.Application
import com.allamvizsga.tamas.architecture.injection.dataBaseReferenceModule
import com.allamvizsga.tamas.architecture.injection.viewModelModule
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.android.startKoin

class AllamvizsgaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        startKoin(this, listOf(dataBaseReferenceModule, viewModelModule))
    }
}