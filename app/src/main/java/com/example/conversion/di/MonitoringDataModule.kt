package com.example.conversion.di

import com.example.conversion.data.repository.FolderMonitorRepositoryImpl
import com.example.conversion.domain.repository.FileRenameRepository
import com.example.conversion.domain.repository.FolderMonitorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Dependency Injection module for File Observer/Monitoring feature (Chunk 9).
 * Provides FolderMonitorRepository implementation for real-time folder monitoring.
 * 
 * This module supports the File Observer feature, allowing users to:
 * - Monitor folders for new files in real-time
 * - Automatically rename files as they are added
 * - Track monitoring status and statistics
 * - Receive notifications for file events
 * 
 * Note: Current implementation is a mock/simplified version due to scoped storage restrictions.
 * Production implementation would require:
 * - Storage Access Framework (SAF) for folder access
 * - Foreground service for background monitoring
 * - Proper permission handling
 */
@Module
@InstallIn(SingletonComponent::class)
object MonitoringDataModule {

    /**
     * Provides FolderMonitorRepository implementation.
     * 
     * NOTE: This is a mock implementation using FileObserver.
     * In production with Android 10+ scoped storage:
     * - Would use DocumentFile API with SAF
     * - Requires foreground service for background operation
     * - May need WorkManager for periodic checks as FileObserver may not work with scoped storage
     * - Needs POST_NOTIFICATIONS permission for foreground service notification
     * 
     * Dependencies:
     * - FileRenameRepository: For renaming files when detected
     * - IoDispatcher: For background operations
     */
    @Provides
    @Singleton
    fun provideFolderMonitorRepository(
        fileRenameRepository: FileRenameRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): FolderMonitorRepository = FolderMonitorRepositoryImpl(
        fileRenameRepository = fileRenameRepository,
        ioDispatcher = ioDispatcher
    )
}
