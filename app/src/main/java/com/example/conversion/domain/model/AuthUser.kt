package com.example.conversion.domain.model

/**
 * Domain model representing an authenticated user
 *
 * Contains user information from Firebase Authentication.
 * Used for identifying users in Firestore operations and
 * displaying account information in the UI.
 *
 * @property uid Unique user ID from Firebase
 * @property email User's email address (null for anonymous users)
 * @property displayName User's display name (null for anonymous users)
 * @property isAnonymous True if user is signed in anonymously
 */
data class AuthUser(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val isAnonymous: Boolean = false
) {
    /**
     * Returns true if user has a permanent account (not anonymous)
     */
    val isPermanent: Boolean
        get() = !isAnonymous && email != null
}
