package com.example.conversion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.conversion.data.local.entity.FileTagCrossRef
import com.example.conversion.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for file tags.
 *
 * Provides methods to perform CRUD operations on tags and file-tag associations.
 */
@Dao
interface TagDao {

    /**
     * Observes all tags ordered by creation date.
     *
     * @return Flow emitting the list of all tags whenever it changes
     */
    @Query("SELECT * FROM file_tags ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TagEntity>>

    /**
     * Gets all tags ordered by creation date.
     *
     * @return List of all tags
     */
    @Query("SELECT * FROM file_tags ORDER BY createdAt DESC")
    suspend fun getAll(): List<TagEntity>

    /**
     * Gets a specific tag by ID.
     *
     * @param id The tag ID
     * @return The tag if found, null otherwise
     */
    @Query("SELECT * FROM file_tags WHERE id = :id")
    suspend fun getById(id: String): TagEntity?

    /**
     * Searches tags by name (case-insensitive).
     *
     * @param query The search query
     * @return List of matching tags
     */
    @Query("SELECT * FROM file_tags WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchByName(query: String): List<TagEntity>

    /**
     * Inserts a new tag or replaces if it already exists.
     *
     * @param tag The tag to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity)

    /**
     * Updates an existing tag.
     *
     * @param tag The tag to update
     */
    @Update
    suspend fun update(tag: TagEntity)

    /**
     * Deletes a specific tag by ID.
     * Foreign key constraint will also delete associated file-tag relationships.
     *
     * @param id The ID of the tag to delete
     */
    @Query("DELETE FROM file_tags WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Deletes all tags from the database.
     */
    @Query("DELETE FROM file_tags")
    suspend fun deleteAll()

    // File-Tag Association Operations

    /**
     * Associates a file with a tag.
     *
     * @param crossRef The file-tag cross reference
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFileTagCrossRef(crossRef: FileTagCrossRef)

    /**
     * Removes the association between a file and a tag.
     *
     * @param fileUriString String representation of the file URI
     * @param tagId The tag ID
     */
    @Query("DELETE FROM file_tag_cross_ref WHERE fileUriString = :fileUriString AND tagId = :tagId")
    suspend fun deleteFileTagCrossRef(fileUriString: String, tagId: String)

    /**
     * Gets all tags for a specific file.
     *
     * @param fileUriString String representation of the file URI
     * @return List of tags associated with the file
     */
    @Transaction
    @Query("""
        SELECT file_tags.* FROM file_tags
        INNER JOIN file_tag_cross_ref ON file_tags.id = file_tag_cross_ref.tagId
        WHERE file_tag_cross_ref.fileUriString = :fileUriString
        ORDER BY file_tags.name ASC
    """)
    suspend fun getTagsForFile(fileUriString: String): List<TagEntity>

    /**
     * Observes all tags for a specific file.
     *
     * @param fileUriString String representation of the file URI
     * @return Flow emitting the list of tags whenever it changes
     */
    @Transaction
    @Query("""
        SELECT file_tags.* FROM file_tags
        INNER JOIN file_tag_cross_ref ON file_tags.id = file_tag_cross_ref.tagId
        WHERE file_tag_cross_ref.fileUriString = :fileUriString
        ORDER BY file_tags.name ASC
    """)
    fun observeTagsForFile(fileUriString: String): Flow<List<TagEntity>>

    /**
     * Gets all file URIs that have a specific tag.
     *
     * @param tagId The tag ID
     * @return List of file URI strings
     */
    @Query("SELECT fileUriString FROM file_tag_cross_ref WHERE tagId = :tagId")
    suspend fun getFileUrisForTag(tagId: String): List<String>

    /**
     * Gets all file URIs that have any of the specified tags.
     *
     * @param tagIds List of tag IDs to search for
     * @return List of file URI strings
     */
    @Query("""
        SELECT DISTINCT fileUriString FROM file_tag_cross_ref 
        WHERE tagId IN (:tagIds)
    """)
    suspend fun getFileUrisForTags(tagIds: List<String>): List<String>

    /**
     * Removes all tag associations for a specific file.
     *
     * @param fileUriString String representation of the file URI
     */
    @Query("DELETE FROM file_tag_cross_ref WHERE fileUriString = :fileUriString")
    suspend fun deleteAllTagsForFile(fileUriString: String)

    /**
     * Checks if a file has a specific tag.
     *
     * @param fileUriString String representation of the file URI
     * @param tagId The tag ID
     * @return True if the file has the tag, false otherwise
     */
    @Query("""
        SELECT COUNT(*) > 0 FROM file_tag_cross_ref 
        WHERE fileUriString = :fileUriString AND tagId = :tagId
    """)
    suspend fun hasTag(fileUriString: String, tagId: String): Boolean

    /**
     * Gets the count of files for each tag.
     *
     * @return Map of tag IDs to file counts
     */
    @Query("""
        SELECT tagId, COUNT(*) as count FROM file_tag_cross_ref 
        GROUP BY tagId
    """)
    suspend fun getFileCountsPerTag(): Map<@MapColumn(columnName = "tagId") String, @MapColumn(columnName = "count") Int>
}
