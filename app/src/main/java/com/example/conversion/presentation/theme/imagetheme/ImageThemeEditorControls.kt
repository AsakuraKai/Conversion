package com.example.conversion.presentation.theme.imagetheme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Control components for Image Theme Editor.
 */

@Composable
fun ImagePositionControl(
    selectedPosition: ImagePosition,
    onPositionChange: (ImagePosition) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Image Position",
            style = MaterialTheme.typography.titleMedium
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImagePosition.entries.forEach { position ->
                    Surface(
                        onClick = { onPositionChange(position) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        color = if (selectedPosition == position) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPosition == position,
                                onClick = { onPositionChange(position) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = position.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlurControl(
    blurAmount: Float,
    onBlurChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Blur Effect",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${blurAmount.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = blurAmount,
            onValueChange = onBlurChange,
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Apply blur to soften background image",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ColorOverlayControl(
    overlayColor: Color,
    overlayOpacity: Float,
    onColorChange: (Color) -> Unit,
    onOpacityChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Color Overlay",
            style = MaterialTheme.typography.titleMedium
        )
        
        // Color Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Overlay Color",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Surface(
                onClick = { showColorPicker = !showColorPicker },
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    ),
                color = overlayColor,
                shape = MaterialTheme.shapes.small
            ) {}
        }
        
        // Predefined color picker
        if (showColorPicker) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                items(presetColors) { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, MaterialTheme.shapes.small)
                            .border(
                                width = if (color == overlayColor) 3.dp else 1.dp,
                                color = if (color == overlayColor) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable { onColorChange(color) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (color == overlayColor) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Opacity Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Opacity",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${overlayOpacity.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = overlayOpacity,
            onValueChange = onOpacityChange,
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BrightnessControl(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Brightness",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (brightness >= 0) "+${brightness.toInt()}" else brightness.toInt().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = -100f..100f,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Adjust image brightness (-100 to +100)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ContrastControl(
    contrast: Float,
    onContrastChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contrast",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (contrast >= 0) "+${contrast.toInt()}" else contrast.toInt().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = contrast,
            onValueChange = onContrastChange,
            valueRange = -100f..100f,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Adjust image contrast (-100 to +100)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Preset colors for overlay selection.
 */
private val presetColors = listOf(
    Color.Black,
    Color.White,
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Yellow,
    Color.Cyan,
    Color.Magenta,
    Color(0xFF212121), // Dark Gray
    Color(0xFF757575), // Gray
    Color(0xFFBDBDBD), // Light Gray
    Color(0xFFE0E0E0), // Very Light Gray
    Color(0xFF1976D2), // Blue
    Color(0xFF388E3C), // Green
    Color(0xFFD32F2F), // Red
    Color(0xFFF57C00)  // Orange
)
