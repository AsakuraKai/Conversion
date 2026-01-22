package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity for storing file tags.
 *
 * Mapping functions are in TagMapper.
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
)
