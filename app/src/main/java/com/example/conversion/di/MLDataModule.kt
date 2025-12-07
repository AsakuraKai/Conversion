package com.example.conversion.di

import com.example.conversion.data.repository.MLRepositoryImpl
import com.example.conversion.domain.repository.MLRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection module for Machine Learning features.
 * 
 * Provides bindings for ML Kit integration and AI-powered filename suggestions.
 * Currently uses mock implementation; will be upgraded to real ML Kit when ready.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MLDataModule {
    
    /**
     * Binds MLRepository implementation.
     * 
     * **Current:** Mock implementation for development
     * **Production:** Will use real ML Kit Image Labeling API
     */
    @Binds
    @Singleton
    abstract fun bindMLRepository(
        impl: MLRepositoryImpl
    ): MLRepository
}
