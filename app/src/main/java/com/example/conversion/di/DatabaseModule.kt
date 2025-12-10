package com.example.conversion.di

import android.content.Context
import androidx.room.Room
import com.example.conversion.data.local.AppDatabase
import com.example.conversion.data.local.dao.ActivityLogDao
import com.example.conversion.data.local.dao.OperationDao
import com.example.conversion.data.local.dao.TagDao
import com.example.conversion.data.local.dao.TemplateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // TODO: Add proper migrations for production
            .build()
    }
    
    /**
     * Provides the TemplateDao.
     */
    @Provides
    @Singleton
    fun provideTemplateDao(database: AppDatabase): TemplateDao {
        return database.templateDao()
    }
    
    /**
     * Provides the OperationDao.
     */
    @Provides
    @Singleton
    fun provideOperationDao(database: AppDatabase): OperationDao {
        return database.operationDao()
    }
    
    /**
     * Provides the TagDao.
     */
    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }
    
    /**
     * Provides the ActivityLogDao.
     */
    @Provides
    @Singleton
    fun provideActivityLogDao(database: AppDatabase): ActivityLogDao {
        return database.activityLogDao()
    }
}
