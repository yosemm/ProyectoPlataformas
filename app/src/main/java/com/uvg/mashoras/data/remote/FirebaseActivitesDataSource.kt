package com.uvg.mashoras.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.mashoras.domain.model.ActivityItem
import kotlinx.coroutines.tasks.await

/**
 * Fuente remota en Firestore para actividades.
 * Lee y escribe en la colección "activities".
 */
class FirebaseActivitiesDataSource(
    private val firestore: FirebaseFirestore
) {
    private val col = firestore.collection("activities")

    /** Devuelve la lista de actividades con su ID de documento. */
    suspend fun fetchActivities(): List<ActivityItem> {
        val snap = col.get().await()
        return snap.documents.mapNotNull { d ->
            val title = d.getString("title") ?: return@mapNotNull null
            val description = d.getString("description") ?: ""
            val hours = (d.getLong("hours") ?: 0L).toInt()
            ActivityItem(
                id = d.id,
                title = title,
                description = description,
                hours = hours
            )
        }
    }

    /** Crea una actividad; retorna el ID creado. */
    suspend fun addActivity(
        title: String,
        description: String,
        hours: Int,
        ownerEmail: String
    ): String {
        val data = mapOf(
            "title" to title,
            "description" to description,
            "hours" to hours,
            "ownerEmail" to ownerEmail,
            "timestamp" to System.currentTimeMillis()
        )
        val ref = col.add(data).await()
        return ref.id
    }

    /** Elimina una actividad por ID. */
    suspend fun deleteActivity(id: String): Boolean {
        return try {
            col.document(id).delete().await()
            true
        } catch (_: Exception) {
            false
        }
    }
}
