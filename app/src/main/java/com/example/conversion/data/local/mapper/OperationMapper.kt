package com.example.conversion.data.local.mapper

import android.net.Uri
import com.example.conversion.data.local.entity.OperationEntity
import com.example.conversion.domain.model.RenameOperation

/**
 * Mapper for converting between RenameOperation domain model and OperationEntity.
 * 
 * Handles conversion of Uri objects to/from string representation for database storage.
 */
object OperationMapper {
    
    /**
     * Converts OperationEntity to domain RenameOperation.
     */
    fun OperationEntity.toDomain(): RenameOperation {
        return RenameOperation(
            id = id,
            originalUri = Uri.parse(originalUriString),
            newUri = Uri.parse(newUriString),
            originalName = originalName,
            newName = newName,
            timestamp = timestamp
        )
    }
    
    /**
     * Converts domain RenameOperation to OperationEntity.
     */
    fun RenameOperation.toEntity(): OperationEntity {
        return OperationEntity(
            id = id,
            originalUriString = originalUri.toString(),
            newUriString = newUri.toString(),
            originalName = originalName,
            newName = newName,
            timestamp = timestamp
        )
    }
}
