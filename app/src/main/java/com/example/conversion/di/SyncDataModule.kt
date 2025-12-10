package com.example.conversion.di

import com.example.conversion.data.repository.CloudSyncRepositoryImpl
import com.example.conversion.data.repository.SyncRepositoryImpl
import com.example.conversion.domain.repository.CloudSyncRepository
import com.example.conversion.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for cloud sync functionality
 * 
 * Provides:
 * - SyncRepository binding to SyncRepositoryImpl (Firestore)
 * - CloudSyncRepository binding to CloudSyncRepositoryImpl (Firebase Storage)
 * 
 * Scope: Singleton (one instance per app)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncDataModule {
    
    /**
     * Binds the SyncRepository interface to its Firebase Firestore implementation
     * 
     * Handles user preferences sync across devices using Firestore.
     */
    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        impl: SyncRepositoryImpl
    ): SyncRepository
    
    /**
     * Binds the CloudSyncRepository interface to its Firebase Storage implementation
     * 
     * Handles file backup and sync to Firebase Cloud Storage.
     */
    @Binds
    @Singleton
    abstract fun bindCloudSyncRepository(
        impl: CloudSyncRepositoryImpl
    ): CloudSyncRepository
}
