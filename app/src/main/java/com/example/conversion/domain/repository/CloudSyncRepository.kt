package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.model.SyncProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for cloud file storage and backup
 *
 * Provides methods for:
 * - Uploading renamed files to cloud storage
 * - Batch file synchronization
 * - Progress tracking for uploads
 * - Backup management
 *
 * Cloud Storage Structure:
 * /users/{uid}/files/{filename}
 *
 * Supports background sync with WorkManager for automatic backup
 * when device is on WiFi and charging.
 */
interface CloudSyncRepository {
    /**
     * Upload a single file to cloud storage
     *
     * @param fileUri Local file URI to upload
     * @param remotePath Destination path in cloud storage
     * @return Result with download URL on success
     */
    suspend fun uploadFile(
        fileUri: Uri,
        remotePath: String
    ): Result<String>

    /**
     * Sync multiple files to cloud storage
     *
     * Uploads files in parallel with progress tracking.
     * Emits progress updates via Flow.
     *
     * @param files List of file URIs to sync
     * @return Flow of SyncProgress updates
     */
    fun syncFiles(files: List<Uri>): Flow<SyncProgress>

    /**
     * Delete a file from cloud storage
     *
     * @param remotePath Path to file in cloud storage
     * @return Result indicating success or failure
     */
    suspend fun deleteFile(remotePath: String): Result<Unit>

    /**
     * Get list of backed up files for current user
     *
     * @return Result with list of file paths in cloud storage
     */
    suspend fun getBackedUpFiles(): Result<List<String>>

    /**
     * Check if automatic backup is enabled and configured
     *
     * @return True if backup is ready to run
     */
    suspend fun isBackupEnabled(): Boolean
}
