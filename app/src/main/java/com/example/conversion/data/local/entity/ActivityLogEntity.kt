package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Room entity for storing activity logs.
 *
 * @property id Auto-generated unique identifier
 * @property action The action that was performed
 * @property details Additional details about the action
 * @property timestampMillis Timestamp when action occurred (epoch milliseconds)
 * @property status The outcome of the action (stored as string)
 */
@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val details: String,
    val timestampMillis: Long,
    val status: String
)

/**
 * Extension function to convert ActivityLogEntity to domain ActivityLog.
 */
fun ActivityLogEntity.toDomain(): ActivityLog {
    val localDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestampMillis),
        ZoneId.systemDefault()
    )
    
    return ActivityLog(
        id = id,
        action = action,
        details = details,
        timestamp = localDateTime,
        status = ActivityStatus.valueOf(status)
    )
}

/**
 * Extension function to convert domain ActivityLog to ActivityLogEntity.
 */
fun ActivityLog.toEntity(): ActivityLogEntity {
    val timestampMillis = timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    
    return ActivityLogEntity(
        id = if (id == 0L) 0 else id,
        action = action,
        details = details,
        timestampMillis = timestampMillis,
        status = status.name
    )
}
