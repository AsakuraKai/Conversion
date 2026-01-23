package com.example.conversion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.conversion.domain.model.ImagePalette
import com.example.conversion.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ConversionTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    // Custom palette from image (overrides dynamic color)
    customPalette: ImagePalette? = null,
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    
    // Determine if we should use dark theme
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
    }
    
    val colorScheme = when {
        // Custom palette takes highest priority
        customPalette != null && customPalette.hasColors -> {
            createColorSchemeFromPalette(customPalette, darkTheme)
        }
        
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Creates a Material 3 color scheme from an ImagePalette.
 * 
 * @param palette The color palette extracted from an image
 * @param darkTheme Whether to create a dark or light color scheme
 * @return A ColorScheme suitable for Material 3 theming
 */
@Composable
private fun createColorSchemeFromPalette(
    palette: ImagePalette,
    darkTheme: Boolean
) = if (darkTheme) {
    darkColorScheme(
        primary = palette.darkThemePrimary ?: palette.primaryColor ?: Purple80,
        secondary = palette.darkMutedColor ?: palette.secondaryColor ?: PurpleGrey80,
        tertiary = palette.mutedColor ?: Pink80,
        background = palette.dominantColor?.copy(alpha = 0.1f) ?: DarkColorScheme.background,
        surface = palette.dominantColor?.copy(alpha = 0.15f) ?: DarkColorScheme.surface
    )
} else {
    lightColorScheme(
        primary = palette.primaryColor ?: Purple40,
        secondary = palette.secondaryColor ?: PurpleGrey40,
        tertiary = palette.lightVibrantColor ?: palette.vibrantColor ?: Pink40,
        background = palette.dominantColor?.copy(alpha = 0.05f) ?: LightColorScheme.background,
        surface = palette.dominantColor?.copy(alpha = 0.08f) ?: LightColorScheme.surface
    )
}
