package com.example.conversion.presentation.renameconfig.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Supported file conversion formats.
 */
enum class ConversionFormat(val displayName: String, val extension: String) {
    NONE("No Conversion", ""),
    JPG("JPEG", "jpg"),
    PNG("PNG", "png"),
    WEBP("WebP", "webp"),
    HEIC("HEIC", "heic"),
    PDF("PDF", "pdf"),
    MP4("MP4", "mp4"),
    MKV("MKV", "mkv"),
    AVI("AVI", "avi");
    
    companion object {
        fun imageFormats() = listOf(NONE, JPG, PNG, WEBP, HEIC)
        fun videoFormats() = listOf(NONE, MP4, MKV, AVI)
        fun documentFormats() = listOf(NONE, PDF)
    }
}

/**
 * File Type Converter Component.
 * Allows users to convert file formats during batch rename operations.
 */
@Composable
fun FileTypeConverter(
    selectedFormat: ConversionFormat,
    availableFormats: List<ConversionFormat>,
    onFormatSelect: (ConversionFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Transform,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "File Type Conversion",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedFormat.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Convert To") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableFormats.forEach { format ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = format.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (format.extension.isNotEmpty()) {
                                        Text(
                                            text = ".${format.extension}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onFormatSelect(format)
                                expanded = false
                            },
                            leadingIcon = if (selectedFormat == format) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Transform,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
            
            if (selectedFormat != ConversionFormat.NONE) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Files will be converted to ${selectedFormat.displayName} format during rename",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
