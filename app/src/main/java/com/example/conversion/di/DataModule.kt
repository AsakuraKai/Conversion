package com.example.conversion.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.conversion.data.repository.PreferencesRepositoryImpl
import com.example.conversion.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "conversion_preferences")

/**
 * Hilt module for providing data layer dependencies.
 * Repositories, data sources, and storage components are provided here.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore
    
    @Provides
    @Singleton
    fun providePreferencesRepository(
        dataStore: DataStore<Preferences>
    ): PreferencesRepository = PreferencesRepositoryImpl(dataStore)
}
