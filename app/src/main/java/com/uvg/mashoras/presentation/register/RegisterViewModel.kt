package com.uvg.mashoras.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterViewModel(
    private val repository: RegisterRepository
) : ViewModel() {
    suspend fun register(email: String, password: String, career: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            repository.register(email, password, career)
        }
    }
}
