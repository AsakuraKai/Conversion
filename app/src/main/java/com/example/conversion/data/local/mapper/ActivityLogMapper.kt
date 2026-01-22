package com.example.conversion.data.local.mapper

import com.example.conversion.data.local.entity.ActivityLogEntity
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Mapper for converting between ActivityLog domain model and ActivityLogEntity.
 * 
 * Handles conversion of timestamps between LocalDateTime and epoch milliseconds,
 * and ActivityStatus between enum and string representation.
 */
object ActivityLogMapper {
    
    /**
     * Converts ActivityLogEntity to domain ActivityLog.
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
     * Converts domain ActivityLog to ActivityLogEntity.
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
}
