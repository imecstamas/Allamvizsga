package com.allamvizsga.tamas.architecture.injection

import com.allamvizsga.tamas.feature.quiz.QuizViewModel
import com.allamvizsga.tamas.feature.response.ResponseViewModel
import com.allamvizsga.tamas.feature.station.StationArViewModel
import com.allamvizsga.tamas.feature.station.StationViewModel
import com.allamvizsga.tamas.feature.walk.detail.WalkDetailViewModel
import com.allamvizsga.tamas.feature.walk.list.WalkListViewModel
import com.allamvizsga.tamas.model.Question
import com.allamvizsga.tamas.model.Station
import com.allamvizsga.tamas.model.Walk
import com.allamvizsga.tamas.storage.preference.SharedPreferencesManager
import com.allamvizsga.tamas.storage.repository.StationRepository
import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val storageModule = module {
    single { FirebaseDatabase.getInstance().reference }
    single { SharedPreferencesManager(get()) }
    single { WalkRepository(get(), get()) }
    single { StationRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { WalkListViewModel(get()) }
    viewModel { (walk: Walk) -> WalkDetailViewModel(get(), get(), walk) }
    viewModel { StationViewModel(get()) }
    viewModel { (isCorrectAnswer: Boolean, nextStation: Station) -> ResponseViewModel(isCorrectAnswer, nextStation, get()) }
    viewModel { (question: Question) -> QuizViewModel(question) }
    viewModel { StationArViewModel(get(), get()) }
}