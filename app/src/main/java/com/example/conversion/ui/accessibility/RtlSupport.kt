package com.example.conversion.ui.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * RTL (Right-to-Left) layout support utilities.
 *
 * Provides utilities for handling RTL languages like Arabic, Hebrew, etc.
 * Ensures proper layout mirroring and directional padding.
 *
 * Features:
 * - Layout direction detection
 * - Directional padding (start/end instead of left/right)
 * - Arrangement helpers for RTL-aware layouts
 * - Icon mirroring utilities
 */
object RtlSupport {
    
    /**
     * Check if current layout direction is RTL.
     */
    @Composable
    fun isRtl(): Boolean {
        return LocalLayoutDirection.current == LayoutDirection.Rtl
    }
    
    /**
     * Get layout-aware horizontal arrangement.
     * Use SpaceBetween for consistent RTL behavior.
     */
    fun horizontalArrangement(
        isRtl: Boolean = false
    ): Arrangement.Horizontal {
        return Arrangement.SpaceBetween
    }
    
    /**
     * Create RTL-aware padding values.
     * Uses start/end instead of left/right.
     *
     * @param start Padding at the start (left in LTR, right in RTL)
     * @param top Top padding
     * @param end Padding at the end (right in LTR, left in RTL)
     * @param bottom Bottom padding
     */
    fun paddingValues(
        start: Dp = 0.dp,
        top: Dp = 0.dp,
        end: Dp = 0.dp,
        bottom: Dp = 0.dp
    ): PaddingValues {
        return PaddingValues(
            start = start,
            top = top,
            end = end,
            bottom = bottom
        )
    }
    
    /**
     * Create symmetric horizontal padding that respects RTL.
     */
    fun horizontalPadding(horizontal: Dp): PaddingValues {
        return PaddingValues(horizontal = horizontal)
    }
    
    /**
     * Icons that should be mirrored in RTL layouts.
     * Common icons that have directional meaning.
     */
    object MirroredIcons {
        const val ARROW_BACK = "arrow_back"
        const val ARROW_FORWARD = "arrow_forward"
        const val CHEVRON_LEFT = "chevron_left"
        const val CHEVRON_RIGHT = "chevron_right"
        const val NAVIGATE_NEXT = "navigate_next"
        const val NAVIGATE_BEFORE = "navigate_before"
    }
    
    /**
     * Icons that should NOT be mirrored in RTL layouts.
     * Common icons that are universal.
     */
    object NonMirroredIcons {
        const val CLOSE = "close"
        const val ADD = "add"
        const val REMOVE = "remove"
        const val SETTINGS = "settings"
        const val SEARCH = "search"
        const val MORE_VERT = "more_vert"
        const val DELETE = "delete"
        const val EDIT = "edit"
        const val CHECK = "check"
    }
}

/**
 * RTL layout best practices for Compose.
 *
 * Guidelines:
 * 1. Always use Modifier.fillMaxWidth() for full-width elements
 * 2. Use Arrangement.SpaceBetween in Row for consistent RTL behavior
 * 3. Use start/end instead of left/right in padding
 * 4. Test with Arabic locale (Settings → System → Languages → Add language → العربية)
 * 5. Use CompositionLocalProvider to force RTL in previews:
 *    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) { ... }
 *
 * Example RTL-aware Row:
 * ```
 * Row(
 *     modifier = Modifier.fillMaxWidth(),
 *     horizontalArrangement = Arrangement.SpaceBetween
 * ) {
 *     Text("Start") // Left in LTR, Right in RTL
 *     Text("End")   // Right in LTR, Left in RTL
 * }
 * ```
 *
 * Example RTL-aware padding:
 * ```
 * Modifier.padding(
 *     start = 16.dp,  // Left in LTR, Right in RTL
 *     end = 8.dp      // Right in LTR, Left in RTL
 * )
 * ```
 */
