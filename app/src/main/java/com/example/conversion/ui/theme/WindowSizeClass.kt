package com.example.conversion.ui.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Provides current window size class for responsive layouts
 */
val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("No WindowSizeClass provided")
}

/**
 * Calculate WindowSizeClass from current configuration
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val width = with(density) { configuration.screenWidthDp.dp }
    val height = with(density) { configuration.screenHeightDp.dp }
    
    return WindowSizeClass.calculateFromSize(DpSize(width, height))
}

/**
 * Helper to determine if device is tablet-sized
 */
@Composable
fun isTablet(): Boolean {
    return LocalWindowSizeClass.current.widthSizeClass >= WindowWidthSizeClass.Medium
}

/**
 * Helper to determine if screen is in landscape
 */
@Composable
fun isLandscape(): Boolean {
    return LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp
}

/**
 * Get number of columns for grid layouts
 */
@Composable
fun getGridColumns(
    compactColumns: Int = 2,
    mediumColumns: Int = 3,
    expandedColumns: Int = 4
): Int {
    return when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumns
        WindowWidthSizeClass.Medium -> mediumColumns
        WindowWidthSizeClass.Expanded -> expandedColumns
        else -> compactColumns
    }
}
