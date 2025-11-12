package com.uvg.mashoras.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uvg.mashoras.domain.model.ActivityItem

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val hours: Int
) {
    fun toDomain() = ActivityItem(id, title, description, hours)

    companion object {
        fun fromDomain(item: ActivityItem) =
            ActivityEntity(item.id, item.title, item.description, item.hours)
    }
}

