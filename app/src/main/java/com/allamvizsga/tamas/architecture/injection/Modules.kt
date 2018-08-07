package com.allamvizsga.tamas.architecture.injection

import com.allamvizsga.tamas.feature.quiz.QuizActivity
import com.allamvizsga.tamas.feature.quiz.QuizViewModel
import com.allamvizsga.tamas.feature.response.ResponseActivity
import com.allamvizsga.tamas.feature.response.ResponseViewModel
import com.allamvizsga.tamas.feature.station.StationViewModel
import com.allamvizsga.tamas.feature.walk.detail.WalkDetailActivity
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
    bean { SharedPreferencesManager(get()) }
    bean { WalkRepository(get(), get()) }
    bean { StationRepository(get(), get()) }
}

val viewModelModule = applicationContext {
    viewModel { WalkListViewModel(get()) }
    viewModel { WalkDetailViewModel(get(), getProperty(WalkDetailActivity.WALK)) }
    viewModel { StationViewModel(get()) }
    viewModel { ResponseViewModel(getProperty(ResponseActivity.CORRECT_ANSWER), getProperty(ResponseActivity.NEXT_STATION), get()) }
    viewModel { QuizViewModel(getProperty(QuizActivity.QUESTION)) }
}