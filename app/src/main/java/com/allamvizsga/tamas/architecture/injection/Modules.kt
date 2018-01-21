package com.allamvizsga.tamas.architecture.injection

import com.allamvizsga.tamas.storage.repository.WalkRepository
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module.applicationContext

val dataBaseReferenceModule = applicationContext {
    provide { FirebaseDatabase.getInstance().reference }
    provide { WalkRepository(get()) }
}