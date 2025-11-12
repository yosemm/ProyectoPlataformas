package com.uvg.mashoras.domain.repository

import com.uvg.mashoras.domain.model.ActivityItem
import kotlinx.coroutines.flow.Flow

interface ActivitiesRepository {
    /** Flujo de actividades desde Room (cache local). */
    fun observeActivities(): Flow<List<ActivityItem>>

    /** Sincroniza cache local con Firestore. */
    suspend fun refreshActivities()

    /** Alta remota + refresco local. */
    suspend fun addActivity(title: String, description: String, hours: Int, ownerEmail: String)

    /** Baja remota + refresco local. */
    suspend fun deleteActivity(id: String)
}
