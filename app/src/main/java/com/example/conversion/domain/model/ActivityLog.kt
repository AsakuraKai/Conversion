package com.example.conversion.domain.model

import java.time.LocalDateTime

/**
 * Represents an activity log entry in the system.
 *
 * @property id Unique identifier for the log entry
 * @property action The action that was performed (e.g., "FILE_RENAMED", "BATCH_PROCESSED")
 * @property details Additional details about the action (e.g., file names, error messages)
 * @property timestamp When the action occurred
 * @property status The outcome of the action
 */
data class ActivityLog(
    val id: Long = 0,
    val action: String,
    val details: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: ActivityStatus
)

/**
 * Status of an activity log entry.
 */
enum class ActivityStatus {
    SUCCESS,
    FAILED,
    IN_PROGRESS,
    CANCELLED
}
