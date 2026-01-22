package com.example.conversion.di

import android.content.Context
import com.example.conversion.data.local.preferences.SidebarPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for sidebar navigation dependencies.
 * 
 * Provides:
 * - SidebarPreferences for state persistence
 */
@Module
@InstallIn(SingletonComponent::class)
object SidebarModule {

    /**
     * Provides SidebarPreferences instance.
     * 
     * @param context Application context
     * @return SidebarPreferences instance
     */
    @Provides
    @Singleton
    fun provideSidebarPreferences(
        @ApplicationContext context: Context
    ): SidebarPreferences {
        return SidebarPreferences(context)
    }
}
