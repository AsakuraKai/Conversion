package com.example.conversion.presentation.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.conversion.ui.theme.Dimensions
import com.example.conversion.ui.theme.LocalWindowSizeClass

/**
 * Adaptive Layouts Component
 * 
 * Provides NEW responsive layout patterns not covered by existing components:
 * - TwoPaneLayout: Master-detail pattern for tablets
 * - AdaptiveButtonGroup: Prevents button overflow on small screens
 * - ResponsiveCardGrid: Fixed-column grid alternative
 * 
 * These complement existing MainContentArea.ResponsiveGridLayout (adaptive sizing).
 */

/**
 * Two-pane layout for tablets (master-detail pattern)
 * 
 * Automatically switches between single-pane (phone) and two-pane (tablet) layouts.
 * 
 * Use for: Settings, History, Template editor
 * 
 * @param modifier Modifier for customization
 * @param showTwoPanes Override for manual control (defaults to WindowSizeClass-based)
 * @param masterPane Left pane content (list, navigation)
 * @param detailPane Right pane content (detail view)
 */
@Composable
fun TwoPaneLayout(
    modifier: Modifier = Modifier,
    showTwoPanes: Boolean = LocalWindowSizeClass.current.widthSizeClass >= WindowWidthSizeClass.Medium,
    masterPane: @Composable () -> Unit,
    detailPane: @Composable () -> Unit
) {
    if (showTwoPanes) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing)
        ) {
            Box(modifier = Modifier.weight(0.4f)) { masterPane() }
            Box(modifier = Modifier.weight(0.6f)) { detailPane() }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) { masterPane() }
    }
}

/**
 * Adaptive button group that prevents overflow on small screens
 * 
 * Automatically wraps buttons into multiple rows based on screen size.
 * Ensures buttons never overflow horizontally.
 * 
 * Use for: RenameConfigScreen helper tools, multi-button actions
 * 
 * @param modifier Modifier for customization
 * @param maxButtonsPerRow Maximum buttons per row on largest screens
 * @param buttons List of button composables
 */
@Composable
fun AdaptiveButtonGroup(
    modifier: Modifier = Modifier,
    maxButtonsPerRow: Int = 4,
    buttons: List<@Composable RowScope.() -> Unit>
) {
    val buttonsPerRow = when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> minOf(2, maxButtonsPerRow)
        WindowWidthSizeClass.Medium -> minOf(3, maxButtonsPerRow)
        WindowWidthSizeClass.Expanded -> maxButtonsPerRow
        else -> 2
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing)
    ) {
        buttons.chunked(buttonsPerRow).forEach { rowButtons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimensions.itemSpacing,
                    Alignment.CenterHorizontally
                )
            ) {
                rowButtons.forEach { button ->
                    Box(modifier = Modifier.weight(1f)) {
                        button.invoke(this@Row)
                    }
                }
            }
        }
    }
}

/**
 * Fixed-column responsive grid (alternative to adaptive)
 * 
 * Uses fixed number of columns that changes with screen size,
 * rather than adaptive column sizing.
 * 
 * Use for: Dashboard cards, feature cards with consistent sizing
 * 
 * @param modifier Modifier for customization
 * @param compactColumns Columns on compact screens (phones)
 * @param mediumColumns Columns on medium screens (tablets portrait)
 * @param expandedColumns Columns on expanded screens (tablets landscape)
 * @param content Grid content
 */
@Composable
fun ResponsiveCardGrid(
    modifier: Modifier = Modifier,
    compactColumns: Int = 1,
    mediumColumns: Int = 2,
    expandedColumns: Int = 3,
    content: LazyGridScope.() -> Unit
) {
    val columns = when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumns
        WindowWidthSizeClass.Medium -> mediumColumns
        WindowWidthSizeClass.Expanded -> expandedColumns
        else -> compactColumns
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(Dimensions.contentPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing),
        content = content
    )
}
