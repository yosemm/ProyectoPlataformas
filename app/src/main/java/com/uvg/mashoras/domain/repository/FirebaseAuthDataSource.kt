package com.uvg.mashoras.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): String {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Usuario inválido")
            val doc = firestore.collection("users").document(uid).get().await()
            return doc.getString("role") ?: "student"
        } catch (e: Exception) {
            throw Exception(e.message ?: "Error al iniciar sesión")
        }
    }

    suspend fun register(email: String, password: String, career: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Error al registrar usuario")

        val data = mapOf(
            "email" to email,
            "role" to "student",
            "career" to career
        )
        firestore.collection("users").document(uid).set(data).await()
        return "student"
    }


    fun logout() {
        auth.signOut()
    }
}
