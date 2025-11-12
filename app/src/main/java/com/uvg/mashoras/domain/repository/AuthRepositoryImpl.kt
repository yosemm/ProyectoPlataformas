package com.uvg.mashoras.data.repository

import com.uvg.mashoras.data.remote.FirebaseAuthDataSource
import com.uvg.mashoras.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remote: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): String {
        return remote.login(email, password)
    }

    override suspend fun register(email: String, password: String, role: String) {
        remote.register(email, password, role)
    }

    override fun logout() {
        remote.logout()
    }
}
