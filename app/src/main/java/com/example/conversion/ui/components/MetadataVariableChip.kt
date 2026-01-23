package com.example.conversion.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.MetadataVariable
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Chip component for metadata variable selection.
 * Displays variable name, description, and example value.
 *
 * @param variable The metadata variable to display
 * @param onClick Callback when the chip is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun MetadataVariableChip(
    variable: MetadataVariable,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = variable.variable,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "e.g., ${variable.example}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        icon = {
            Icon(
                imageVector = getIconForVariable(variable),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

/**
 * Compact version of MetadataVariableChip for smaller displays.
 */
@Composable
fun MetadataVariableChipCompact(
    variable: MetadataVariable,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = variable.variable,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = getIconForVariable(variable),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier
    )
}

/**
 * Returns an appropriate icon for each metadata variable type.
 */
private fun getIconForVariable(variable: MetadataVariable): ImageVector {
    return when (variable) {
        MetadataVariable.DATE,
        MetadataVariable.YEAR,
        MetadataVariable.MONTH,
        MetadataVariable.DAY -> Icons.Default.CalendarToday

        MetadataVariable.TIME -> Icons.Default.Schedule

        MetadataVariable.LATITUDE,
        MetadataVariable.LONGITUDE,
        MetadataVariable.LOCATION -> Icons.Default.LocationOn

        MetadataVariable.CAMERA -> Icons.Default.CameraAlt

        MetadataVariable.FNUMBER,
        MetadataVariable.EXPOSURE,
        MetadataVariable.ISO,
        MetadataVariable.FOCAL_LENGTH -> Icons.Default.Settings

        MetadataVariable.WIDTH,
        MetadataVariable.HEIGHT,
        MetadataVariable.MEGAPIXELS -> Icons.Default.AspectRatio

        MetadataVariable.ORIENTATION -> Icons.Default.ScreenRotation
    }
}

// ============= Previews =============

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun MetadataVariableChipPreview() {
    ConversionTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetadataVariableChip(
                    variable = MetadataVariable.DATE,
                    onClick = {}
                )
                MetadataVariableChip(
                    variable = MetadataVariable.CAMERA,
                    onClick = {}
                )
                MetadataVariableChip(
                    variable = MetadataVariable.LOCATION,
                    onClick = {}
                )
            }
        }
    }
}

@Preview(name = "Compact Chips", showBackground = true)
@Composable
private fun MetadataVariableChipCompactPreview() {
    ConversionTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetadataVariableChipCompact(
                    variable = MetadataVariable.DATE,
                    onClick = {}
                )
                MetadataVariableChipCompact(
                    variable = MetadataVariable.CAMERA,
                    onClick = {}
                )
                MetadataVariableChipCompact(
                    variable = MetadataVariable.LOCATION,
                    onClick = {}
                )
            }
        }
    }
}
