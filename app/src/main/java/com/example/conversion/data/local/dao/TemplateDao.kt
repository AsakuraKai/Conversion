package com.example.conversion.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.conversion.data.local.entity.TemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Template entities.
 * Provides methods to interact with the templates table.
 */
@Dao
interface TemplateDao {
    /**
     * Observe all templates ordered by creation date (newest first).
     */
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TemplateEntity>>
    
    /**
     * Observe favorite templates ordered by last used date (most recently used first).
     */
    @Query("SELECT * FROM templates WHERE isFavorite = 1 ORDER BY lastUsedAt DESC")
    fun observeFavorites(): Flow<List<TemplateEntity>>
    
    /**
     * Get all templates (one-time query).
     */
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    suspend fun getAll(): List<TemplateEntity>
    
    /**
     * Get a specific template by ID.
     */
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getById(templateId: String): TemplateEntity?
    
    /**
     * Insert a new template. Replace if conflict occurs.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TemplateEntity)
    
    /**
     * Update an existing template.
     */
    @Update
    suspend fun update(template: TemplateEntity)
    
    /**
     * Delete a template.
     */
    @Delete
    suspend fun delete(template: TemplateEntity)
    
    /**
     * Delete a template by ID.
     */
    @Query("DELETE FROM templates WHERE id = :templateId")
    suspend fun deleteById(templateId: String)
    
    /**
     * Delete all templates.
     */
    @Query("DELETE FROM templates")
    suspend fun deleteAll()
    
    /**
     * Count total number of templates.
     */
    @Query("SELECT COUNT(*) FROM templates")
    suspend fun count(): Int
    
    /**
     * Get favorite templates (one-time query).
     */
    @Query("SELECT * FROM templates WHERE isFavorite = 1 ORDER BY lastUsedAt DESC")
    suspend fun getFavorites(): List<TemplateEntity>
}
