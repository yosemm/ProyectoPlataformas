package com.uvg.mashoras.ui.register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.utils.EmailValidator
import kotlinx.coroutines.tasks.await

class RegisterRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun register(
        email: String, 
        password: String, 
        nombre: String,
        apellido: String,
        career: String
    ): Result<Unit> {
        // Validación de correo institucional
        if (!EmailValidator.isUvgEmail(email)) {
            return Result.failure(
                IllegalArgumentException("El correo debe ser institucional (@uvg.edu.gt)")
            )
        }

        // Validación de campos requeridos
        if (nombre.isBlank() || apellido.isBlank()) {
            return Result.failure(
                IllegalArgumentException("El nombre y apellido son requeridos")
            )
        }

        return try {
            // 1. Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: throw IllegalStateException("No se pudo obtener el UID del usuario")

            // 2. Determinar el rol basándose en el correo
            val rol = EmailValidator.determineUserRole(email)

            // 3. Guardar datos adicionales en Firestore
            val data = hashMapOf(
                "uid" to uid,
                "nombre" to nombre,
                "apellido" to apellido,
                "correo" to email,
                "meta" to 0,
                "avance" to 0,
                "rol" to rol.name, // "ESTUDIANTE" o "MAESTRO"
                "carrera" to career,
                "actividadesRealizadas" to emptyList<String>() // Inicialmente vacío
            )

            firestore.collection("users")
                .document(uid)
                .set(data)
                .await()

            // 4. Si TODO salió bien
            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}