package com.allamvizsga.tamas.architecture.injection

import com.allamvizsga.tamas.feature.station.StationViewModel
import com.allamvizsga.tamas.feature.walk.detail.WalkDetailViewModel
import com.allamvizsga.tamas.feature.walk.list.WalkListViewModel
import com.allamvizsga.tamas.storage.preference.SharedPreferencesManager
import com.allamvizsga.tamas.storage.repository.StationRepository
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val storageModule = applicationContext {
    bean { FirebaseDatabase.getInstance().reference }
    bean { WalkRepository(get()) }
    bean { StationRepository(get()) }
    bean { SharedPreferencesManager(get()) }
}

val viewModelModule = applicationContext {
    viewModel { WalkListViewModel(get()) }
    viewModel { WalkDetailViewModel(get()) }
    viewModel { StationViewModel(get()) }
}