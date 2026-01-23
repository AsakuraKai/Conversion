package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ExportFormat
import com.example.conversion.domain.model.LogFilter

/**
 * Repository interface for managing activity logs.
 *
 * This repository provides functionality to track, store, retrieve, and export
 * activity logs for all operations performed in the application.
 */
interface ActivityRepository {

    /**
     * Logs an activity in the system.
     *
     * @param log The activity log to save
     * @return Success with Unit, or Error with exception
     */
    suspend fun logActivity(log: ActivityLog): Result<Unit>

    /**
     * Retrieves activity logs based on filter criteria.
     *
     * @param filter Filter criteria for querying logs
     * @return Success with list of activity logs, or Error with exception
     */
    suspend fun getActivityLogs(filter: LogFilter): Result<List<ActivityLog>>

    /**
     * Exports activity logs to a file in the specified format.
     *
     * @param format The export format (CSV or JSON)
     * @return Success with URI of the exported file, or Error with exception
     */
    suspend fun exportLogs(format: ExportFormat): Result<Uri>
}
