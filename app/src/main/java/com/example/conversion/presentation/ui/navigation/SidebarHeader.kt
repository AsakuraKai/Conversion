package com.example.conversion.presentation.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import com.example.conversion.ui.theme.ConversionTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Sidebar Header Component
 * 
 * Displays current date information that adapts based on sidebar state:
 * - Expanded: Full date display (e.g., "January 22, 2026")
 * - Collapsed: Day number only (e.g., "22")
 * 
 * @param isCollapsed Whether the sidebar is collapsed
 * @param currentDate The date to display (defaults to today)
 * @param modifier Modifier for customization
 */
@Composable
fun SidebarHeader(
    isCollapsed: Boolean,
    modifier: Modifier = Modifier,
    currentDate: LocalDate = LocalDate.now()
) {
    val dayOfMonth = currentDate.dayOfMonth.toString()
    val monthYear = currentDate.format(
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    )
    val fullDate = currentDate.format(
        DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())
    )

    // Animate scale for smooth transitions
    val scale by animateFloatAsState(
        targetValue = if (isCollapsed) 0.9f else 1f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MS,
            easing = EaseInOutCubic
        ),
        label = "header_scale_animation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(HEADER_PADDING)
            .scale(scale)
            .semantics(mergeDescendants = true) {
                heading()
                liveRegion = LiveRegionMode.Polite
                contentDescription = "Navigation header, Current date: $fullDate"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isCollapsed) {
            // Collapsed state: Show only day number
            Text(
                text = dayOfMonth,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        } else {
            // Expanded state: Show full date
            AnimatedVisibility(
                visible = !isCollapsed,
                enter = fadeIn(animationSpec = tween(
                    durationMillis = ANIMATION_DURATION_MS,
                    easing = EaseInOutCubic
                )),
                exit = fadeOut(animationSpec = tween(
                    durationMillis = ANIMATION_DURATION_MS,
                    easing = EaseInOutCubic
                ))
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = dayOfMonth,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = monthYear,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = currentDate.dayOfWeek.toString().lowercase()
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Design tokens
private val HEADER_PADDING = 16.dp
private const val ANIMATION_DURATION_MS = 300

// EaseInOutCubic easing function for smooth animations
private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1.0f)

// Preview compositions
@Preview(name = "Expanded Header - Light", showBackground = true)
@Composable
private fun SidebarHeaderExpandedPreview() {
    ConversionTheme {
        SidebarHeader(
            isCollapsed = false,
            currentDate = LocalDate.of(2026, 1, 22)
        )
    }
}

@Preview(name = "Collapsed Header - Light", showBackground = true)
@Composable
private fun SidebarHeaderCollapsedPreview() {
    ConversionTheme {
        SidebarHeader(
            isCollapsed = true,
            currentDate = LocalDate.of(2026, 1, 22)
        )
    }
}

@Preview(
    name = "Expanded Header - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SidebarHeaderExpandedDarkPreview() {
    ConversionTheme {
        SidebarHeader(
            isCollapsed = false,
            currentDate = LocalDate.of(2026, 1, 22)
        )
    }
}

@Preview(
    name = "Collapsed Header - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SidebarHeaderCollapsedDarkPreview() {
    ConversionTheme {
        SidebarHeader(
            isCollapsed = true,
            currentDate = LocalDate.of(2026, 1, 22)
        )
    }
}
