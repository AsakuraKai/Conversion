package com.example.conversion.di

import com.example.conversion.domain.repository.PermissionsRepository
import com.example.conversion.domain.repository.PreferencesRepository
import com.example.conversion.domain.usecase.permissions.CheckPermissionsUseCase
import com.example.conversion.domain.usecase.permissions.GetRequiredPermissionsUseCase
import com.example.conversion.domain.usecase.permissions.HasMediaAccessUseCase
import com.example.conversion.domain.usecase.permissions.ObservePermissionsUseCase
import com.example.conversion.domain.usecase.settings.GetUserPreferencesUseCase
import com.example.conversion.domain.usecase.settings.SetThemeModeUseCase
import com.example.conversion.domain.usecase.settings.SetUseDynamicColorsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Hilt module for providing domain layer dependencies.
 * Use cases and domain models are provided here.
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    
    @Provides
    @Singleton
    fun provideGetUserPreferencesUseCase(
        repository: PreferencesRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): GetUserPreferencesUseCase = GetUserPreferencesUseCase(repository, dispatcher)
    
    @Provides
    @Singleton
    fun provideSetThemeModeUseCase(
        repository: PreferencesRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): SetThemeModeUseCase = SetThemeModeUseCase(repository, dispatcher)
    
    @Provides
    @Singleton
    fun provideSetUseDynamicColorsUseCase(
        repository: PreferencesRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): SetUseDynamicColorsUseCase = SetUseDynamicColorsUseCase(repository, dispatcher)
    
    // Permissions Use Cases
    @Provides
    @Singleton
    fun provideCheckPermissionsUseCase(
        repository: PermissionsRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): CheckPermissionsUseCase = CheckPermissionsUseCase(repository, dispatcher)
    
    @Provides
    @Singleton
    fun provideGetRequiredPermissionsUseCase(
        repository: PermissionsRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): GetRequiredPermissionsUseCase = GetRequiredPermissionsUseCase(repository, dispatcher)
    
    @Provides
    @Singleton
    fun provideHasMediaAccessUseCase(
        repository: PermissionsRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): HasMediaAccessUseCase = HasMediaAccessUseCase(repository, dispatcher)
    
    @Provides
    @Singleton
    fun provideObservePermissionsUseCase(
        repository: PermissionsRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): ObservePermissionsUseCase = ObservePermissionsUseCase(repository, dispatcher)
}
