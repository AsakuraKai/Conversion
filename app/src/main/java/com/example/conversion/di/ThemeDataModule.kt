package com.example.conversion.di

import android.content.Context
import com.example.conversion.data.repository.ThemeRepositoryImpl
import com.example.conversion.domain.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing theme-related dependencies.
 * Provides ThemeRepository implementation for color palette extraction.
 */
@Module
@InstallIn(SingletonComponent::class)
object ThemeDataModule {
    
    /**
     * Provides singleton instance of ThemeRepository.
     * 
     * @param context Application context for accessing ContentResolver
     * @return ThemeRepository implementation
     */
    @Provides
    @Singleton
    fun provideThemeRepository(
        @ApplicationContext context: Context
    ): ThemeRepository = ThemeRepositoryImpl(context)
}
