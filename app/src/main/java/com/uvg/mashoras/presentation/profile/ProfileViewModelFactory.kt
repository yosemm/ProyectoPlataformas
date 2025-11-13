package com.uvg.mashoras.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModelFactory(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(auth, firestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
