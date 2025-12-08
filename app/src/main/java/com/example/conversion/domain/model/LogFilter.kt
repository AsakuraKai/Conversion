package com.example.conversion.domain.model

import java.time.LocalDateTime

/**
 * Filter criteria for querying activity logs.
 *
 * @property startDate Start of the date range (inclusive)
 * @property endDate End of the date range (inclusive)
 * @property status Filter by specific status, null for all statuses
 * @property action Filter by specific action, null for all actions
 * @property limit Maximum number of logs to return
 */
data class LogFilter(
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val status: ActivityStatus? = null,
    val action: String? = null,
    val limit: Int = 100
)
