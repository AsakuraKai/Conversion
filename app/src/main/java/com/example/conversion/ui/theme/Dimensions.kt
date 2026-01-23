package com.example.conversion.ui.theme

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized responsive dimension system.
 * Consolidates scattered dimension tokens from MainContentArea, ContentCard, etc.
 * Makes them responsive based on WindowSizeClass.
 * 
 * MIGRATION: Replace hardcoded values with these responsive tokens:
 * - MainContentArea: CONTENT_PADDING → Dimensions.contentPadding
 * - ContentCard: CARD_PADDING → Dimensions.cardPadding
 * - All screens: Fixed padding(16.dp) → padding(Dimensions.contentPadding)
 */
object Dimensions {
    
    /**
     * Content padding for main areas
     * Replaces: MainContentArea.CONTENT_PADDING (was 24.dp fixed)
     */
    val contentPadding: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
            else -> 16.dp
        }
    
    /**
     * Card padding
     * Replaces: ContentCard.CARD_PADDING (was 16.dp fixed)
     */
    val cardPadding: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 12.dp
            WindowWidthSizeClass.Medium -> 16.dp
            WindowWidthSizeClass.Expanded -> 20.dp
            else -> 12.dp
        }
    
    /**
     * Vertical spacing between sections
     */
    val sectionSpacing: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 20.dp
            WindowWidthSizeClass.Expanded -> 24.dp
            else -> 16.dp
        }
    
    /**
     * Item spacing in lists/grids
     * Replaces: MainContentArea.GRID_ITEM_SPACING (was 16.dp fixed)
     */
    val itemSpacing: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 8.dp
            WindowWidthSizeClass.Medium -> 12.dp
            WindowWidthSizeClass.Expanded -> 16.dp
            else -> 8.dp
        }
    
    /**
     * Minimum touch target size (always 48dp per Material guidelines)
     */
    val minTouchTarget: Dp = 48.dp
    
    /**
     * Icon sizes
     */
    object Icon {
        val small: Dp = 16.dp
        val medium: Dp = 24.dp
        val large: Dp = 32.dp
        val extraLarge: Dp
            @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
                WindowWidthSizeClass.Compact -> 48.dp
                WindowWidthSizeClass.Medium -> 56.dp
                WindowWidthSizeClass.Expanded -> 64.dp
                else -> 48.dp
            }
    }
    
    /**
     * Grid configuration
     * Enhances existing ResponsiveGridLayout defaults
     */
    object Grid {
        val minItemWidthCompact: Dp = 120.dp
        val minItemWidthMedium: Dp = 150.dp
        val minItemWidthExpanded: Dp = 200.dp
        
        @Composable
        fun getMinItemWidth(): Dp = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> minItemWidthCompact
            WindowWidthSizeClass.Medium -> minItemWidthMedium
            WindowWidthSizeClass.Expanded -> minItemWidthExpanded
            else -> minItemWidthCompact
        }
    }
}
