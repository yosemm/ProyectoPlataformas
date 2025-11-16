package com.uvg.mashoras.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.data.models.User
import com.uvg.mashoras.data.models.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseUserDataSource(
    private val firestore: FirebaseFirestore
) {
    
    /**
     * Observa los cambios del usuario en tiempo real
     */
    fun observeUser(uid: String): Flow<User?> = callbackFlow {
        val registration = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot == null || !snapshot.exists()) {
                    trySend(null).isSuccess
                    return@addSnapshotListener
                }
                
                val user = try {
                    User(
                        uid = snapshot.getString("uid") ?: uid,
                        nombre = snapshot.getString("nombre") ?: "",
                        apellido = snapshot.getString("apellido") ?: "",
                        correo = snapshot.getString("correo") ?: "",
                        meta = (snapshot.getLong("meta") ?: 0L).toInt(),
                        avance = (snapshot.getLong("avance") ?: 0L).toInt(),
                        rol = when (snapshot.getString("rol")) {
                            UserRole.MAESTRO.name -> UserRole.MAESTRO
                            else -> UserRole.ESTUDIANTE
                        },
                        carrera = snapshot.getString("carrera") ?: "",
                        actividadesRealizadas = (snapshot.get("actividadesRealizadas") as? List<String>).orEmpty()
                    )
                } catch (e: Exception) {
                    null
                }
                
                trySend(user).isSuccess
            }
        
        awaitClose { registration.remove() }
    }
    
    /**
     * Actualiza la meta del usuario
     */
    suspend fun updateMeta(uid: String, newMeta: Int) {
        firestore.collection("users")
            .document(uid)
            .update("meta", newMeta)
            .await()
    }
    
    /**
     * Obtiene el usuario una sola vez (para casos donde no se necesita tiempo real)
     */
    suspend fun getUser(uid: String): User? {
        return try {
            val doc = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            
            if (!doc.exists()) return null
            
            User(
                uid = doc.getString("uid") ?: uid,
                nombre = doc.getString("nombre") ?: "",
                apellido = doc.getString("apellido") ?: "",
                correo = doc.getString("correo") ?: "",
                meta = (doc.getLong("meta") ?: 0L).toInt(),
                avance = (doc.getLong("avance") ?: 0L).toInt(),
                rol = when (doc.getString("rol")) {
                    UserRole.MAESTRO.name -> UserRole.MAESTRO
                    else -> UserRole.ESTUDIANTE
                },
                carrera = doc.getString("carrera") ?: "",
                actividadesRealizadas = (doc.get("actividadesRealizadas") as? List<String>).orEmpty()
            )
        } catch (e: Exception) {
            null
        }
    }
}