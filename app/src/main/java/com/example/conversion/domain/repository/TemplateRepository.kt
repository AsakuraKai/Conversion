package com.example.conversion.domain.repository

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Reusable Templates.
 * Handles CRUD operations and observation of template data.
 */
interface TemplateRepository {
    /**
     * Saves a new template or updates an existing one.
     * @param template The template to save
     * @return Result indicating success or failure
     */
    suspend fun saveTemplate(template: RenameTemplate): Result<Unit>

    /**
     * Retrieves all saved templates.
     * @return Result containing the list of templates or an error
     */
    suspend fun getTemplates(): Result<List<RenameTemplate>>

    /**
     * Retrieves a specific template by its ID.
     * @param id The template ID
     * @return Result containing the template or an error
     */
    suspend fun getTemplateById(id: String): Result<RenameTemplate?>

    /**
     * Deletes a template by its ID.
     * @param id The ID of the template to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTemplate(id: String): Result<Unit>

    /**
     * Observes changes to the template list.
     * @return Flow emitting the list of templates whenever it changes
     */
    fun observeTemplates(): Flow<List<RenameTemplate>>

    /**
     * Retrieves only favorite templates.
     * @return Result containing the list of favorite templates or an error
     */
    suspend fun getFavoriteTemplates(): Result<List<RenameTemplate>>

    /**
     * Observes changes to favorite templates.
     * @return Flow emitting the list of favorite templates whenever it changes
     */
    fun observeFavoriteTemplates(): Flow<List<RenameTemplate>>

    /**
     * Updates the last used timestamp for a template.
     * @param id The template ID
     * @return Result indicating success or failure
     */
    suspend fun markTemplateAsUsed(id: String): Result<Unit>

    /**
     * Toggles the favorite status of a template.
     * @param id The template ID
     * @return Result indicating success or failure
     */
    suspend fun toggleFavorite(id: String): Result<Unit>

    /**
     * Deletes all templates.
     * @return Result indicating success or failure
     */
    suspend fun clearAllTemplates(): Result<Unit>
}
