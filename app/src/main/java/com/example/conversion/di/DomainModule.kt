package com.example.conversion.di

import com.example.conversion.domain.repository.PreferencesRepository
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
}
