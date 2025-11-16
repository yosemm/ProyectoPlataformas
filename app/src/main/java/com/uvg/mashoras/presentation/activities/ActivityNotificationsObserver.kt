package com.uvg.mashoras.presentation.activities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.uvg.mashoras.utils.NotificationHelper

class ActivityNotificationsObserver(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val notificationHelper: NotificationHelper
) {

    private var newActivitiesListener: ListenerRegistration? = null
    private var enrolledActivitiesListener: ListenerRegistration? = null

    private val prefs: SharedPreferences =
        context.getSharedPreferences("activity_notifications_prefs", Context.MODE_PRIVATE)

    fun startObserving(userCareer: String, isTeacher: Boolean) {
        val currentUser = auth.currentUser ?: run {
            Log.d("ActivityNotif", "No hay usuario logueado, no se observan actividades")
            return
        }

        if (isTeacher) {
            Log.d("ActivityNotif", "Usuario es maestro, no se activan notificaciones de estudiante")
            return
        }

        val userId = currentUser.uid
        val userCareerNorm = userCareer.lowercase()

        Log.d(
            "ActivityNotif",
            "Iniciando observers para userId=$userId carrera=$userCareerNorm"
        )

        // 1) NUEVAS ACTIVIDADES (para su carrera o para todas)
        newActivitiesListener = firestore.collection("activities")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ActivityNotif", "Error en listener de nuevas actividades", error)
                    return@addSnapshotListener
                }
                if (snapshots == null) return@addSnapshotListener

                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val doc = change.document

                        // "career" o "carrera" (por si acaso)
                        val docCareerRaw =
                            doc.getString("career")
                                ?: doc.getString("carrera")
                                ?: "todas"
                        val docCareer = docCareerRaw.lowercase()

                        // Aceptamos:
                        // - mismas carreras
                        // - "todas", "todos", "all"
                        val esParaTodos = docCareer in listOf("todas", "todos", "all")
                        val esParaMiCarrera = docCareer == userCareerNorm

                        if (!esParaTodos && !esParaMiCarrera) {
                            continue
                        }

                        val title =
                            doc.getString("title")
                                ?: doc.getString("titulo")
                                ?: "Nueva actividad"

                        Log.d(
                            "ActivityNotif",
                            "Nueva actividad detectada: id=${doc.id}, titulo=$title, carrera=$docCareerRaw"
                        )

                        if (!hasNotifiedNewActivity(doc.id)) {
                            notificationHelper.showNotification(
                                doc.id.hashCode(),
                                "Nueva actividad",
                                title
                            )
                            markNotifiedNewActivity(doc.id)
                        }
                    }
                }
            }

        // 2) ACTIVIDADES DONDE ESTA INSCRITO, Y QUE SE FINALIZAN
        enrolledActivitiesListener = firestore.collection("activities")
            .whereArrayContains("estudiantesInscritos", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ActivityNotif", "Error en listener de actividades inscritas", error)
                    return@addSnapshotListener
                }
                if (snapshots == null) return@addSnapshotListener

                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.MODIFIED) {
                        val doc = change.document

                        val estado = doc.getString("estado")
                        val finalizadoBool = doc.getBoolean("finalizado") ?: false
                        val isFinished = (estado?.lowercase() == "finalizada") || finalizadoBool

                        if (!isFinished) continue

                        val title =
                            doc.getString("title")
                                ?: doc.getString("titulo")
                                ?: "Actividad finalizada"

                        Log.d(
                            "ActivityNotif",
                            "Actividad finalizada detectada: id=${doc.id}, titulo=$title"
                        )

                        if (!hasNotifiedFinished(doc.id)) {
                            notificationHelper.showNotification(
                                (doc.id + "_finished").hashCode(),
                                "Actividad finalizada",
                                title
                            )
                            markNotifiedFinished(doc.id)
                        }
                    }
                }
            }
    }

    fun stopObserving() {
        Log.d("ActivityNotif", "Deteniendo observers")
        newActivitiesListener?.remove()
        enrolledActivitiesListener?.remove()
    }

    // ---------- evitar spam ----------

    private fun hasNotifiedNewActivity(activityId: String): Boolean {
        return prefs.getBoolean("new_$activityId", false)
    }

    private fun markNotifiedNewActivity(activityId: String) {
        prefs.edit().putBoolean("new_$activityId", true).apply()
    }

    private fun hasNotifiedFinished(activityId: String): Boolean {
        return prefs.getBoolean("finished_$activityId", false)
    }

    private fun markNotifiedFinished(activityId: String) {
        prefs.edit().putBoolean("finished_$activityId", true).apply()
    }
}
