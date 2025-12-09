package com.example.conversion.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.FileTag
import com.example.conversion.ui.theme.ConversionTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment

/**
 * Filter chips component for tag selection.
 * Displays tags as selectable chips with color indicators.
 *
 * @param tags List of available tags
 * @param selectedTags Set of currently selected tag IDs
 * @param onTagClick Callback when a tag chip is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun TagFilterChips(
    tags: List<FileTag>,
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) {
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filter by Tags",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Flow layout for chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                TagFilterChip(
                    tag = tag,
                    isSelected = tag.id in selectedTags,
                    onClick = { onTagClick(tag.id) }
                )
            }
        }
    }
}

/**
 * Individual tag filter chip.
 *
 * @param tag The tag to display
 * @param isSelected Whether the tag is currently selected
 * @param onClick Callback when the chip is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun TagFilterChip(
    tag: FileTag,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = tag.name,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(tag.color)),
                        shape = CircleShape
                    )
            )
        },
        trailingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        modifier = modifier
    )
}

/**
 * Compact version of tag filter chips for smaller displays.
 *
 * @param tags List of available tags
 * @param selectedTags Set of currently selected tag IDs
 * @param onTagClick Callback when a tag chip is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun TagFilterChipsCompact(
    tags: List<FileTag>,
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) {
        return
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tags.forEach { tag ->
            FilterChip(
                selected = tag.id in selectedTags,
                onClick = { onTagClick(tag.id) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color(android.graphics.Color.parseColor(tag.color)),
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

/**
 * Tag selection summary showing count of selected tags.
 *
 * @param selectedCount Number of selected tags
 * @param totalCount Total number of tags
 * @param onClearAll Callback to clear all selections
 * @param modifier Optional modifier for customization
 */
@Composable
fun TagSelectionSummary(
    selectedCount: Int,
    totalCount: Int,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedCount == 0) {
        return
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedCount of $totalCount tags selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            TextButton(onClick = onClearAll) {
                Text("Clear All")
            }
        }
    }
}

// Previews
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TagFilterChipsPreview() {
    ConversionTheme {
        Surface {
            TagFilterChips(
                tags = listOf(
                    FileTag("1", "Work", "#2196F3", System.currentTimeMillis()),
                    FileTag("2", "Personal", "#4CAF50", System.currentTimeMillis()),
                    FileTag("3", "Important", "#F44336", System.currentTimeMillis()),
                    FileTag("4", "Photos", "#FF9800", System.currentTimeMillis()),
                    FileTag("5", "Documents", "#9C27B0", System.currentTimeMillis())
                ),
                selectedTags = setOf("1", "3"),
                onTagClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Compact Version")
@Composable
private fun TagFilterChipsCompactPreview() {
    ConversionTheme {
        Surface {
            TagFilterChipsCompact(
                tags = listOf(
                    FileTag("1", "Work", "#2196F3", System.currentTimeMillis()),
                    FileTag("2", "Personal", "#4CAF50", System.currentTimeMillis()),
                    FileTag("3", "Important", "#F44336", System.currentTimeMillis())
                ),
                selectedTags = setOf("2"),
                onTagClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Selection Summary")
@Composable
private fun TagSelectionSummaryPreview() {
    ConversionTheme {
        Surface {
            TagSelectionSummary(
                selectedCount = 3,
                totalCount = 5,
                onClearAll = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Empty State")
@Composable
private fun TagFilterChipsEmptyPreview() {
    ConversionTheme {
        Surface {
            TagFilterChips(
                tags = emptyList(),
                selectedTags = emptySet(),
                onTagClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
