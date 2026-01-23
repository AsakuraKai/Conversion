package com.example.conversion.util

import android.net.Uri
import com.example.conversion.domain.model.*
import java.time.Instant

/**
 * Factory for creating test data instances.
 * Provides consistent test data across all test suites.
 */
object TestDataFactory {

    // File Items
    fun createFileItem(
        id: String = "test_file_1",
        uri: Uri = Uri.parse("content://media/external/images/media/1"),
        name: String = "test_image.jpg",
        path: String = "/storage/emulated/0/Pictures/test_image.jpg",
        size: Long = 1024000,
        mimeType: String = "image/jpeg",
        dateModified: Long = System.currentTimeMillis(),
        thumbnailUri: Uri? = null
    ): FileItem = FileItem(
        id = id,
        uri = uri,
        name = name,
        path = path,
        size = size,
        mimeType = mimeType,
        dateModified = dateModified,
        thumbnailUri = thumbnailUri
    )

    fun createFileItems(count: Int): List<FileItem> = List(count) { index ->
        createFileItem(
            id = "test_file_$index",
            uri = Uri.parse("content://media/external/images/media/$index"),
            name = "test_image_$index.jpg",
            path = "/storage/emulated/0/Pictures/test_image_$index.jpg"
        )
    }

    // Rename Configuration
    fun createRenameConfig(
        prefix: String = "IMG",
        startNumber: Int = 1,
        digitCount: Int = 3,
        preserveExtension: Boolean = true,
        sortStrategy: SortStrategy = SortStrategy.NAME
    ): RenameConfig = RenameConfig(
        prefix = prefix,
        startNumber = startNumber,
        digitCount = digitCount,
        preserveExtension = preserveExtension,
        sortStrategy = sortStrategy
    )

    // Rename Results
    fun createRenameResult(
        originalFile: FileItem = createFileItem(),
        newName: String = "IMG_001.jpg",
        success: Boolean = true,
        error: String? = null
    ): RenameResult = RenameResult(
        originalFile = originalFile,
        newName = newName,
        success = success,
        error = error
    )

    // Folder Information
    fun createFolderInfo(
        path: String = "/storage/emulated/0/Pictures",
        name: String = "Pictures",
        fileCount: Int = 10,
        subfolderCount: Int = 2,
        uri: Uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3APictures")
    ): FolderInfo = FolderInfo(
        path = path,
        name = name,
        fileCount = fileCount,
        subfolderCount = subfolderCount,
        uri = uri
    )

    // Preview Items
    fun createPreviewItem(
        original: FileItem = createFileItem(),
        preview: String = "IMG_001.jpg",
        hasConflict: Boolean = false,
        conflictReason: String? = null
    ): PreviewItem = PreviewItem(
        original = original,
        preview = preview,
        hasConflict = hasConflict,
        conflictReason = conflictReason
    )

    // Image Metadata
    fun createImageMetadata(
        dateTaken: String? = "2025-12-08 10:30:00",
        location: String? = "37.7749,-122.4194",
        cameraModel: String? = "Pixel 8 Pro",
        width: Int = 4032,
        height: Int = 3024,
        orientation: Int = 0
    ): ImageMetadata = ImageMetadata(
        dateTaken = dateTaken,
        location = location,
        cameraModel = cameraModel,
        width = width,
        height = height,
        orientation = orientation
    )

    // Reusable Templates
    fun createRenameTemplate(
        id: String = "template_1",
        name: String = "Basic Template",
        pattern: String = "{prefix}{number}",
        config: RenameConfig = createRenameConfig(),
        isFavorite: Boolean = false,
        createdAt: Instant = Instant.now(),
        lastUsedAt: Instant? = null
    ): RenameTemplate = RenameTemplate(
        id = id,
        name = name,
        pattern = pattern,
        config = config,
        isFavorite = isFavorite,
        createdAt = createdAt,
        lastUsedAt = lastUsedAt
    )

    // Image Labels (ML)
    fun createImageLabel(
        text: String = "Mountain",
        confidence: Float = 0.95f,
        category: String = "Nature"
    ): ImageLabel = ImageLabel(
        text = text,
        confidence = confidence,
        category = category
    )

    // Rename Operations (History)
    fun createRenameOperation(
        id: String = "operation_1",
        originalUri: Uri = Uri.parse("content://media/external/images/media/1"),
        newUri: Uri = Uri.parse("content://media/external/images/media/1"),
        originalName: String = "old_name.jpg",
        newName: String = "new_name.jpg",
        timestamp: Instant = Instant.now()
    ): RenameOperation = RenameOperation(
        id = id,
        originalUri = originalUri,
        newUri = newUri,
        originalName = originalName,
        newName = newName,
        timestamp = timestamp
    )

    // File Tags
    fun createFileTag(
        id: String = "tag_1",
        name: String = "Important",
        color: Int = 0xFF0000FF.toInt(),
        createdAt: Instant = Instant.now()
    ): FileTag = FileTag(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt
    )

    // Cloud Sync Configuration
    fun createSyncConfig(
        provider: CloudProvider = CloudProvider.GOOGLE_DRIVE,
        autoSync: Boolean = true,
        syncInterval: Long = 3600000L,
        enabled: Boolean = true
    ): SyncConfig = SyncConfig(
        provider = provider,
        autoSync = autoSync,
        syncInterval = syncInterval,
        enabled = enabled
    )

    // Folder Monitor
    fun createFolderMonitor(
        id: String = "monitor_1",
        folderUri: Uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3APictures"),
        folderName: String = "Pictures",
        pattern: String = "*.jpg",
        renameConfig: RenameConfig = createRenameConfig(),
        isActive: Boolean = false,
        createdAt: Instant = Instant.now()
    ): FolderMonitor = FolderMonitor(
        id = id,
        folderUri = folderUri,
        folderName = folderName,
        pattern = pattern,
        renameConfig = renameConfig,
        isActive = isActive,
        createdAt = createdAt
    )

    // File Events
    fun createFileEvent(
        filePath: String = "/storage/emulated/0/Pictures/test.jpg",
        eventType: FileEventType = FileEventType.CREATED,
        timestamp: Long = System.currentTimeMillis()
    ): FileEvent = FileEvent(
        filePath = filePath,
        eventType = eventType,
        timestamp = timestamp
    )

    // Monitoring Status
    fun createMonitoringStatus(
        activeMonitors: Int = 1,
        totalFilesProcessed: Int = 10,
        successfulRenames: Int = 9,
        failedRenames: Int = 1,
        lastEventTime: Instant? = Instant.now()
    ): MonitoringStatus = MonitoringStatus(
        activeMonitors = activeMonitors,
        totalFilesProcessed = totalFilesProcessed,
        successfulRenames = successfulRenames,
        failedRenames = failedRenames,
        lastEventTime = lastEventTime
    )

    // Extracted Text (OCR)
    fun createExtractedText(
        text: String = "Sample text",
        confidence: Float = 0.95f,
        boundingBox: android.graphics.Rect = android.graphics.Rect(0, 0, 100, 50),
        language: String = "en"
    ): ExtractedText = ExtractedText(
        text = text,
        confidence = confidence,
        boundingBox = boundingBox,
        language = language
    )

    // User Preferences (Sync)
    fun createUserPreferences(
        templates: List<RenameTemplate> = emptyList(),
        tags: List<FileTag> = emptyList(),
        settings: Map<String, String> = emptyMap(),
        lastSyncTimestamp: Long = System.currentTimeMillis()
    ): UserPreferences = UserPreferences(
        templates = templates,
        tags = tags,
        settings = settings,
        lastSyncTimestamp = lastSyncTimestamp
    )

    // Activity Log
    fun createActivityLog(
        id: String = "log_1",
        action: String = "FILE_RENAMED",
        details: String = "Renamed test.jpg to IMG_001.jpg",
        timestamp: Instant = Instant.now(),
        status: String = "SUCCESS"
    ): ActivityLog = ActivityLog(
        id = id,
        action = action,
        details = details,
        timestamp = timestamp,
        status = status
    )
}
