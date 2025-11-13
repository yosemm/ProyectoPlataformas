package com.uvg.mashoras.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.uvg.mashoras.domain.repository.ActivitiesRepository

class ActivitiesViewModelFactory(
    private val repository: ActivitiesRepository,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivitiesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivitiesViewModel(repository, auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}