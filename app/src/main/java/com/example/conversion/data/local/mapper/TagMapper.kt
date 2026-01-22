package com.example.conversion.data.local.mapper

import com.example.conversion.data.local.entity.TagEntity
import com.example.conversion.domain.model.FileTag

/**
 * Mapper for converting between FileTag domain model and TagEntity.
 * 
 * Handles simple 1:1 property mapping for file tags.
 */
object TagMapper {
    
    /**
     * Converts TagEntity to domain FileTag.
     */
    fun TagEntity.toDomain(): FileTag {
        return FileTag(
            id = id,
            name = name,
            color = color,
            createdAt = createdAt
        )
    }
    
    /**
     * Converts domain FileTag to TagEntity.
     */
    fun FileTag.toEntity(): TagEntity {
        return TagEntity(
            id = id,
            name = name,
            color = color,
            createdAt = createdAt
        )
    }
}
