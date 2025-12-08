package com.example.conversion.domain.model

/**
 * Domain model representing theme preferences
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Domain model for user preferences/settings
 * 
 * Contains all user customizations that should be synced across devices:
 * - Theme and appearance settings
 * - Rename templates
 * - File tags
 * - Sync metadata
 * 
 * @property themeMode The theme preference (light, dark, or system)
 * @property useDynamicColors Whether to use Material You dynamic colors
 * @property templates List of saved rename templates
 * @property tags List of saved file tags
 * @property lastSyncTimestamp The last time preferences were synced (milliseconds since epoch), null if never synced
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColors: Boolean = true,
    val templates: List<RenameTemplate> = emptyList(),
    val tags: List<FileTag> = emptyList(),
    val lastSyncTimestamp: Long? = null
)
