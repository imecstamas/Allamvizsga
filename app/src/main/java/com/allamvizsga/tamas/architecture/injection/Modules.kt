package com.allamvizsga.tamas.architecture.injection

import com.allamvizsga.tamas.feature.walk.detail.WalkDetailViewModel
import com.allamvizsga.tamas.feature.walk.list.WalkListViewModel
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val dataBaseReferenceModule = applicationContext {
    provide { FirebaseDatabase.getInstance().reference }
    provide { WalkRepository(get()) }
}

val viewModelModule = applicationContext {
    viewModel { WalkListViewModel(get()) }
    viewModel { WalkDetailViewModel(get()) }
}