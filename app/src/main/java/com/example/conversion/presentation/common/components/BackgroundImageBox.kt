package com.example.conversion.presentation.common.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.conversion.presentation.theme.imagetheme.ImagePosition

/**
 * Background Image Box Component.
 * Displays a custom background image with applied theme settings globally across the app.
 * Can be used as a base layer in any screen that supports custom backgrounds.
 */
@Composable
fun BackgroundImageBox(
    imageUri: Uri?,
    position: ImagePosition = ImagePosition.CENTER,
    blurAmount: Float = 0f,
    overlayColor: Color = Color.Black,
    overlayOpacity: Float = 0f,
    brightness: Float = 0f,
    contrast: Float = 0f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background Image Layer
        if (imageUri != null) {
            val painter = rememberAsyncImagePainter(imageUri)
            val contentScale = when (position) {
                ImagePosition.CENTER -> ContentScale.None
                ImagePosition.STRETCH -> ContentScale.FillBounds
                ImagePosition.FIT -> ContentScale.Fit
                ImagePosition.FILL -> ContentScale.Crop
                ImagePosition.TILE -> ContentScale.None
            }
            
            // Apply color matrix for brightness/contrast
            val colorMatrix = ColorMatrix().apply {
                setToSaturation(1f)
                val brightnessValue = brightness / 100f
                val contrastValue = (contrast + 100f) / 100f
                this.set(
                    floatArrayOf(
                        contrastValue, 0f, 0f, 0f, brightnessValue,
                        0f, contrastValue, 0f, 0f, brightnessValue,
                        0f, 0f, contrastValue, 0f, brightnessValue,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            
            Image(
                painter = painter,
                contentDescription = "Background image",
                contentScale = contentScale,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier
                    .fillMaxSize()
                    .blur(blurAmount.dp)
            )
            
            // Overlay Layer
            if (overlayOpacity > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            overlayColor.copy(
                                alpha = overlayOpacity / 100f
                            )
                        )
                )
            }
        }
        
        // Content Layer
        content()
    }
}

/**
 * Simplified version without explicit settings - reads from preferences.
 */
@Composable
fun BackgroundImageBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // TODO: Read settings from preferences
    // For now, just render content without background
    Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}
