package com.example.conversion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.conversion.data.local.dao.ActivityLogDao
import com.example.conversion.data.local.dao.OperationDao
import com.example.conversion.data.local.dao.TagDao
import com.example.conversion.data.local.dao.TemplateDao
import com.example.conversion.data.local.entity.ActivityLogEntity
import com.example.conversion.data.local.entity.FileTagCrossRef
import com.example.conversion.data.local.entity.OperationEntity
import com.example.conversion.data.local.entity.TagEntity
import com.example.conversion.data.local.entity.TemplateEntity

/**
 * Room database for the Conversion app.
 * Contains all entities and provides DAOs for accessing them.
 *
 * Version 1 includes:
 * - Templates (Reusable Templates/presets)
 * - Operations (undo/redo history)
 * - Tags (file organization tags)
 * - FileTagCrossRef (many-to-many file-tag relationships)
 * - ActivityLogs (user activity tracking)
 */
@Database(
    entities = [
        TemplateEntity::class,
        OperationEntity::class,
        TagEntity::class,
        FileTagCrossRef::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to template operations.
     */
    abstract fun templateDao(): TemplateDao
    
    /**
     * Provides access to undo/redo operations.
     */
    abstract fun operationDao(): OperationDao
    
    /**
     * Provides access to tag and file-tag operations.
     */
    abstract fun tagDao(): TagDao
    
    /**
     * Provides access to activity log operations.
     */
    abstract fun activityLogDao(): ActivityLogDao
    
    companion object {
        /**
         * Name of the database file.
         */
        const val DATABASE_NAME = "conversion_database"
    }
}
