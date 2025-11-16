package com.uvg.mashoras.data.repository

import com.uvg.mashoras.data.models.User
import com.uvg.mashoras.data.remote.FirebaseUserDataSource
import com.uvg.mashoras.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val remote: FirebaseUserDataSource
) : UserRepository {
    
    override fun observeUser(uid: String): Flow<User?> = 
        remote.observeUser(uid)
    
    override suspend fun updateMeta(uid: String, newMeta: Int) {
        remote.updateMeta(uid, newMeta)
    }
    
    override suspend fun getUser(uid: String): User? = 
        remote.getUser(uid)
}