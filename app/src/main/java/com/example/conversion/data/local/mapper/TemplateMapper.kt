package com.example.conversion.data.local.mapper

import com.example.conversion.data.local.entity.TemplateEntity
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Mapper for converting between RenameTemplate domain model and TemplateEntity.
 * 
 * Handles JSON serialization of RenameConfig for database storage.
 */
object TemplateMapper {
    
    /**
     * Serializable version of RenameConfig for JSON storage.
     */
    @kotlinx.serialization.Serializable
    private data class RenameConfigData(
        val prefix: String,
        val startNumber: Int,
        val digitCount: Int,
        val preserveExtension: Boolean,
        val sortStrategy: String
    )
    
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Converts TemplateEntity to domain RenameTemplate.
     */
    fun TemplateEntity.toDomain(): RenameTemplate {
        val configData = json.decodeFromString<RenameConfigData>(configJson)
        val config = RenameConfig(
            prefix = configData.prefix,
            startNumber = configData.startNumber,
            digitCount = configData.digitCount,
            preserveExtension = configData.preserveExtension,
            sortStrategy = SortStrategy.valueOf(configData.sortStrategy)
        )
        
        return RenameTemplate(
            id = id,
            name = name,
            pattern = pattern,
            config = config,
            isFavorite = isFavorite,
            createdAt = createdAt,
            lastUsedAt = lastUsedAt
        )
    }
    
    /**
     * Converts domain RenameTemplate to TemplateEntity.
     */
    fun RenameTemplate.toEntity(): TemplateEntity {
        val configData = RenameConfigData(
            prefix = config.prefix,
            startNumber = config.startNumber,
            digitCount = config.digitCount,
            preserveExtension = config.preserveExtension,
            sortStrategy = config.sortStrategy.name
        )
        
        val configJson = json.encodeToString(configData)
        
        return TemplateEntity(
            id = id,
            name = name,
            pattern = pattern,
            configJson = configJson,
            isFavorite = isFavorite,
            createdAt = createdAt,
            lastUsedAt = lastUsedAt
        )
    }
}
