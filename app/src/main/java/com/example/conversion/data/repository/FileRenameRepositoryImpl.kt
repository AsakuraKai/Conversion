package com.example.conversion.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.FileRenameRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Implementation of FileRenameRepository using MediaStore API.
 * Handles file renaming with Android 10+ scoped storage support.
 *
 * @property contentResolver The ContentResolver for MediaStore operations
 * @property ioDispatcher The dispatcher for IO operations
 */
class FileRenameRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher
) : FileRenameRepository {

    override suspend fun renameFile(uri: Uri, newName: String): Result<Uri> = withContext(ioDispatcher) {
        try {
            // Prepare content values with new filename
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
            }

            // Update the file in MediaStore
            val updatedRows = contentResolver.update(uri, contentValues, null, null)

            if (updatedRows > 0) {
                // Trigger media scanner to update the system
                triggerMediaScan(uri)
                Result.Success(uri)
            } else {
                Result.Error(Exception("Failed to rename file: No rows updated"))
            }
        } catch (e: SecurityException) {
            Result.Error(Exception("Permission denied: Cannot rename file", e))
        } catch (e: IllegalArgumentException) {
            Result.Error(Exception("Invalid file URI or name", e))
        } catch (e: Exception) {
            Result.Error(Exception("Failed to rename file: ${e.message}", e))
        }
    }

    override suspend fun checkNameConflict(uri: Uri, newName: String): Boolean = withContext(ioDispatcher) {
        try {
            // Get the directory path of the current file
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    val filePath = cursor.getString(dataIndex)
                    val directory = filePath.substringBeforeLast('/')
                    
                    // Check if a file with the new name exists in the same directory
                    val newFilePath = "$directory/$newName"
                    
                    // Query MediaStore for files with matching path
                    val conflictProjection = arrayOf(MediaStore.MediaColumns._ID)
                    val selection = "${MediaStore.MediaColumns.DATA} = ?"
                    val selectionArgs = arrayOf(newFilePath)
                    
                    contentResolver.query(
                        MediaStore.Files.getContentUri("external"),
                        conflictProjection,
                        selection,
                        selectionArgs,
                        null
                    )?.use { conflictCursor ->
                        return@withContext conflictCursor.count > 0
                    }
                }
            }
            false
        } catch (e: Exception) {
            // If we can't check, assume no conflict to allow the rename attempt
            false
        }
    }

    override suspend fun batchRenameFiles(renamePairs: List<Pair<Uri, String>>): Map<Uri, Result<Uri>> = 
        withContext(ioDispatcher) {
            val results = mutableMapOf<Uri, Result<Uri>>()
            
            renamePairs.forEach { (uri, newName) ->
                val result = renameFile(uri, newName)
                results[uri] = result
            }
            
            results
        }

    /**
     * Triggers MediaScanner to update the system's media database.
     * This ensures the renamed file appears correctly in gallery apps and file managers.
     *
     * Note: This is a mock implementation for Android 10+ as MediaScannerConnection
     * requires a Context which we'll get from Sokchea's implementation.
     */
    private suspend fun triggerMediaScan(uri: Uri) {
        // Mock implementation - In real app, this would use MediaScannerConnection
        // MediaScannerConnection requires Context, which Sokchea will provide
        
        // For now, we'll just log or skip this step
        // In production, this would be:
        // suspendCancellableCoroutine { continuation ->
        //     MediaScannerConnection.scanFile(
        //         context,
        //         arrayOf(filePath),
        //         null
        //     ) { path, scanUri ->
        //         continuation.resume(Unit)
        //     }
        // }
    }
}
