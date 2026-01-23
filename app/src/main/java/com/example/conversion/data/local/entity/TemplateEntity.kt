package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing Reusable Templates.
 *
 * Mapping functions are in TemplateMapper.
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
