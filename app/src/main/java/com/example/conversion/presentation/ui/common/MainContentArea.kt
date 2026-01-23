package com.example.conversion.presentation.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme
import com.example.conversion.ui.theme.Dimensions

/**
 * Main Content Area Component
 * 
 * A container for the main content area with responsive layout support.
 * Provides consistent padding and background styling.
 * 
 * @param modifier Modifier for customization
 * @param contentDescription Optional content description for accessibility
 * @param content The content to display
 */
@Composable
fun MainContentArea(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription?.let {
                    this.contentDescription = it
                }
            },
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Responsive Grid Layout Component
 * 
 * Displays content in a responsive grid that adapts to screen size.
 * Uses adaptive grid cells for optimal layout across devices.
 * 
 * @param modifier Modifier for customization
 * @param minItemWidth Minimum width for each grid item
 * @param contentPadding Padding around the grid content
 * @param verticalSpacing Vertical spacing between items
 * @param horizontalSpacing Horizontal spacing between items
 * @param content The grid content
 */
@Composable
fun ResponsiveGridLayout(
    modifier: Modifier = Modifier,
    minItemWidth: androidx.compose.ui.unit.Dp = Dimensions.Grid.getMinItemWidth(),
    contentPadding: PaddingValues = PaddingValues(Dimensions.contentPadding),
    verticalSpacing: androidx.compose.ui.unit.Dp = Dimensions.itemSpacing,
    horizontalSpacing: androidx.compose.ui.unit.Dp = Dimensions.itemSpacing,
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minItemWidth),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        content = content
    )
}

/**
 * Content Column Layout Component
 * 
 * A simple vertical scrolling layout for content cards.
 * 
 * @param modifier Modifier for customization
 * @param contentPadding Padding around the content
 * @param verticalSpacing Spacing between items
 * @param content The content to display
 */
@Composable
fun ContentColumnLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimensions.contentPadding),
    verticalSpacing: androidx.compose.ui.unit.Dp = Dimensions.itemSpacing,
    content: LazyItemScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        item { content() }
    }
}

// Preview compositions
@Preview(name = "Main Content Area - Light", showBackground = true)
@Composable
private fun MainContentAreaPreview() {
    ConversionTheme {
        MainContentArea(
            contentDescription = "Main content area"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContentCard(title = "Card 1") {
                    Text("Content for card 1")
                }
                ContentCard(title = "Card 2") {
                    Text("Content for card 2")
                }
                ContentCard(title = "Card 3") {
                    Text("Content for card 3")
                }
            }
        }
    }
}

@Preview(
    name = "Main Content Area - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MainContentAreaDarkPreview() {
    ConversionTheme {
        MainContentArea(
            contentDescription = "Main content area"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContentCard(title = "Card 1") {
                    Text("Content for card 1")
                }
                ContentCard(title = "Card 2") {
                    Text("Content for card 2")
                }
            }
        }
    }
}

@Preview(name = "Responsive Grid - Light", showBackground = true, widthDp = 800)
@Composable
private fun ResponsiveGridLayoutPreview() {
    ConversionTheme {
        MainContentArea {
            ResponsiveGridLayout {
                items(6) { index ->
                    ContentCard(title = "Grid Item ${index + 1}") {
                        Text("Content for grid item ${index + 1}")
                    }
                }
            }
        }
    }
}
