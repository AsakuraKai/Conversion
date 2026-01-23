package com.example.conversion.di

import com.example.conversion.data.repository.AuthRepositoryImpl
import com.example.conversion.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for Firebase services
 *
 * Provides:
 * - FirebaseAuth instance
 * - FirebaseFirestore instance
 * - FirebaseStorage instance
 * - AuthRepository binding
 *
 * All Firebase instances are Singletons to ensure proper
 * connection pooling and resource management.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides Firebase Authentication instance
     *
     * FirebaseAuth manages user authentication state and tokens.
     * The instance is automatically initialized from google-services.json
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Provides Firebase Firestore instance
     *
     * Firestore is a NoSQL cloud database for storing and syncing data.
     * Enables offline persistence by default.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            // Enable offline persistence
            firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }
    }

    /**
     * Provides Firebase Storage instance
     *
     * Firebase Storage provides cloud file storage with automatic
     * retry and resumable uploads.
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
}

/**
 * Dependency injection module for Auth repository binding
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {

    /**
     * Binds AuthRepository interface to its Firebase implementation
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
