package com.uvg.mashoras.data

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.local.AppDatabase
import com.uvg.mashoras.data.local.UserPreferencesDataSource
import com.uvg.mashoras.data.local.userPrefsDataStore
import com.uvg.mashoras.data.remote.FirebaseActivitiesDataSource
import com.uvg.mashoras.data.remote.FirebaseAuthDataSource
import com.uvg.mashoras.data.repository.ActivitiesRepositoryImpl
import com.uvg.mashoras.data.repository.AuthRepositoryImpl
import com.uvg.mashoras.domain.repository.ActivitiesRepository
import com.uvg.mashoras.domain.repository.AuthRepository

class AppContainer(context: Context) {

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "mas_horas_db"
    ).fallbackToDestructiveMigration().build()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val userPrefs = UserPreferencesDataSource(context.userPrefsDataStore)

    val authRepository: AuthRepository =
        AuthRepositoryImpl(FirebaseAuthDataSource(auth, firestore))

    val activitiesRepository: ActivitiesRepository =
        ActivitiesRepositoryImpl(FirebaseActivitiesDataSource(firestore), db.activityDao())
}
