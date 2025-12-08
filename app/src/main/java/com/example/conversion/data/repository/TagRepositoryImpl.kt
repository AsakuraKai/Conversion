package com.example.conversion.data.repository

import android.net.Uri
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.model.TaggedFile
import com.example.conversion.domain.repository.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MOCK IMPLEMENTATION: In-memory tag repository.
 * 
 * This is a strategic implementation using in-memory storage to enable rapid development
 * and UI implementation without blocking on Room database setup.
 * 
 * **Production Upgrade Path:**
 * - Replace with Room database (TagEntity, FileTagCrossRef, TagDao)
 * - Add persistent storage with SQLite
 * - Implement proper database migrations
 * - Add complex queries with JOIN operations
 * - Implement efficient many-to-many relationships
 * 
 * **Current Functionality:**
 * ✅ Complete CRUD operations for tags
 * ✅ Tag-file associations (many-to-many)
 * ✅ Flow-based observation
 * ✅ Thread-safe with mutex
 * ✅ Full error handling
 * ✅ Search and filtering
 * 
 * **Limitations:**
 * ⚠️ Data lost on app restart (no persistence)
 * ⚠️ No database migrations
 * ⚠️ File queries return mock data
 * ⚠️ Limited query optimization
 * 
 * See: MOCK_IMPLEMENTATIONS.md - CHUNK 16
 */
@Singleton
class TagRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TagRepository {

    // In-memory storage
    private val tags = mutableMapOf<String, FileTag>()
    private val fileTags = mutableMapOf<String, MutableSet<String>>() // fileUri -> Set<tagId>
    private val tagsFlow = MutableStateFlow<List<FileTag>>(emptyList())
    private val mutex = Mutex()

    override suspend fun createTag(tag: FileTag): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    if (tags.containsKey(tag.id)) {
                        return@withContext Result.Error(
                            IllegalArgumentException("Tag with ID ${tag.id} already exists")
                        )
                    }
                    tags[tag.id] = tag
                    emitTags()
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun updateTag(tag: FileTag): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    if (!tags.containsKey(tag.id)) {
                        return@withContext Result.Error(
                            IllegalArgumentException("Tag with ID ${tag.id} does not exist")
                        )
                    }
                    tags[tag.id] = tag
                    emitTags()
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTags(): Result<List<FileTag>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    Result.Success(tags.values.sortedByDescending { it.createdAt })
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTagById(id: String): Result<FileTag?> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    Result.Success(tags[id])
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteTag(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    tags.remove(id)
                    // Remove all file associations with this tag
                    fileTags.values.forEach { tagSet ->
                        tagSet.remove(id)
                    }
                    emitTags()
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTags(): Flow<List<FileTag>> {
        return tagsFlow
    }

    override suspend fun tagFile(fileUri: Uri, tagId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    if (!tags.containsKey(tagId)) {
                        return@withContext Result.Error(
                            IllegalArgumentException("Tag with ID $tagId does not exist")
                        )
                    }
                    val uriString = fileUri.toString()
                    val tagSet = fileTags.getOrPut(uriString) { mutableSetOf() }
                    tagSet.add(tagId)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun untagFile(fileUri: Uri, tagId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val uriString = fileUri.toString()
                    fileTags[uriString]?.remove(tagId)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getFilesByTag(tagId: String): Result<List<FileItem>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    // Mock implementation: Return empty list
                    // In production, this would query MediaStore for files with this tag
                    val fileUris = fileTags.entries
                        .filter { it.value.contains(tagId) }
                        .map { Uri.parse(it.key) }
                    
                    // Return mock FileItems for demonstration
                    val mockFiles = fileUris.mapIndexed { index, uri ->
                        FileItem(
                            id = index.toLong(),
                            uri = uri,
                            name = uri.lastPathSegment ?: "Unknown",
                            path = uri.path ?: "",
                            size = 0L,
                            mimeType = "image/jpeg",
                            dateModified = System.currentTimeMillis(),
                            thumbnailUri = uri
                        )
                    }
                    
                    Result.Success(mockFiles)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTagsForFile(fileUri: Uri): Result<List<FileTag>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val uriString = fileUri.toString()
                    val tagIds = fileTags[uriString] ?: emptySet()
                    val tags = tagIds.mapNotNull { tagId -> this@TagRepositoryImpl.tags[tagId] }
                    Result.Success(tags)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTaggedFile(fileUri: Uri): Result<TaggedFile?> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val uriString = fileUri.toString()
                    val tagIds = fileTags[uriString] ?: return@withContext Result.Success(null)
                    val fileTags = tagIds.mapNotNull { tags[it] }
                    
                    if (fileTags.isEmpty()) {
                        Result.Success(null)
                    } else {
                        Result.Success(TaggedFile(fileUri, fileTags))
                    }
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTagsForFile(fileUri: Uri): Flow<List<FileTag>> {
        val uriString = fileUri.toString()
        return tagsFlow.map { allTags ->
            val tagIds = fileTags[uriString] ?: emptySet()
            allTags.filter { it.id in tagIds }
        }
    }

    override suspend fun searchFilesByTagName(query: String): Result<List<TaggedFile>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    // Find tags matching the query
                    val matchingTags = tags.values.filter { 
                        it.name.contains(query, ignoreCase = true) 
                    }
                    
                    // Find all files with these tags
                    val matchingTagIds = matchingTags.map { it.id }.toSet()
                    val taggedFiles = fileTags.entries
                        .filter { (_, tagIds) -> tagIds.any { it in matchingTagIds } }
                        .map { (uriString, tagIds) ->
                            val uri = Uri.parse(uriString)
                            val fileTags = tagIds.mapNotNull { tags[it] }
                            TaggedFile(uri, fileTags)
                        }
                    
                    Result.Success(taggedFiles)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Emits the current list of tags to the flow.
     */
    private fun emitTags() {
        tagsFlow.value = tags.values.sortedByDescending { it.createdAt }
    }
}
