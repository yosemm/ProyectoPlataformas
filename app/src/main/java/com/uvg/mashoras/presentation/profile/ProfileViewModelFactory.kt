package com.uvg.mashoras.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.uvg.mashoras.domain.repository.UserRepository

class ProfileViewModelFactory(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(auth, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}