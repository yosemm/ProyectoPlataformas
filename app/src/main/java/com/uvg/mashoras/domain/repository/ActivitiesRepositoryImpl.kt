package com.uvg.mashoras.data.repository

import com.uvg.mashoras.data.models.Activity
import com.uvg.mashoras.data.remote.FirebaseActivitiesDataSource
import com.uvg.mashoras.domain.repository.ActivitiesRepository
import kotlinx.coroutines.flow.Flow

class ActivitiesRepositoryImpl(
    private val remote: FirebaseActivitiesDataSource
) : ActivitiesRepository {

    // Flujo en tiempo real desde Firestore
    override fun observeActivities(): Flow<List<Activity>> =
        remote.observeActivities()

    // Si tu data source ya escucha cambios, esto puede ser no-op
    override suspend fun refreshActivities() {
        remote.refreshActivities()
    }

    override suspend fun createActivity(activity: Activity, creatorId: String) {
        remote.createActivity(activity, creatorId)
    }

    override suspend fun enrollStudent(activityId: String, userId: String) {
        remote.enrollStudent(activityId, userId)
    }

    override suspend fun unenrollStudent(activityId: String, userId: String) {
        remote.unenrollStudent(activityId, userId)
    }

    override suspend fun markActivityAsCompleted(activityId: String) {
        remote.markActivityAsCompleted(activityId)
    }

    override suspend fun deleteActivity(activityId: String) {
        remote.deleteActivity(activityId)
    }
}
