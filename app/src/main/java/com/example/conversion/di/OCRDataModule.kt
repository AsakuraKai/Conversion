package com.example.conversion.di

import com.example.conversion.data.repository.OCRRepositoryImpl
import com.example.conversion.domain.repository.OCRRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection module for OCR (Optical Character Recognition) features.
 * 
 * Provides bindings for ML Kit Text Recognition integration and text extraction.
 * Currently uses mock implementation; will be upgraded to real ML Kit when ready.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OCRDataModule {
    
    /**
     * Binds OCRRepository implementation.
     * 
     * **Current:** Mock implementation for development
     * **Production:** Will use real ML Kit Text Recognition API
     */
    @Binds
    @Singleton
    abstract fun bindOCRRepository(
        impl: OCRRepositoryImpl
    ): OCRRepository
}
