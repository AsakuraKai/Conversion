package com.example.conversion.di

import com.example.conversion.data.repository.HistoryRepositoryImpl
import com.example.conversion.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for history-related components.
 *
 * Provides bindings for the undo/redo history system.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryDataModule {

    /**
     * Binds the HistoryRepository implementation.
     *
     * Currently provides a mock implementation using in-memory storage.
     * Replace with Room-based implementation for production.
     */
    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepositoryImpl
    ): HistoryRepository
}
