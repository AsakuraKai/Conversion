package com.example.conversion.di

import android.content.Context
import com.example.conversion.data.repository.MetadataRepositoryImpl
import com.example.conversion.domain.repository.MetadataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing metadata-related dependencies.
 * Provides MetadataRepository implementation for EXIF data extraction.
 */
@Module
@InstallIn(SingletonComponent::class)
object MetadataDataModule {
    
    /**
     * Provides singleton instance of MetadataRepository.
     * 
     * @param context Application context for accessing ContentResolver
     * @return MetadataRepository implementation
     */
    @Provides
    @Singleton
    fun provideMetadataRepository(
        @ApplicationContext context: Context
    ): MetadataRepository = MetadataRepositoryImpl(context)
}
