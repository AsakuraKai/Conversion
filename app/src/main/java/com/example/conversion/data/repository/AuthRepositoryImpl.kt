package com.example.conversion.data.repository

import com.example.conversion.domain.model.AuthUser
import com.example.conversion.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of AuthRepository
 *
 * Handles authentication using Firebase Auth with support for:
 * - Anonymous sign-in (immediate access)
 * - Google Sign-In (permanent accounts)
 * - Persistent session management
 *
 * Firebase Auth automatically handles:
 * - Token refresh
 * - Session persistence across app restarts
 * - Secure credential storage
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInAnonymously(): Result<AuthUser> {
        return try {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user ?: return Result.failure(Exception("Sign-in failed"))
            
            Result.success(
                AuthUser(
                    uid = user.uid,
                    email = null,
                    displayName = null,
                    isAnonymous = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<AuthUser> {
        return try {
            // Note: In a real implementation, this would:
            // 1. Launch Google Sign-In intent
            // 2. Get Google ID token from result
            // 3. Create Firebase credential
            // 4. Sign in to Firebase
            //
            // For now, this is a placeholder that will be completed
            // when integrating with the UI layer (Activity/ViewModel)
            
            Result.failure(Exception("Google Sign-In must be implemented via Activity"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Complete Google Sign-In with ID token
     * Call this from ViewModel after receiving Google Sign-In result
     */
    suspend fun signInWithGoogleCredential(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user ?: return Result.failure(Exception("Sign-in failed"))
            
            Result.success(
                AuthUser(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    isAnonymous = false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        val user = firebaseAuth.currentUser ?: return null
        
        return AuthUser(
            uid = user.uid,
            email = user.email,
            displayName = user.displayName,
            isAnonymous = user.isAnonymous
        )
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        val user = firebaseAuth.currentUser
        return user != null && !user.isAnonymous
    }
}
