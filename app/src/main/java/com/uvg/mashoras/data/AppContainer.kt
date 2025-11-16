package com.uvg.mashoras.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.local.UserPreferencesDataSource
import com.uvg.mashoras.data.local.userPrefsDataStore
import com.uvg.mashoras.data.remote.FirebaseActivitiesDataSource
import com.uvg.mashoras.data.remote.FirebaseAuthDataSource
import com.uvg.mashoras.data.remote.FirebaseUserDataSource
import com.uvg.mashoras.data.repository.ActivitiesRepositoryImpl
import com.uvg.mashoras.data.repository.AuthRepositoryImpl
import com.uvg.mashoras.data.repository.UserRepositoryImpl
import com.uvg.mashoras.domain.repository.ActivitiesRepository
import com.uvg.mashoras.domain.repository.AuthRepository
import com.uvg.mashoras.domain.repository.UserRepository

class AppContainer(context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val userPrefs = UserPreferencesDataSource(context.userPrefsDataStore)

    val authRepository: AuthRepository =
        AuthRepositoryImpl(FirebaseAuthDataSource(auth, firestore))

    private val activitiesRemoteDataSource = FirebaseActivitiesDataSource(firestore)

    val activitiesRepository: ActivitiesRepository =
        ActivitiesRepositoryImpl(activitiesRemoteDataSource)
    
    // 👇 NUEVO: UserRepository para datos de usuario en tiempo real
    private val userRemoteDataSource = FirebaseUserDataSource(firestore)
    
    val userRepository: UserRepository =
        UserRepositoryImpl(userRemoteDataSource)
}