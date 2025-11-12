package com.uvg.mashoras.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): String
    suspend fun register(email: String, password: String, role: String)
    fun logout()
}
