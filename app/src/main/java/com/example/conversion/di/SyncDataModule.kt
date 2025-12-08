package com.example.conversion.di

import com.example.conversion.data.repository.SyncRepositoryImpl
import com.example.conversion.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for multi-device sync functionality
 * 
 * Provides:
 * - SyncRepository binding to SyncRepositoryImpl
 * 
 * Scope: Singleton (one instance per app)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncDataModule {
    
    /**
     * Binds the SyncRepository interface to its implementation
     * 
     * Currently uses mock implementation (SyncRepositoryImpl with in-memory storage).
     * In production, replace with Firebase Firestore implementation.
     */
    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        impl: SyncRepositoryImpl
    ): SyncRepository
}
