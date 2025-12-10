package com.example.conversion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.conversion.domain.model.RenameOperation
import android.net.Uri

/**
 * Room database entity for storing rename operations.
 *
 * This entity represents a single rename operation in the database,
 * storing all necessary information to support undo/redo functionality.
 *
 * @property id Unique identifier for this operation
 * @property originalUriString String representation of the original file URI
 * @property newUriString String representation of the new file URI
 * @property originalName Original filename (including extension)
 * @property newName New filename (including extension)
 * @property timestamp Time when the operation was performed (epoch milliseconds)
 * @property stackPosition Position in the undo/redo stack
 */
@Entity(tableName = "rename_operations")
data class OperationEntity(
    @PrimaryKey
    val id: String,
    val originalUriString: String,
    val newUriString: String,
    val originalName: String,
    val newName: String,
    val timestamp: Long,
    val stackPosition: Int = 0
) {
    /**
     * Converts this entity to a domain model.
     *
     * @return RenameOperation domain model
     */
    fun toDomain(): RenameOperation {
        return RenameOperation(
            id = id,
            originalUri = Uri.parse(originalUriString),
            newUri = Uri.parse(newUriString),
            originalName = originalName,
            newName = newName,
            timestamp = timestamp
        )
    }

    companion object {
        /**
         * Creates an entity from a domain model.
         *
         * @param operation The RenameOperation to convert
         * @return OperationEntity for database storage
         */
        fun fromDomain(operation: RenameOperation): OperationEntity {
            return OperationEntity(
                id = operation.id,
                originalUriString = operation.originalUri.toString(),
                newUriString = operation.newUri.toString(),
                originalName = operation.originalName,
                newName = operation.newName,
                timestamp = operation.timestamp
            )
        }
    }
}
