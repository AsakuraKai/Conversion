package com.example.conversion.util

import android.net.Uri
import com.example.conversion.domain.model.*
import com.example.conversion.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Fake repository implementations for testing.
 * These provide controllable behavior for unit tests.
 */

class FakeMediaRepository : MediaRepository {
    var mediaFiles = mutableListOf<FileItem>()
    var shouldFail = false
    var failureMessage = "Test failure"

    override suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>> {
        return if (shouldFail) {
            Result.failure(Exception(failureMessage))
        } else {
            Result.success(mediaFiles.filter { file ->
                when {
                    filter.includeImages && file.mimeType.startsWith("image/") -> true
                    filter.includeVideos && file.mimeType.startsWith("video/") -> true
                    filter.includeAudio && file.mimeType.startsWith("audio/") -> true
                    else -> false
                }
            })
        }
    }

    override suspend fun getFilesByFolder(folderPath: String): Result<List<FileItem>> {
        return if (shouldFail) {
            Result.failure(Exception(failureMessage))
        } else {
            Result.success(mediaFiles.filter { it.path.startsWith(folderPath) })
        }
    }

    override fun observeMediaFiles(): Flow<List<FileItem>> = flowOf(mediaFiles)

    fun reset() {
        mediaFiles.clear()
        shouldFail = false
        failureMessage = "Test failure"
    }
}

class FakeFolderRepository : FolderRepository {
    var folders = mutableListOf<FolderInfo>()
    var shouldFail = false

    override suspend fun getFolders(parentPath: String): Result<List<FolderInfo>> {
        return if (shouldFail) {
            Result.failure(Exception("Test failure"))
        } else {
            Result.success(folders.filter { it.path.startsWith(parentPath) })
        }
    }

    override suspend fun createFolder(parentPath: String, name: String): Result<FolderInfo> {
        return if (shouldFail) {
            Result.failure(Exception("Test failure"))
        } else {
            val newFolder = TestDataFactory.createFolderInfo(
                path = "$parentPath/$name",
                name = name
            )
            folders.add(newFolder)
            Result.success(newFolder)
        }
    }

    fun reset() {
        folders.clear()
        shouldFail = false
    }
}

class FakeFileRenameRepository : FileRenameRepository {
    var renamedFiles = mutableListOf<Pair<Uri, String>>()
    var shouldFail = false

    override suspend fun renameFile(uri: Uri, newName: String): Result<Uri> {
        return if (shouldFail) {
            Result.failure(Exception("Rename failed"))
        } else {
            renamedFiles.add(uri to newName)
            Result.success(uri)
        }
    }

    fun reset() {
        renamedFiles.clear()
        shouldFail = false
    }
}

class FakeTemplateRepository : TemplateRepository {
    private val templates = mutableMapOf<String, RenameTemplate>()
    private val _templatesFlow = MutableStateFlow<List<RenameTemplate>>(emptyList())
    var shouldFail = false

    override suspend fun saveTemplate(template: RenameTemplate): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Save failed"))
        } else {
            templates[template.id] = template
            _templatesFlow.value = templates.values.toList()
            Result.success(Unit)
        }
    }

    override suspend fun getTemplate(id: String): Result<RenameTemplate> {
        return if (shouldFail) {
            Result.failure(Exception("Get failed"))
        } else {
            templates[id]?.let { Result.success(it) }
                ?: Result.failure(Exception("Template not found"))
        }
    }

    override suspend fun getAllTemplates(): Result<List<RenameTemplate>> {
        return if (shouldFail) {
            Result.failure(Exception("Get all failed"))
        } else {
            Result.success(templates.values.toList())
        }
    }

    override suspend fun deleteTemplate(id: String): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Delete failed"))
        } else {
            templates.remove(id)
            _templatesFlow.value = templates.values.toList()
            Result.success(Unit)
        }
    }

    override suspend fun updateTemplate(template: RenameTemplate): Result<Unit> {
        return saveTemplate(template)
    }

    override fun observeTemplates(): Flow<List<RenameTemplate>> = _templatesFlow

    override fun observeFavorites(): Flow<List<RenameTemplate>> =
        _templatesFlow.map { it.filter { template -> template.isFavorite } }

    fun reset() {
        templates.clear()
        _templatesFlow.value = emptyList()
        shouldFail = false
    }
}

class FakeMLRepository : MLRepository {
    var labels = listOf<ImageLabel>()
    var shouldFail = false

    override suspend fun analyzeImage(uri: Uri): Result<List<ImageLabel>> {
        return if (shouldFail) {
            Result.failure(Exception("Analysis failed"))
        } else {
            Result.success(labels)
        }
    }

    override suspend fun generateFilenameSuggestions(uri: Uri): Result<List<String>> {
        return if (shouldFail) {
            Result.failure(Exception("Suggestion failed"))
        } else {
            Result.success(labels.map { it.text.lowercase().replace(" ", "_") })
        }
    }

    fun reset() {
        labels = emptyList()
        shouldFail = false
    }
}

class FakeHistoryRepository : HistoryRepository {
    private val operations = mutableListOf<RenameOperation>()
    var shouldFail = false

    override suspend fun saveOperation(operation: RenameOperation): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Save failed"))
        } else {
            operations.add(operation)
            Result.success(Unit)
        }
    }

    override suspend fun getHistory(limit: Int): Result<List<RenameOperation>> {
        return if (shouldFail) {
            Result.failure(Exception("Get history failed"))
        } else {
            Result.success(operations.takeLast(limit))
        }
    }

    override suspend fun clearHistory(): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Clear failed"))
        } else {
            operations.clear()
            Result.success(Unit)
        }
    }

    override fun observeHistory(): Flow<List<RenameOperation>> = flowOf(operations)

    fun reset() {
        operations.clear()
        shouldFail = false
    }
}

class FakeTagRepository : TagRepository {
    private val tags = mutableMapOf<String, FileTag>()
    private val fileTags = mutableMapOf<String, MutableSet<String>>() // fileUri to tagIds
    var shouldFail = false

    override suspend fun createTag(tag: FileTag): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Create failed"))
        } else {
            tags[tag.id] = tag
            Result.success(Unit)
        }
    }

    override suspend fun getTag(id: String): Result<FileTag> {
        return if (shouldFail) {
            Result.failure(Exception("Get failed"))
        } else {
            tags[id]?.let { Result.success(it) }
                ?: Result.failure(Exception("Tag not found"))
        }
    }

    override suspend fun getAllTags(): Result<List<FileTag>> {
        return if (shouldFail) {
            Result.failure(Exception("Get all failed"))
        } else {
            Result.success(tags.values.toList())
        }
    }

    override suspend fun deleteTag(id: String): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Delete failed"))
        } else {
            tags.remove(id)
            fileTags.values.forEach { it.remove(id) }
            Result.success(Unit)
        }
    }

    override suspend fun tagFile(fileUri: Uri, tagId: String): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Tag file failed"))
        } else {
            fileTags.getOrPut(fileUri.toString()) { mutableSetOf() }.add(tagId)
            Result.success(Unit)
        }
    }

    override suspend fun untagFile(fileUri: Uri, tagId: String): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Untag failed"))
        } else {
            fileTags[fileUri.toString()]?.remove(tagId)
            Result.success(Unit)
        }
    }

    override suspend fun getFileTags(fileUri: Uri): Result<List<FileTag>> {
        return if (shouldFail) {
            Result.failure(Exception("Get file tags failed"))
        } else {
            val tagIds = fileTags[fileUri.toString()] ?: emptySet()
            Result.success(tagIds.mapNotNull { tags[it] })
        }
    }

    override suspend fun searchFilesByTag(tagId: String): Result<List<String>> {
        return if (shouldFail) {
            Result.failure(Exception("Search failed"))
        } else {
            Result.success(
                fileTags.filterValues { tagId in it }.keys.toList()
            )
        }
    }

    fun reset() {
        tags.clear()
        fileTags.clear()
        shouldFail = false
    }
}

class FakeMetadataRepository : MetadataRepository {
    var metadata: ImageMetadata? = null
    var shouldFail = false

    override suspend fun extractMetadata(uri: Uri): Result<ImageMetadata> {
        return if (shouldFail) {
            Result.failure(Exception("Extract failed"))
        } else {
            metadata?.let { Result.success(it) }
                ?: Result.failure(Exception("No metadata"))
        }
    }

    fun reset() {
        metadata = null
        shouldFail = false
    }
}

class FakeFolderMonitorRepository : FolderMonitorRepository {
    private val monitors = mutableMapOf<String, FolderMonitor>()
    private val _statusFlow = MutableStateFlow(TestDataFactory.createMonitoringStatus())
    private val _eventsFlow = MutableStateFlow<FileEvent?>(null)
    var shouldFail = false

    override suspend fun startMonitoring(monitor: FolderMonitor): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Start failed"))
        } else {
            monitors[monitor.id] = monitor.copy(isActive = true)
            Result.success(Unit)
        }
    }

    override suspend fun stopMonitoring(monitorId: String): Result<Unit> {
        return if (shouldFail) {
            Result.failure(Exception("Stop failed"))
        } else {
            monitors[monitorId]?.let {
                monitors[monitorId] = it.copy(isActive = false)
            }
            Result.success(Unit)
        }
    }

    override suspend fun getMonitoringStatus(): Result<MonitoringStatus> {
        return if (shouldFail) {
            Result.failure(Exception("Get status failed"))
        } else {
            Result.success(_statusFlow.value)
        }
    }

    override fun observeStatus(): Flow<MonitoringStatus> = _statusFlow

    override fun observeFileEvents(): Flow<FileEvent> =
        _eventsFlow.map { it ?: TestDataFactory.createFileEvent() }

    fun emitEvent(event: FileEvent) {
        _eventsFlow.value = event
    }

    fun reset() {
        monitors.clear()
        _statusFlow.value = TestDataFactory.createMonitoringStatus()
        _eventsFlow.value = null
        shouldFail = false
    }
}
