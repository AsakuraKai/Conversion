package com.example.conversion.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.conversion.data.local.dao.ActivityLogDao
import com.example.conversion.data.local.mapper.ActivityLogMapper.toDomain
import com.example.conversion.data.local.mapper.ActivityLogMapper.toEntity
import com.example.conversion.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.domain.model.ExportFormat
import com.example.conversion.domain.model.LogFilter
import com.example.conversion.domain.repository.ActivityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PRODUCTION IMPLEMENTATION: Room database activity repository.
 *
 * This implementation uses Room database for persistent activity log storage
 * with efficient time-based querying and filtering capabilities.
 *
 * **Production Features:**
 * ✅ Persistent storage across app restarts
 * ✅ Efficient time-based queries with indexed timestamps
 * ✅ Status and action filtering
 * ✅ CSV and JSON export functionality
 * ✅ Thread-safe database operations
 * ✅ Automatic ID generation
 * 
 * **Architecture:**
 * - ActivityLogEntity: Database representation with LocalDateTime conversion
 * - ActivityLogDao: Data access object with optimized time-based queries
 * - Room handles thread safety and transactions automatically
 * 
 * **Upgrade from Mock:**
 * - Replaced in-memory MutableList with ActivityLogDao
 * - Replaced Mutex synchronization with Room's built-in thread safety
 * - Added persistent storage with indexed queries
 * - Improved query performance for large log histories
 *
 * @property context Application context for file operations
 * @property activityLogDao Room DAO for activity log operations
 * @property ioDispatcher For background operations
 */
@Singleton
class ActivityRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityLogDao: ActivityLogDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ActivityRepository {

    override suspend fun logActivity(log: ActivityLog): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = log.toEntity()
                activityLogDao.insert(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getActivityLogs(filter: LogFilter): Result<List<ActivityLog>> =
        withContext(ioDispatcher) {
            try {
                val entities = when {
                    // Use optimized queries when possible
                    filter.startDate != null && filter.endDate != null && filter.status != null -> {
                        val startMillis = filter.startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val endMillis = filter.endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        activityLogDao.getInTimeRange(startMillis, endMillis)
                            .filter { it.status == filter.status.name }
                    }
                    filter.startDate != null && filter.endDate != null -> {
                        val startMillis = filter.startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val endMillis = filter.endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        activityLogDao.getInTimeRange(startMillis, endMillis)
                    }
                    filter.status != null && filter.action != null -> {
                        activityLogDao.getByStatus(filter.status.name)
                            .filter { it.action == filter.action }
                    }
                    filter.status != null -> {
                        activityLogDao.getByStatus(filter.status.name)
                    }
                    filter.action != null -> {
                        activityLogDao.getByAction(filter.action)
                    }
                    else -> {
                        activityLogDao.getAll()
                    }
                }
                
                // Apply limit
                val limitedEntities = entities.take(filter.limit)
                Result.Success(limitedEntities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun exportLogs(format: ExportFormat): Result<Uri> =
        withContext(ioDispatcher) {
            try {
                val entities = activityLogDao.getAll()
                val logs = entities.map { it.toDomain() }
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
