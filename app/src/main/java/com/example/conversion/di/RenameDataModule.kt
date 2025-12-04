package com.example.conversion.di

import android.content.ContentResolver
import android.content.Context
import com.example.conversion.data.manager.FileOperationsManager
import com.example.conversion.data.repository.FileRenameRepositoryImpl
import com.example.conversion.domain.repository.FileRenameRepository
import com.example.conversion.domain.usecase.rename.ExecuteBatchRenameUseCase
import com.example.conversion.domain.usecase.rename.GenerateFilenameUseCase
import com.example.conversion.domain.usecase.rename.ValidateFilenameUseCase
import com.example.conversion.domain.usecase.sort.SortFilesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Hilt module for providing rename-related dependencies.
 * Includes use cases, repositories, and managers for batch rename operations.
 */
@Module
@InstallIn(SingletonComponent::class)
object RenameDataModule {

    @Provides
    @Singleton
    fun provideFileOperationsManager(): FileOperationsManager = FileOperationsManager()

    @Provides
    @Singleton
    fun provideGenerateFilenameUseCase(): GenerateFilenameUseCase = GenerateFilenameUseCase()

    @Provides
    @Singleton
    fun provideValidateFilenameUseCase(): ValidateFilenameUseCase = ValidateFilenameUseCase()

    @Provides
    @Singleton
    fun provideSortFilesUseCase(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): SortFilesUseCase = SortFilesUseCase(dispatcher)

    @Provides
    @Singleton
    fun provideFileRenameRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): FileRenameRepository = FileRenameRepositoryImpl(
        contentResolver = context.contentResolver,
        ioDispatcher = ioDispatcher
    )

    @Provides
    @Singleton
    fun provideExecuteBatchRenameUseCase(
        fileRenameRepository: FileRenameRepository,
        generateFilenameUseCase: GenerateFilenameUseCase,
        validateFilenameUseCase: ValidateFilenameUseCase,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): ExecuteBatchRenameUseCase = ExecuteBatchRenameUseCase(
        fileRenameRepository = fileRenameRepository,
        generateFilenameUseCase = generateFilenameUseCase,
        validateFilenameUseCase = validateFilenameUseCase,
        dispatcher = dispatcher
    )
}
