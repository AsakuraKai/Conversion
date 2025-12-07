package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.conversion.domain.model.FileTag

/**
 * Room database entity for storing file tags.
 *
 * @property id Unique identifier for this tag
 * @property name Display name of the tag
 * @property color Hex color code for the tag
 * @property createdAt Timestamp when tag was created
 */
@Entity(tableName = "file_tags")
data class TagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String,
    val createdAt: Long
) {
    /**
     * Converts this entity to a domain model.
     *
     * @return FileTag domain model
     */
    fun toDomain(): FileTag {
        return FileTag(
            id = id,
            name = name,
            color = color,
            createdAt = createdAt
        )
    }

    companion object {
        /**
         * Creates an entity from a domain model.
         *
         * @param tag The domain model to convert
         * @return TagEntity for database storage
         */
        fun fromDomain(tag: FileTag): TagEntity {
            return TagEntity(
                id = tag.id,
                name = tag.name,
                color = tag.color,
                createdAt = tag.createdAt
            )
        }
    }
}
