package com.uvg.mashoras.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.uvg.mashoras.data.models.Activity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseActivitiesDataSource(
    private val firestore: FirebaseFirestore
) {

    private val activitiesCollection = firestore.collection("activities")

    // === LECTURA EN TIEMPO REAL ===
    fun observeActivities(): Flow<List<Activity>> = callbackFlow {
        val registration = activitiesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val activities = snapshot?.documents
                ?.mapNotNull { it.toActivity() }
                ?: emptyList()

            trySend(activities).isSuccess
        }

        awaitClose { registration.remove() }
    }

    // Si no necesitas nada especial, puede ser un no-op
    suspend fun refreshActivities() {
        // no-op, el flujo ya se actualiza solo
    }

    // === CREAR ACTIVIDAD ===
    suspend fun createActivity(activity: Activity, creatorId: String) {
        val docRef = activitiesCollection.document()

        val data = hashMapOf(
            "titulo" to activity.titulo,
            "descripcion" to activity.descripcion,
            "fecha" to (activity.fecha ?: Timestamp.now()),
            "cupos" to activity.cupos,
            "carrera" to activity.carrera,
            "finalizado" to activity.finalizado,
            "horasARealizar" to activity.horasARealizar,
            "estudiantesInscritos" to activity.estudiantesInscritos,
            "creadoPor" to creatorId
        )

        docRef.set(data)
    }

    // === INSCRIBIR / DESINSCRIBIR ===
    suspend fun enrollStudent(activityId: String, userId: String) {
        val docRef = activitiesCollection.document(activityId)
        docRef.update("estudiantesInscritos", FieldValue.arrayUnion(userId))
    }

    suspend fun unenrollStudent(activityId: String, userId: String) {
        val docRef = activitiesCollection.document(activityId)
        docRef.update("estudiantesInscritos", FieldValue.arrayRemove(userId))
    }

    // === FINALIZAR Y ELIMINAR ===
    suspend fun markActivityAsCompleted(activityId: String) {
        val docRef = activitiesCollection.document(activityId)
        docRef.update("finalizado", true)
    }

    suspend fun deleteActivity(activityId: String) {
        val docRef = activitiesCollection.document(activityId)
        docRef.delete()
    }

    // === MAPEO DE DOCUMENTO A MODELO ===
    private fun DocumentSnapshot.toActivity(): Activity? {
        val titulo = getString("titulo") ?: return null
        val descripcion = getString("descripcion") ?: ""
        val fecha = getTimestamp("fecha")
        val cupos = getLong("cupos")?.toInt() ?: 0
        val carrera = getString("carrera") ?: ""
        val finalizado = getBoolean("finalizado") ?: false
        val horas = getLong("horasARealizar")?.toInt() ?: 0
        val inscritos = get("estudiantesInscritos") as? List<String> ?: emptyList()
        val creadoPor = getString("creadoPor") ?: ""

        return Activity(
            id = id,
            titulo = titulo,
            descripcion = descripcion,
            fecha = fecha,
            cupos = cupos,
            carrera = carrera,
            finalizado = finalizado,
            horasARealizar = horas,
            estudiantesInscritos = inscritos,
            creadoPor = creadoPor
        )
    }
}
