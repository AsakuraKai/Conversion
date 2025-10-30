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
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColors: Boolean = true
)
