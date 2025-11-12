package com.uvg.mashoras.data.repository

import com.uvg.mashoras.data.local.ActivityDao
import com.uvg.mashoras.data.local.ActivityEntity
import com.uvg.mashoras.data.remote.FirebaseActivitiesDataSource
import com.uvg.mashoras.domain.model.ActivityItem
import com.uvg.mashoras.domain.repository.ActivitiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivitiesRepositoryImpl(
    private val remote: FirebaseActivitiesDataSource,
    private val dao: ActivityDao
) : ActivitiesRepository {

    override fun observeActivities(): Flow<List<ActivityItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun refreshActivities() {
        val cloud = remote.fetchActivities()
        dao.clear()
        dao.insertAll(cloud.map { ActivityEntity.fromDomain(it) })
    }

    override suspend fun addActivity(
        title: String,
        description: String,
        hours: Int,
        ownerEmail: String
    ) {
        remote.addActivity(title, description, hours, ownerEmail)
        refreshActivities()
    }

    override suspend fun deleteActivity(id: String) {
        remote.deleteActivity(id)
        refreshActivities()
    }
}
