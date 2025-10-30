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
}
