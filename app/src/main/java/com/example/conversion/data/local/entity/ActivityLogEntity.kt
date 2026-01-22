package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing activity logs.
 *
 * Mapping functions are in ActivityLogMapper.
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
