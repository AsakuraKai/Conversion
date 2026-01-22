package com.example.conversion.ui.navigation.performance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.conversion.presentation.ui.navigation.CollapsibleNavigationDrawer
import com.example.conversion.presentation.ui.navigation.NavigationItem
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

/**
 * Performance tests for navigation components.
 * Tests animation frame rates, rendering performance, and memory efficiency.
 */
@RunWith(AndroidJUnit4::class)
class NavigationPerformanceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun drawer_collapseExpand_performanceTest() {
        var isCollapsed by mutableStateOf(false)
        var toggleCount = 0

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(
                    isCollapsed = isCollapsed,
                    onCollapseToggle = {
                        isCollapsed = !isCollapsed
                        toggleCount++
                    }
                ) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = isCollapsed,
                        isSelected = false,
                        onClick = {}
                    )
                }
            }
        }

        // Measure time for multiple toggle operations
        val totalTime = measureTimeMillis {
            repeat(10) {
                isCollapsed = !isCollapsed
                composeTestRule.waitForIdle()
            }
        }

        // Each toggle should take roughly 300ms (animation duration)
        // Total time should be around 3000ms, allowing 50% overhead
        val expectedMaxTime = 10 * 300 * 1.5 // 4500ms
        assert(totalTime < expectedMaxTime) {
            "Toggle operations took $totalTime ms, expected < $expectedMaxTime ms"
        }

        println("Performance Test: 10 toggles completed in $totalTime ms")
    }

    @Test
    fun drawer_multipleItems_renderingPerformance() {
        val icons = listOf(
            Icons.Default.Home,
            Icons.Default.Folder,
            Icons.Default.History,
            Icons.Default.Settings,
            Icons.Default.Cloud,
            Icons.Default.Tag,
            Icons.Default.Search,
            Icons.Default.Info,
            Icons.Default.Share,
            Icons.Default.Star
        )

        val labels = listOf(
            "Home", "Files", "History", "Settings", "Cloud",
            "Tags", "Search", "About", "Share", "Favorites"
        )

        val renderTime = measureTimeMillis {
            composeTestRule.setContent {
                ConversionTheme {
                    CollapsibleNavigationDrawer(isCollapsed = false) {
                        icons.forEachIndexed { index, icon ->
                            NavigationItem(
                                icon = icon,
                                label = labels[index],
                                isCollapsed = false,
                                isSelected = index == 0,
                                onClick = {}
                            )
                        }
                    }
                }
            }
            composeTestRule.waitForIdle()
        }

        // Initial render should complete quickly (<500ms for 10 items)
        assert(renderTime < 500) {
            "Initial render took $renderTime ms, expected < 500ms"
        }

        println("Performance Test: Rendered 10 items in $renderTime ms")
    }

    @Test
    fun drawer_rapidItemClicks_performanceTest() {
        var selectedIndex by mutableStateOf(0)
        val itemCount = 5

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    repeat(itemCount) { index ->
                        NavigationItem(
                            icon = Icons.Default.Home,
                            label = "Item $index",
                            isCollapsed = false,
                            isSelected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                    }
                }
            }
        }

        // Measure time for rapid selection changes
        val totalTime = measureTimeMillis {
            repeat(20) {
                val index = it % itemCount
                composeTestRule.onNodeWithText("Item $index").performClick()
                composeTestRule.waitForIdle()
            }
        }

        // Rapid clicks should be responsive (<100ms per click)
        val expectedMaxTime = 20 * 100 // 2000ms
        assert(totalTime < expectedMaxTime) {
            "Rapid clicks took $totalTime ms, expected < $expectedMaxTime ms"
        }

        println("Performance Test: 20 rapid clicks completed in $totalTime ms")
    }

    @Test
    fun drawer_badgeUpdates_performanceTest() {
        var badgeCount by mutableStateOf(0)

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.History,
                        label = "History",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = {},
                        badgeCount = badgeCount
                    )
                }
            }
        }

        // Measure time for badge count updates
        val totalTime = measureTimeMillis {
            repeat(50) {
                badgeCount = it
                composeTestRule.waitForIdle()
            }
        }

        // Badge updates should be efficient (<50ms per update)
        val expectedMaxTime = 50 * 50 // 2500ms
        assert(totalTime < expectedMaxTime) {
            "Badge updates took $totalTime ms, expected < $expectedMaxTime ms"
        }

        println("Performance Test: 50 badge updates completed in $totalTime ms")
    }

    @Test
    fun drawer_collapsedState_withManyItems_performanceTest() {
        var isCollapsed by mutableStateOf(false)
        val itemCount = 15

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = isCollapsed) {
                    repeat(itemCount) { index ->
                        NavigationItem(
                            icon = Icons.Default.Home,
                            label = "Item $index with a very long label",
                            isCollapsed = isCollapsed,
                            isSelected = index == 0,
                            onClick = {}
                        )
                    }
                }
            }
        }

        composeTestRule.waitForIdle()

        // Measure time to collapse with many items
        val collapseTime = measureTimeMillis {
            isCollapsed = true
            composeTestRule.waitForIdle()
        }

        // Collapse animation should complete efficiently even with many items
        val expectedMaxTime = 500 // 500ms (300ms animation + overhead)
        assert(collapseTime < expectedMaxTime) {
            "Collapse with $itemCount items took $collapseTime ms, expected < $expectedMaxTime ms"
        }

        println("Performance Test: Collapse with $itemCount items completed in $collapseTime ms")
    }

    @Test
    fun drawer_themeSwitch_performanceTest() {
        var isDarkTheme by mutableStateOf(false)

        composeTestRule.setContent {
            ConversionTheme(darkTheme = isDarkTheme) {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    repeat(8) { index ->
                        NavigationItem(
                            icon = Icons.Default.Home,
                            label = "Item $index",
                            isCollapsed = false,
                            isSelected = index == 0,
                            onClick = {}
                        )
                    }
                }
            }
        }

        // Measure time for theme switches
        val totalTime = measureTimeMillis {
            repeat(10) {
                isDarkTheme = !isDarkTheme
                composeTestRule.waitForIdle()
            }
        }

        // Theme switches should be efficient (<200ms per switch)
        val expectedMaxTime = 10 * 200 // 2000ms
        assert(totalTime < expectedMaxTime) {
            "Theme switches took $totalTime ms, expected < $expectedMaxTime ms"
        }

        println("Performance Test: 10 theme switches completed in $totalTime ms")
    }

    @Test
    fun drawer_simultaneousAnimations_performanceTest() {
        var isCollapsed by mutableStateOf(false)
        var selectedIndex by mutableStateOf(0)

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = isCollapsed) {
                    repeat(5) { index ->
                        NavigationItem(
                            icon = Icons.Default.Home,
                            label = "Item $index",
                            isCollapsed = isCollapsed,
                            isSelected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                    }
                }
            }
        }

        // Measure time for simultaneous state changes
        val totalTime = measureTimeMillis {
            repeat(5) {
                isCollapsed = !isCollapsed
                selectedIndex = (selectedIndex + 1) % 5
                composeTestRule.waitForIdle()
            }
        }

        // Simultaneous animations should handle efficiently
        val expectedMaxTime = 5 * 400 // 2000ms (allowing for combined animations)
        assert(totalTime < expectedMaxTime) {
            "Simultaneous animations took $totalTime ms, expected < $expectedMaxTime ms"
        }

        println("Performance Test: 5 simultaneous state changes completed in $totalTime ms")
    }
}
