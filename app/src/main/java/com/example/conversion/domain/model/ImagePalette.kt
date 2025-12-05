package com.example.conversion.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Represents a color palette extracted from an image.
 * Used for dynamic theming based on image colors.
 *
 * @property dominantColor The dominant color in the image (most common)
 * @property vibrantColor A vibrant color in the image (saturated and bright)
 * @property mutedColor A muted color in the image (desaturated and calm)
 * @property darkVibrantColor A dark vibrant variant (for dark themes)
 * @property lightVibrantColor A light vibrant variant (for light themes)
 * @property darkMutedColor A dark muted variant
 * @property lightMutedColor A light muted variant
 */
data class ImagePalette(
    val dominantColor: Color?,
    val vibrantColor: Color?,
    val mutedColor: Color?,
    val darkVibrantColor: Color? = null,
    val lightVibrantColor: Color? = null,
    val darkMutedColor: Color? = null,
    val lightMutedColor: Color? = null
) {
    /**
     * Returns true if at least one color was successfully extracted.
     */
    val hasColors: Boolean
        get() = dominantColor != null || vibrantColor != null || mutedColor != null
    
    /**
     * Returns the best color for primary theme color.
     * Prefers vibrant, falls back to dominant, then muted.
     */
    val primaryColor: Color?
        get() = vibrantColor ?: dominantColor ?: mutedColor
    
    /**
     * Returns the best color for secondary theme color.
     * Prefers muted, falls back to dark vibrant.
     */
    val secondaryColor: Color?
        get() = mutedColor ?: darkVibrantColor
    
    /**
     * Returns a color suitable for dark theme primary.
     */
    val darkThemePrimary: Color?
        get() = darkVibrantColor ?: dominantColor
    
    /**
     * Returns a color suitable for light theme primary.
     */
    val lightThemePrimary: Color?
        get() = lightVibrantColor ?: vibrantColor
}
