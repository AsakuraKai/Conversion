package com.example.conversion.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.ImageMetadata
import com.example.conversion.ui.theme.ConversionTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * Preview card component that displays metadata and the resulting filename.
 * Shows actual metadata values from a sample image.
 *
 * @param metadata The sample image metadata to display
 * @param pattern The current rename pattern with variables
 * @param preview The preview filename with variables replaced
 * @param modifier Optional modifier for customization
 */
@Composable
fun MetadataPreviewCard(
    metadata: ImageMetadata,
    pattern: String,
    preview: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Live Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Divider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))

            // Pattern
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pattern:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = pattern,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            // Preview
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Preview:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = preview,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))

            // Sample Metadata Info
            Text(
                text = "Sample Metadata:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )

            // Metadata details
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                metadata.dateTaken?.let { timestamp ->
                    MetadataInfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Date Taken",
                        value = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
                            .format(Date(timestamp))
                    )
                }

                metadata.cameraModel?.let { camera ->
                    MetadataInfoRow(
                        icon = Icons.Default.CameraAlt,
                        label = "Camera",
                        value = camera
                    )
                }

                metadata.getFormattedLocation()?.let { location ->
                    MetadataInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = location
                    )
                }

                metadata.getFormattedDimensions()?.let { dimensions ->
                    MetadataInfoRow(
                        icon = Icons.Default.AspectRatio,
                        label = "Dimensions",
                        value = "$dimensions (${String.format(Locale.US, "%.1f", metadata.getMegapixels())} MP)"
                    )
                }

                metadata.iso?.let { iso ->
                    MetadataInfoRow(
                        icon = Icons.Default.Settings,
                        label = "ISO",
                        value = iso
                    )
                }

                metadata.fNumber?.let { fnumber ->
                    MetadataInfoRow(
                        icon = Icons.Default.Settings,
                        label = "Aperture",
                        value = "f/$fnumber"
                    )
                }
            }
        }
    }
}

/**
 * Compact preview card that only shows pattern and result.
 */
@Composable
fun MetadataPreviewCardCompact(
    pattern: String,
    preview: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Preview:",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = preview,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MetadataInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

// ============= Previews =============

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun MetadataPreviewCardPreview() {
    ConversionTheme {
        Surface {
            MetadataPreviewCard(
                metadata = ImageMetadata(
                    dateTaken = System.currentTimeMillis(),
                    cameraModel = "Pixel 7 Pro",
                    dimensions = 4080 to 3072,
                    latitude = 37.774929,
                    longitude = -122.419418,
                    iso = "100",
                    fNumber = "1.8"
                ),
                pattern = "IMG_{date}_{camera}_{location}",
                preview = "IMG_20231215_Pixel_7_Pro_37.774929_-122.419418",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Compact Preview", showBackground = true)
@Composable
private fun MetadataPreviewCardCompactPreview() {
    ConversionTheme {
        Surface {
            MetadataPreviewCardCompact(
                pattern = "IMG_{date}_{camera}",
                preview = "IMG_20231215_Pixel_7_Pro",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Minimal Metadata", showBackground = true)
@Composable
private fun MetadataPreviewCardMinimalPreview() {
    ConversionTheme {
        Surface {
            MetadataPreviewCard(
                metadata = ImageMetadata(
                    dateTaken = System.currentTimeMillis()
                ),
                pattern = "{date}_{time}",
                preview = "20231215_143025",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
