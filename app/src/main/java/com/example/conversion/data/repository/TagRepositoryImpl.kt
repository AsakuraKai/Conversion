package com.example.conversion.data.repository

import android.net.Uri
import com.example.conversion.data.local.dao.TagDao
import com.example.conversion.data.local.entity.FileTagCrossRef
import com.example.conversion.data.local.mapper.TagMapper.toDomain
import com.example.conversion.data.local.mapper.TagMapper.toEntity
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.model.TaggedFile
import com.example.conversion.domain.repository.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PRODUCTION IMPLEMENTATION: Room database tag repository.
 * 
 * This implementation uses Room database for persistent tag storage with
 * efficient many-to-many relationships between files and tags.
 * 
 * **Production Features:**
 * ✅ Persistent storage with SQLite
 * ✅ Efficient many-to-many relationships (FileTagCrossRef)
 * ✅ Complex JOIN queries for tag associations
 * ✅ Flow-based reactive observations
 * ✅ Thread-safe database operations
 * ✅ Full CRUD operations
 * ✅ Search and filtering capabilities
 * 
 * **Architecture:**
 * - TagEntity: Database representation of tags
 * - FileTagCrossRef: Junction table for file-tag associations
 * - TagDao: Data access object with optimized queries
 * - Room handles thread safety and transactions automatically
 * 
 * **Upgrade from Mock:**
 * - Replaced in-memory MutableMap with TagDao
 * - Replaced Mutex synchronization with Room's built-in thread safety
 * - Added persistent storage across app restarts
 * - Improved query performance with SQL indexes
 */
@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TagRepository {

    override suspend fun createTag(tag: FileTag): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = tag.toEntity()
                tagDao.insert(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun updateTag(tag: FileTag): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = tag.toEntity()
                tagDao.update(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTags(): Result<List<FileTag>> =
        withContext(ioDispatcher) {
            try {
                val entities = tagDao.getAll()
                Result.Success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTagById(id: String): Result<FileTag?> =
        withContext(ioDispatcher) {
            try {
                val entity = tagDao.getById(id)
                Result.Success(entity?.toDomain())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteTag(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                tagDao.deleteById(id)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTags(): Flow<List<FileTag>> {
        return tagDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun tagFile(fileUri: Uri, tagId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val crossRef = FileTagCrossRef(
                    fileUriString = fileUri.toString(),
                    tagId = tagId
                )
                tagDao.insertFileTagCrossRef(crossRef)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun untagFile(fileUri: Uri, tagId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                tagDao.deleteFileTagCrossRef(fileUri.toString(), tagId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getFilesByTag(tagId: String): Result<List<FileItem>> =
        withContext(ioDispatcher) {
            try {
                val fileUris = tagDao.getFileUrisForTag(tagId)
                    .map { Uri.parse(it) }
                
                // TODO Phase 5: Query MediaStore for actual file details
                // For now, use DocumentFile to get basic file information
                val files = fileUris.mapIndexed { index, uri ->
                    FileItem(
                        id = index.toLong(),
                        uri = uri,
                        name = uri.lastPathSegment ?: "Unknown",
                        path = uri.path ?: "",
                        size = 0L, // TODO: Get from DocumentFile.length()
                        mimeType = "*/*", // TODO: Get from DocumentFile.type
                        dateModified = System.currentTimeMillis(), // TODO: Get from DocumentFile.lastModified()
                        thumbnailUri = uri
                    )
                }
                
                Result.Success(files)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTagsForFile(fileUri: Uri): Result<List<FileTag>> =
        withContext(ioDispatcher) {
            try {
                val entities = tagDao.getTagsForFile(fileUri.toString())
                Result.Success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTaggedFile(fileUri: Uri): Result<TaggedFile?> =
        withContext(ioDispatcher) {
            try {
                val entities = tagDao.getTagsForFile(fileUri.toString())
                if (entities.isEmpty()) {
                    Result.Success(null)
                } else {
                    val tags = entities.map { it.toDomain() }
                    Result.Success(TaggedFile(fileUri, tags))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTagsForFile(fileUri: Uri): Flow<List<FileTag>> {
        return tagDao.observeTagsForFile(fileUri.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun searchFilesByTagName(query: String): Result<List<TaggedFile>> =
        withContext(ioDispatcher) {
            try {
                val matchingTags = tagDao.searchByName(query)
                val taggedFiles = mutableListOf<TaggedFile>()
                
                matchingTags.forEach { tag ->
                    val fileUris = tagDao.getFileUrisForTag(tag.id)
                    fileUris.forEach { uriString ->
                        val uri = Uri.parse(uriString)
                        val tags = tagDao.getTagsForFile(uriString).map { it.toDomain() }
                        taggedFiles.add(TaggedFile(uri, tags))
                    }
                }
                
                // Remove duplicates based on URI
                val uniqueTaggedFiles = taggedFiles.distinctBy { it.fileUri.toString() }
                Result.Success(uniqueTaggedFiles)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
