package com.example.conversion.ui.components

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.conversion.domain.model.FileItem
import com.example.conversion.ui.theme.ConversionTheme

/**
 * File grid item component for displaying media files in a grid.
 * Shows thumbnail, selection state, and basic file info.
 *
 * @param file The file item to display
 * @param isSelected Whether the file is currently selected
 * @param onClick Callback when the item is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun FileGridItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Thumbnail or placeholder
            if (file.thumbnailUri != null) {
                AsyncImage(
                    model = file.thumbnailUri,
                    contentDescription = file.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            } else {
                // Fallback icon when no thumbnail is available
                FilePlaceholder(
                    fileType = when {
                        file.isImage -> FileType.IMAGE
                        file.isVideo -> FileType.VIDEO
                        file.isAudio -> FileType.AUDIO
                        else -> FileType.OTHER
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Selection overlay
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                )
            }

            // Selection indicator
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(4.dp)
                )
            }

            // File name at bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Placeholder for files without thumbnails.
 */
@Composable
private fun FilePlaceholder(
    fileType: FileType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = fileType.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * File type enumeration for placeholders.
 */
private enum class FileType(val icon: ImageVector) {
    IMAGE(Icons.Default.Image),
    VIDEO(Icons.Default.VideoLibrary),
    AUDIO(Icons.Default.AudioFile),
    OTHER(Icons.Default.Image)
}

// Preview functions
@Preview(name = "Selected File", showBackground = true)
@Preview(name = "Selected File Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FileGridItemSelectedPreview() {
    ConversionTheme {
        Surface {
            FileGridItem(
                file = FileItem(
                    id = 1L,
                    uri = Uri.parse("content://media/external/images/1"),
                    name = "IMG_001.jpg",
                    path = "/storage/emulated/0/Pictures/IMG_001.jpg",
                    size = 1024000L,
                    mimeType = "image/jpeg",
                    dateModified = System.currentTimeMillis(),
                    thumbnailUri = null
                ),
                isSelected = true,
                onClick = {},
                modifier = Modifier
                    .width(120.dp)
                    .padding(8.dp)
            )
        }
    }
}

@Preview(name = "Unselected File", showBackground = true)
@Preview(name = "Unselected File Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FileGridItemUnselectedPreview() {
    ConversionTheme {
        Surface {
            FileGridItem(
                file = FileItem(
                    id = 2L,
                    uri = Uri.parse("content://media/external/videos/2"),
                    name = "VID_002_Long_Filename_Example.mp4",
                    path = "/storage/emulated/0/DCIM/Camera/VID_002.mp4",
                    size = 5048000L,
                    mimeType = "video/mp4",
                    dateModified = System.currentTimeMillis(),
                    thumbnailUri = null
                ),
                isSelected = false,
                onClick = {},
                modifier = Modifier
                    .width(120.dp)
                    .padding(8.dp)
            )
        }
    }
}
