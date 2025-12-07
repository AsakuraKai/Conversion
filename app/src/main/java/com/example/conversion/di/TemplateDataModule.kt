package com.example.conversion.di

import com.example.conversion.data.repository.TemplateRepositoryImpl
import com.example.conversion.domain.repository.TemplateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection module for Template Management feature (Chunk 12).
 * Provides TemplateRepository implementation for saving and managing rename templates.
 * 
 * This module supports the Pattern Templates feature, allowing users to:
 * - Save rename configurations as templates
 * - Retrieve and reuse saved templates
 * - Mark templates as favorites
 * - Track template usage
 * - Delete unwanted templates
 * 
 * **Current Implementation:**
 * Uses in-memory storage (TemplateRepositoryImpl with MutableStateFlow).
 * See MOCK_IMPLEMENTATIONS.md for production upgrade path.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TemplateDataModule {

    /**
     * Binds TemplateRepository interface to TemplateRepositoryImpl.
     * 
     * NOTE: Current implementation uses in-memory storage for rapid development.
     * Will be upgraded to Room database with persistent storage when needed.
     * 
     * @see TemplateRepositoryImpl
     */
    @Binds
    @Singleton
    abstract fun bindTemplateRepository(
        impl: TemplateRepositoryImpl
    ): TemplateRepository
}
