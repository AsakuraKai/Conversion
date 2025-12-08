package com.example.conversion.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.domain.model.ExportFormat
import com.example.conversion.domain.model.LogFilter
import com.example.conversion.domain.repository.ActivityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of ActivityRepository using in-memory storage.
 *
 * **STRATEGIC IMPLEMENTATION - Development Phase**
 *
 * This implementation uses in-memory storage (MutableList) to provide
 * complete activity logging functionality without requiring Room database setup.
 * Perfect for parallel UI/backend development.
 *
 * **Fully Functional Features:**
 * - Complete activity log tracking
 * - Filtering by date, status, and action
 * - CSV and JSON export functionality
 * - Thread-safe operations with Mutex
 *
 * **Production Upgrade Path:**
 * Replace in-memory storage with Room database (ActivityDao) for:
 * - Persistent storage across app restarts
 * - Efficient querying for large log histories
 * - Database transactions for consistency
 *
 * @property context Application context for file operations
 * @property ioDispatcher For background operations
 */
@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ActivityRepository {

    // In-memory storage
    private val activityLogs = mutableListOf<ActivityLog>()
    private val mutex = Mutex()
    private var nextId = 1L

    override suspend fun logActivity(log: ActivityLog): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val logWithId = if (log.id == 0L) {
                        log.copy(id = nextId++)
                    } else {
                        log
                    }
                    activityLogs.add(logWithId)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getActivityLogs(filter: LogFilter): Result<List<ActivityLog>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    var filtered = activityLogs.toList()

                    // Filter by date range
                    if (filter.startDate != null) {
                        filtered = filtered.filter { it.timestamp >= filter.startDate }
                    }
                    if (filter.endDate != null) {
                        filtered = filtered.filter { it.timestamp <= filter.endDate }
                    }

                    // Filter by status
                    if (filter.status != null) {
                        filtered = filtered.filter { it.status == filter.status }
                    }

                    // Filter by action
                    if (filter.action != null) {
                        filtered = filtered.filter { it.action == filter.action }
                    }

                    // Sort by timestamp descending (most recent first)
                    filtered = filtered.sortedByDescending { it.timestamp }

                    // Apply limit
                    filtered = filtered.take(filter.limit)

                    Result.Success(filtered)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun exportLogs(format: ExportFormat): Result<Uri> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val logs = activityLogs.sortedByDescending { it.timestamp }
                    val file = when (format) {
                        ExportFormat.CSV -> exportToCsv(logs)
                        ExportFormat.JSON -> exportToJson(logs)
                    }
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    Result.Success(uri)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Exports activity logs to a CSV file.
     *
     * @param logs List of activity logs to export
     * @return File containing the CSV data
     */
    private fun exportToCsv(logs: List<ActivityLog>): File {
        val exportsDir = File(context.getExternalFilesDir(null), "exports")
        if (!exportsDir.exists()) {
            exportsDir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val file = File(exportsDir, "activity_logs_$timestamp.csv")

        file.bufferedWriter().use { writer ->
            // Write header
            writer.write("ID,Action,Details,Timestamp,Status\n")

            // Write data
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            logs.forEach { log ->
                val timestampStr = log.timestamp.format(formatter)
                val escapedDetails = log.details.replace("\"", "\"\"")
                writer.write("${log.id},${log.action},\"$escapedDetails\",$timestampStr,${log.status}\n")
            }
        }

        return file
    }

    /**
     * Exports activity logs to a JSON file.
     *
     * @param logs List of activity logs to export
     * @return File containing the JSON data
     */
    private fun exportToJson(logs: List<ActivityLog>): File {
        val exportsDir = File(context.getExternalFilesDir(null), "exports")
        if (!exportsDir.exists()) {
            exportsDir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val file = File(exportsDir, "activity_logs_$timestamp.json")

        val jsonArray = JSONArray()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        logs.forEach { log ->
            val jsonObject = JSONObject().apply {
                put("id", log.id)
                put("action", log.action)
                put("details", log.details)
                put("timestamp", log.timestamp.format(formatter))
                put("timestampMillis", log.timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                put("status", log.status.name)
            }
            jsonArray.put(jsonObject)
        }

        val rootObject = JSONObject().apply {
            put("exportTimestamp", System.currentTimeMillis())
            put("totalLogs", logs.size)
            put("logs", jsonArray)
        }

        file.writeText(rootObject.toString(2)) // Pretty print with indent of 2

        return file
    }
}
