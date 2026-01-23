package com.example.conversion.di

import com.example.conversion.data.repository.ActivityRepositoryImpl
import com.example.conversion.domain.repository.ActivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for activity log-related components.
 *
 * Provides bindings for the activity logging system.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ActivityDataModule {

    /**
     * Binds the ActivityRepository implementation.
     *
     * Currently provides a mock implementation using in-memory storage.
     * Replace with Room-based implementation for production.
     */
    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        impl: ActivityRepositoryImpl
    ): ActivityRepository
}
