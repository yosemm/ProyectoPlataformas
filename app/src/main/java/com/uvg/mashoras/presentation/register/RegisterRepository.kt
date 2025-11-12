package com.uvg.mashoras.ui.register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun register(email: String, password: String, career: String): Result<Unit> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: error("Usuario inválido")

        val userDoc = mapOf(
            "email" to email,
            "role" to "student",   // fijo temporal
            "career" to career
        )
        firestore.collection("users").document(uid).set(userDoc).await()
    }
}
