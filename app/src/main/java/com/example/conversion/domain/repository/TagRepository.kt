package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.model.TaggedFile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing file tags.
 * Handles CRUD operations for tags and tag-file associations.
 */
interface TagRepository {
    /**
     * Creates a new tag.
     * @param tag The tag to create
     * @return Result indicating success or failure
     */
    suspend fun createTag(tag: FileTag): Result<Unit>

    /**
     * Updates an existing tag.
     * @param tag The tag to update
     * @return Result indicating success or failure
     */
    suspend fun updateTag(tag: FileTag): Result<Unit>

    /**
     * Retrieves all tags.
     * @return Result containing the list of tags or an error
     */
    suspend fun getTags(): Result<List<FileTag>>

    /**
     * Retrieves a specific tag by its ID.
     * @param id The tag ID
     * @return Result containing the tag or an error
     */
    suspend fun getTagById(id: String): Result<FileTag?>

    /**
     * Deletes a tag by its ID.
     * @param id The ID of the tag to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTag(id: String): Result<Unit>

    /**
     * Observes changes to the tag list.
     * @return Flow emitting the list of tags whenever it changes
     */
    fun observeTags(): Flow<List<FileTag>>

    /**
     * Applies a tag to a file.
     * @param fileUri URI of the file to tag
     * @param tagId ID of the tag to apply
     * @return Result indicating success or failure
     */
    suspend fun tagFile(fileUri: Uri, tagId: String): Result<Unit>

    /**
     * Removes a tag from a file.
     * @param fileUri URI of the file to untag
     * @param tagId ID of the tag to remove
     * @return Result indicating success or failure
     */
    suspend fun untagFile(fileUri: Uri, tagId: String): Result<Unit>

    /**
     * Retrieves all files that have a specific tag.
     * @param tagId The ID of the tag
     * @return Result containing the list of tagged files or an error
     */
    suspend fun getFilesByTag(tagId: String): Result<List<FileItem>>

    /**
     * Retrieves all tags applied to a specific file.
     * @param fileUri URI of the file
     * @return Result containing the list of tags or an error
     */
    suspend fun getTagsForFile(fileUri: Uri): Result<List<FileTag>>

    /**
     * Retrieves a tagged file with all its tags.
     * @param fileUri URI of the file
     * @return Result containing the tagged file or an error
     */
    suspend fun getTaggedFile(fileUri: Uri): Result<TaggedFile?>

    /**
     * Observes tags for a specific file.
     * @param fileUri URI of the file
     * @return Flow emitting the list of tags whenever they change
     */
    fun observeTagsForFile(fileUri: Uri): Flow<List<FileTag>>

    /**
     * Searches for files by tag names.
     * @param query Search query
     * @return Result containing the list of matching tagged files or an error
     */
    suspend fun searchFilesByTagName(query: String): Result<List<TaggedFile>>
}
