package com.example.conversion.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier annotations for different types of dispatchers
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * Hilt module for providing coroutine dispatchers.
 * This allows for easy testing by injecting test dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
    
    @Provides
    @Singleton
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
