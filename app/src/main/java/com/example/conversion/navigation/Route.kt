package com.example.conversion.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
    
    @Serializable
    data object BatchProcess : Route
    
    @Serializable
    data object FormatConverter : Route
    
    @Serializable
    data object BookReader : Route
    
    @Serializable
    data object Settings : Route
    
    @Serializable
    data class RenameConfig(val fileCount: Int = 1) : Route
    
    @Serializable
    data object Preview : Route
    
    @Serializable
    data object FolderSelector : Route
}
