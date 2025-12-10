package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room entity for storing rename templates.
 *
 * @property id Unique identifier for the template
 * @property name Display name for the template
 * @property pattern User-friendly description of the pattern
 * @property configJson Serialized JSON of RenameConfig
 * @property isFavorite Whether this template is marked as favorite
 * @property createdAt Timestamp when template was created (milliseconds since epoch)
 * @property lastUsedAt Timestamp when template was last used (null if never used)
 */
@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val pattern: String,
    val configJson: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val lastUsedAt: Long?
)

/**
 * Serializable version of RenameConfig for JSON storage.
 */
@kotlinx.serialization.Serializable
data class RenameConfigData(
    val prefix: String,
    val startNumber: Int,
    val digitCount: Int,
    val preserveExtension: Boolean,
    val sortStrategy: String
)

/**
 * Extension function to convert TemplateEntity to domain RenameTemplate.
 */
fun TemplateEntity.toDomain(json: Json = Json { ignoreUnknownKeys = true }): RenameTemplate {
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
 * Extension function to convert domain RenameTemplate to TemplateEntity.
 */
fun RenameTemplate.toEntity(json: Json = Json { ignoreUnknownKeys = true }): TemplateEntity {
    val configData = RenameConfigData(
        prefix = config.prefix,
        startNumber = config.startNumber,
        digitCount = config.digitCount,
        preserveExtension = config.preserveExtension,
        sortStrategy = config.sortStrategy.name
    )
    
    return TemplateEntity(
        id = id,
        name = name,
        pattern = pattern,
        configJson = json.encodeToString(configData),
        isFavorite = isFavorite,
        createdAt = createdAt,
        lastUsedAt = lastUsedAt
    )
}
