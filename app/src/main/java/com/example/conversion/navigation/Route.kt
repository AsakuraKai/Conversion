package com.example.conversion.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
    
    @Serializable
    data object FileSelection : Route
    
    @Serializable
    data class RenameConfig(val fileCount: Int = 1) : Route
    
    @Serializable
    data object Preview : Route
    
    @Serializable
    data object RenameProgress : Route
    
    @Serializable
    data object FolderSelector : Route
    
    @Serializable
    data object Monitoring : Route
    
    @Serializable
    data object Settings : Route
    
    @Serializable
    data object TagManagement : Route
    
    @Serializable
    data object TemplateManagement : Route
    
    // Future features
    @Serializable
    data object FormatConverter : Route
    
    @Serializable
    data object BookReader : Route
}
