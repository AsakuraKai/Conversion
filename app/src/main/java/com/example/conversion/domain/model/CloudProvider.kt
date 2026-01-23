package com.example.conversion.domain.model

/**
 * Enum representing supported cloud storage providers
 * 
 * Used for cloud sync functionality where renamed files can be
 * backed up to various cloud storage services.
 * 
 * @property displayName User-friendly name shown in UI
 */
enum class CloudProvider(val displayName: String) {
    /**
     * Google Drive cloud storage
     */
    GOOGLE_DRIVE("Google Drive"),
    
    /**
     * Dropbox cloud storage
     */
    DROPBOX("Dropbox"),
    
    /**
     * Microsoft OneDrive cloud storage
     */
    ONEDRIVE("OneDrive")
}
