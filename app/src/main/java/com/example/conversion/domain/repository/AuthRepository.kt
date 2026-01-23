package com.example.conversion.domain.repository

import com.example.conversion.domain.model.AuthUser

/**
 * Repository interface for Firebase Authentication operations
 *
 * Provides methods for:
 * - Anonymous authentication (initial access)
 * - Google Sign-In (persistent accounts)
 * - User state observation
 * - Sign-out functionality
 *
 * Authentication Flow:
 * 1. App starts → signInAnonymously() for immediate access
 * 2. User wants cloud sync → signInWithGoogle() to link account
 * 3. Signed-in state persists across app restarts
 */
interface AuthRepository {
    /**
     * Sign in anonymously for immediate app access
     *
     * Creates a temporary Firebase user that can be upgraded
     * to a permanent account later via Google Sign-In.
     *
     * @return Result containing the authenticated user or error
     */
    suspend fun signInAnonymously(): Result<AuthUser>

    /**
     * Sign in with Google account
     *
     * Launches OAuth flow and creates/links Firebase user.
     * If user was previously signed in anonymously, their data
     * can be preserved via account linking.
     *
     * @return Result containing the authenticated user or error
     */
    suspend fun signInWithGoogle(): Result<AuthUser>

    /**
     * Get currently authenticated user
     *
     * @return Current user if signed in, null otherwise
     */
    suspend fun getCurrentUser(): AuthUser?

    /**
     * Sign out the current user
     *
     * Clears authentication state and returns to anonymous access.
     * Note: This does NOT delete user data from Firestore.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Check if user is authenticated (not anonymous)
     *
     * @return True if user is signed in with a permanent account
     */
    suspend fun isAuthenticated(): Boolean
}
