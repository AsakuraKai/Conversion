package com.example.conversion.di

import com.example.conversion.data.repository.QRRepositoryImpl
import com.example.conversion.domain.repository.QRRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for QR code feature dependencies.
 * Provides QR repository for QR code generation and parsing.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class QRDataModule {

    /**
     * Binds the QR repository implementation.
     */
    @Binds
    @Singleton
    abstract fun bindQRRepository(
        qrRepositoryImpl: QRRepositoryImpl
    ): QRRepository
}
