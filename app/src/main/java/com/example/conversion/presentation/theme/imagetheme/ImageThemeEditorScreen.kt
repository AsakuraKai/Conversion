package com.example.conversion.presentation.theme.imagetheme

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

/**
 * Image Theme Editor Screen.
 * Provides advanced customization for image-based themes including:
 * - Image position (Center, Stretch, Fit, Fill, Tile)
 * - Color overlay with opacity
 * - Blur effect
 * - Brightness/Contrast adjustments
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageThemeEditorScreen(
    imageUri: Uri?,
    onNavigateBack: () -> Unit,
    onApplySettings: (ImageThemeSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember {
        mutableStateOf(ImageThemeSettings())
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customize Background") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { settings = ImageThemeSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset to defaults"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onApplySettings(settings) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Settings")
                    }
                    
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Live Preview Area
            ImageThemePreview(
                imageUri = imageUri,
                settings = settings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            
            // Controls Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Position Control
                ImagePositionControl(
                    selectedPosition = settings.position,
                    onPositionChange = { settings = settings.copy(position = it) }
                )
                
                // Blur Control
                BlurControl(
                    blurAmount = settings.blurAmount,
                    onBlurChange = { settings = settings.copy(blurAmount = it) }
                )
                
                // Overlay Control
                ColorOverlayControl(
                    overlayColor = settings.overlayColor,
                    overlayOpacity = settings.overlayOpacity,
                    onColorChange = { settings = settings.copy(overlayColor = it) },
                    onOpacityChange = { settings = settings.copy(overlayOpacity = it) }
                )
                
                // Brightness Control
                BrightnessControl(
                    brightness = settings.brightness,
                    onBrightnessChange = { settings = settings.copy(brightness = it) }
                )
                
                // Contrast Control
                ContrastControl(
                    contrast = settings.contrast,
                    onContrastChange = { settings = settings.copy(contrast = it) }
                )
            }
        }
    }
}

/**
 * Live preview of the image with applied settings.
 */
@Composable
private fun ImageThemePreview(
    imageUri: Uri?,
    settings: ImageThemeSettings,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                val painter = rememberAsyncImagePainter(imageUri)
                val contentScale = when (settings.position) {
                    ImagePosition.CENTER -> ContentScale.None
                    ImagePosition.STRETCH -> ContentScale.FillBounds
                    ImagePosition.FIT -> ContentScale.Fit
                    ImagePosition.FILL -> ContentScale.Crop
                    ImagePosition.TILE -> ContentScale.None
                }
                
                // Apply color matrix for brightness/contrast
                val colorMatrix = ColorMatrix().apply {
                    setToSaturation(1f)
                    val brightness = settings.brightness / 100f
                    val contrast = (settings.contrast + 100f) / 100f
                    this.set(
                        floatArrayOf(
                            contrast, 0f, 0f, 0f, brightness,
                            0f, contrast, 0f, 0f, brightness,
                            0f, 0f, contrast, 0f, brightness,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                }
                
                Image(
                    painter = painter,
                    contentDescription = "Background preview",
                    contentScale = contentScale,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(settings.blurAmount.dp)
                )
                
                // Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            settings.overlayColor.copy(
                                alpha = settings.overlayOpacity / 100f
                            )
                        )
                )
            } else {
                Text(
                    text = "No image selected",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Preview Label
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Live Preview",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Data class representing image theme settings.
 */
data class ImageThemeSettings(
    val position: ImagePosition = ImagePosition.CENTER,
    val blurAmount: Float = 0f,
    val overlayColor: Color = Color.Black,
    val overlayOpacity: Float = 0f,
    val brightness: Float = 0f,
    val contrast: Float = 0f
)

/**
 * Enum representing image position options.
 */
enum class ImagePosition(val displayName: String) {
    CENTER("Center"),
    STRETCH("Stretch"),
    FIT("Fit"),
    FILL("Fill"),
    TILE("Tile")
}
