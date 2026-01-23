package com.example.conversion.domain.model

/**
 * Domain model representing cloud sync configuration
 * 
 * Contains user preferences for how and when files should be
 * synchronized with cloud storage providers.
 * 
 * @property provider Selected cloud storage provider
 * @property autoSync Whether automatic sync is enabled
 * @property syncInterval Minutes between automatic syncs (if autoSync is true)
 * @property syncOnWifiOnly Whether to sync only when connected to WiFi
 * @property enableBackup Whether to enable automatic backup of renamed files
 */
data class SyncConfig(
    val provider: CloudProvider? = null,
    val autoSync: Boolean = false,
    val syncInterval: Int = 60, // minutes
    val syncOnWifiOnly: Boolean = true,
    val enableBackup: Boolean = false
) {
    /**
     * Returns true if cloud sync is configured and enabled
     */
    val isConfigured: Boolean
        get() = provider != null
    
    /**
     * Returns true if auto backup is active
     */
    val isBackupActive: Boolean
        get() = isConfigured && enableBackup && autoSync
}
