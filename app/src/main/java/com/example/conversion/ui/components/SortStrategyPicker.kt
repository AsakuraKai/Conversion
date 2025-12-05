package com.example.conversion.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Reusable Sort Strategy Picker component.
 * Displays all available sort strategies with radio buttons and descriptions.
 *
 * @param selectedStrategy The currently selected sort strategy
 * @param onStrategySelected Callback when a strategy is selected
 * @param modifier Optional modifier for the component
 * @param title Optional title text for the picker section
 * @param showCard Whether to wrap the options in a Card
 */
@Composable
fun SortStrategyPicker(
    selectedStrategy: SortStrategy,
    onStrategySelected: (SortStrategy) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = "Sort Order",
    showCard: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Optional title
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Strategy options
        val content: @Composable () -> Unit = {
            Column(
                modifier = Modifier.padding(if (showCard) 8.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SortStrategy.entries.forEach { strategy ->
                    SortStrategyOption(
                        strategy = strategy,
                        isSelected = selectedStrategy == strategy,
                        onClick = { onStrategySelected(strategy) }
                    )
                }
            }
        }

        if (showCard) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

/**
 * Individual sort strategy option with radio button.
 *
 * @param strategy The sort strategy this option represents
 * @param isSelected Whether this option is currently selected
 * @param onClick Callback when this option is clicked
 */
@Composable
private fun SortStrategyOption(
    strategy: SortStrategy,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) {
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
                selected = isSelected,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strategy.toDisplayName(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = strategy.toDescription(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

/**
 * Converts SortStrategy enum to human-readable display name.
 */
private fun SortStrategy.toDisplayName(): String {
    return when (this) {
        SortStrategy.NATURAL -> "Natural (IMG_1, IMG_2, IMG_10)"
        SortStrategy.DATE_MODIFIED -> "Date Modified"
        SortStrategy.SIZE -> "File Size"
        SortStrategy.ORIGINAL_ORDER -> "Original Selection Order"
    }
}

/**
 * Converts SortStrategy enum to detailed description.
 */
private fun SortStrategy.toDescription(): String {
    return when (this) {
        SortStrategy.NATURAL -> "Smart number sorting"
        SortStrategy.DATE_MODIFIED -> "Newest to oldest"
        SortStrategy.SIZE -> "Largest to smallest"
        SortStrategy.ORIGINAL_ORDER -> "Keep selection order"
    }
}

// ============== Preview Functions ==============

@Preview(name = "Light Mode - With Card", showBackground = true)
@Preview(name = "Dark Mode - With Card", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SortStrategyPickerPreview() {
    ConversionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SortStrategyPicker(
                selectedStrategy = SortStrategy.NATURAL,
                onStrategySelected = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Without Card", showBackground = true)
@Composable
private fun SortStrategyPickerNoCardPreview() {
    ConversionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SortStrategyPicker(
                selectedStrategy = SortStrategy.DATE_MODIFIED,
                onStrategySelected = {},
                modifier = Modifier.padding(16.dp),
                showCard = false
            )
        }
    }
}

@Preview(name = "Without Title", showBackground = true)
@Composable
private fun SortStrategyPickerNoTitlePreview() {
    ConversionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SortStrategyPicker(
                selectedStrategy = SortStrategy.SIZE,
                onStrategySelected = {},
                modifier = Modifier.padding(16.dp),
                title = null
            )
        }
    }
}

@Preview(name = "Different Selections", showBackground = true, widthDp = 360)
@Composable
private fun SortStrategyPickerVariationsPreview() {
    ConversionTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {},
                    title = "Natural Sort Selected"
                )

                SortStrategyPicker(
                    selectedStrategy = SortStrategy.ORIGINAL_ORDER,
                    onStrategySelected = {},
                    title = "Original Order Selected"
                )
            }
        }
    }
}
