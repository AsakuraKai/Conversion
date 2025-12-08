package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Room database entity for storing activity logs.
 *
 * This entity represents a single activity log entry in the database,
 * storing information about operations performed in the application.
 *
 * @property id Unique identifier for this log entry
 * @property action The action that was performed
 * @property details Additional details about the action
 * @property timestamp When the action occurred (epoch milliseconds)
 * @property status The outcome of the action
 */
@Entity(tableName = "activity_logs")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val details: String,
    val timestamp: Long,
    val status: String
) {
    /**
     * Converts this entity to a domain model.
     *
     * @return ActivityLog domain model
     */
    fun toDomain(): ActivityLog {
        return ActivityLog(
            id = id,
            action = action,
            details = details,
            timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
            ),
            status = ActivityStatus.valueOf(status)
        )
    }

    companion object {
        /**
         * Creates an entity from a domain model.
         *
         * @param activityLog The domain model to convert
         * @return ActivityEntity for database storage
         */
        fun fromDomain(activityLog: ActivityLog): ActivityEntity {
            return ActivityEntity(
                id = activityLog.id,
                action = activityLog.action,
                details = activityLog.details,
                timestamp = activityLog.timestamp
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                status = activityLog.status.name
            )
        }
    }
}
