package com.uvg.mashoras.ui.register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun register(email: String, password: String, career: String): Result<Unit> {
        // Validación de correo institucional
        if (!email.endsWith("@uvg.edu.gt")) {
            return Result.failure(
                IllegalArgumentException("El correo debe ser institucional (@uvg.edu.gt)")
            )
        }

        return try {
            // 1. Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: throw IllegalStateException("No se pudo obtener el UID del usuario")

            // 2. Guardar datos adicionales en Firestore
            val data = mapOf(
                "email" to email,
                "career" to career
            )

            firestore.collection("users")
                .document(uid)
                .set(data)
                .await()

            // 3. Si TODO salió bien
            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
