package com.uvg.mashoras.domain.repository

import com.uvg.mashoras.data.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(uid: String): Flow<User?>
    suspend fun updateMeta(uid: String, newMeta: Int)
    suspend fun getUser(uid: String): User?
}