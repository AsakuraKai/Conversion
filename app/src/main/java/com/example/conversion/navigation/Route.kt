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
    data object PermissionsManagement : Route
    
    @Serializable
    data object TagManagement : Route
    
    @Serializable
    data object TemplateManagement : Route
    
    // ========== SMART FEATURES ==========
    
    @Serializable
    data object History : Route
    
    @Serializable
    data object AISuggestions : Route
    
    @Serializable
    data object RegexBuilder : Route
    
    @Serializable
    data object MetadataPicker : Route
    
    // ========== INTEGRATION FEATURES ==========
    
    @Serializable
    data object CloudSync : Route
    
    @Serializable
    data object Account : Route
    
    @Serializable
    data object ActivityLog : Route
    
    @Serializable
    data object QRDisplay : Route
    
    @Serializable
    data object QRScanner : Route
    
    @Serializable
    data object OCR : Route
    
    // ========== QR FUNCTIONS ==========
    
    @Serializable
    data object QRFunctions : Route
    
    @Serializable
    data object QRComparison : Route
    
    @Serializable
    data object ImageToQR : Route
    
    @Serializable
    data object QRToImage : Route
    
    // ========== THEME CUSTOMIZATION ==========
    
    @Serializable
    data object ImageThemeEditor : Route
    
    // ========== FUTURE FEATURES ==========
    
    @Serializable
    data object FormatConverter : Route
    
    @Serializable
    data object BookReader : Route
}
