package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Room database entity for the many-to-many relationship between files and tags.
 *
 * This is a cross-reference (junction) table that associates file URIs with tag IDs.
 *
 * @property fileUriString String representation of the file URI
 * @property tagId ID of the tag
 */
@Entity(
    tableName = "file_tag_cross_ref",
    primaryKeys = ["fileUriString", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tagId"]),
        Index(value = ["fileUriString"])
    ]
)
data class FileTagCrossRef(
    val fileUriString: String,
    val tagId: String
)
