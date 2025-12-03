package com.example.conversion.di

import android.content.Context
import com.example.conversion.data.repository.FolderRepositoryImpl
import com.example.conversion.domain.repository.FolderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection module for Folder Selection feature (Chunk 6).
 * Provides FolderRepository implementation for folder browsing and management.
 * 
 * This module supports the Destination Folder Selector feature, allowing users to:
 * - Browse folder hierarchies
 * - Select destination folders for renamed files
 * - Create new folders
 */
@Module
@InstallIn(SingletonComponent::class)
object FolderDataModule {

    /**
     * Provides FolderRepository implementation.
     * 
     * NOTE: Current implementation uses basic File API for mock functionality.
     * Will be upgraded to full Storage Access Framework (SAF) when UI components are ready.
     */
    @Provides
    @Singleton
    fun provideFolderRepository(
        @ApplicationContext context: Context
    ): FolderRepository = FolderRepositoryImpl(context)
}
