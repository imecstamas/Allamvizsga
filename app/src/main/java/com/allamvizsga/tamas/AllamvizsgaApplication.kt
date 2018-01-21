package com.allamvizsga.tamas

import android.app.Application
import com.allamvizsga.tamas.architecture.injection.dataBaseReferenceModule
import com.allamvizsga.tamas.architecture.injection.viewModelModule
import org.koin.android.ext.android.startKoin

class AllamvizsgaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(dataBaseReferenceModule, viewModelModule))
    }
}