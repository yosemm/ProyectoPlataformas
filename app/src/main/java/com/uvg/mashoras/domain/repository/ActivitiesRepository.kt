package com.uvg.mashoras.domain.repository

import com.google.firebase.Timestamp
import com.uvg.mashoras.data.models.Activity
import kotlinx.coroutines.flow.Flow

interface ActivitiesRepository {
    fun observeActivities(): Flow<List<Activity>>
    suspend fun refreshActivities()
    suspend fun createActivity(activity: Activity, creatorId: String)
    suspend fun updateActivity(
        activityId: String,
        titulo: String,
        descripcion: String,
        fecha: Timestamp,
        cupos: Int,
        carrera: String,
        horasARealizar: Int
    ) // 👈 NUEVO
    suspend fun enrollStudent(activityId: String, userId: String)
    suspend fun unenrollStudent(activityId: String, userId: String)
    suspend fun markActivityAsCompleted(activityId: String)
    suspend fun deleteActivity(activityId: String)
}