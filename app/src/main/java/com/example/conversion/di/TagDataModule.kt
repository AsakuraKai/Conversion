package com.example.conversion.di

import com.example.conversion.data.repository.TagRepositoryImpl
import com.example.conversion.domain.repository.TagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection module for Tag System feature (Chunk 16).
 * Provides TagRepository implementation for managing file tags.
 * 
 * This module supports the Tag System feature, allowing users to:
 * - Create and manage custom tags
 * - Apply tags to files for organization
 * - Search files by tags
 * - Filter and categorize files using tags
 * - Track tagged files
 * 
 * **Current Implementation:**
 * Uses in-memory storage (TagRepositoryImpl with MutableStateFlow).
 * See MOCK_IMPLEMENTATIONS.md for production upgrade path.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TagDataModule {

    /**
     * Binds TagRepository interface to TagRepositoryImpl.
     * 
     * NOTE: Current implementation uses in-memory storage for rapid development.
     * Will be upgraded to Room database with persistent storage and proper
     * many-to-many relationships when needed.
     * 
     * @see TagRepositoryImpl
     */
    @Binds
    @Singleton
    abstract fun bindTagRepository(
        impl: TagRepositoryImpl
    ): TagRepository
}
