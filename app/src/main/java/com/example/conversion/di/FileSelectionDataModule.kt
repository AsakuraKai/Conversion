package com.example.conversion.di

import android.content.ContentResolver
import android.content.Context
import com.example.conversion.data.repository.MediaRepositoryImpl
import com.example.conversion.data.source.local.MediaStoreDataSource
import com.example.conversion.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Dependency Injection module for File Selection feature.
 * Provides MediaStore data sources and repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object FileSelectionDataModule {

    /**
     * Provides ContentResolver for MediaStore queries.
     */
    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver = context.contentResolver

    /**
     * Provides MediaStoreDataSource for querying media files.
     */
    @Provides
    @Singleton
    fun provideMediaStoreDataSource(
        contentResolver: ContentResolver
    ): MediaStoreDataSource = MediaStoreDataSource(contentResolver)

    /**
     * Provides MediaRepository implementation.
     */
    @Provides
    @Singleton
    fun provideMediaRepository(
        mediaStoreDataSource: MediaStoreDataSource,
        contentResolver: ContentResolver,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): MediaRepository = MediaRepositoryImpl(
        mediaStoreDataSource = mediaStoreDataSource,
        contentResolver = contentResolver,
        ioDispatcher = ioDispatcher
    )
}
