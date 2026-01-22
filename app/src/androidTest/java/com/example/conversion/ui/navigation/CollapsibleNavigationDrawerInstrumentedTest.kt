package com.example.conversion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.conversion.presentation.ui.navigation.CollapsibleNavigationDrawer
import com.example.conversion.presentation.ui.navigation.SidebarHeader
import com.example.conversion.presentation.ui.navigation.NavigationItem
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Instrumented tests for CollapsibleNavigationDrawer component.
 * Tests animations, transitions, and user interactions.
 */
@RunWith(AndroidJUnit4::class)
class CollapsibleNavigationDrawerInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun drawer_animatesWidthTransition() {
        var isCollapsed = false

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(
                    isCollapsed = isCollapsed,
                    onCollapseToggle = { isCollapsed = !isCollapsed }
                ) {
                    SidebarHeader(isCollapsed = isCollapsed)
                }
            }
        }

        // Initially expanded
        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer, press Escape to toggle")
            .assertIsDisplayed()

        // Toggle to collapsed (animation should occur)
        isCollapsed = true
        composeTestRule.waitForIdle()

        // Should now be collapsed
        composeTestRule
            .onNodeWithContentDescription("Collapsed navigation drawer, press Escape to toggle")
            .assertIsDisplayed()
    }

    @Test
    fun drawer_rendersMultipleNavigationItems() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = true,
                        onClick = {}
                    )
                    NavigationItem(
                        icon = Icons.Default.Folder,
                        label = "Files",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = {}
                    )
                    NavigationItem(
                        icon = Icons.Default.History,
                        label = "History",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = {}
                    )
                    NavigationItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = {}
                    )
                }
            }
        }

        // All items should be visible
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Files").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun drawer_collapsedState_hidesLabels() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = true) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = true,
                        isSelected = true,
                        onClick = {}
                    )
                    NavigationItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isCollapsed = true,
                        isSelected = false,
                        onClick = {}
                    )
                }
            }
        }

        // Labels should not be visible in collapsed state
        composeTestRule.onNodeWithText("Home").assertDoesNotExist()
        composeTestRule.onNodeWithText("Settings").assertDoesNotExist()

        // But icons should still be accessible
        composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
    }

    @Test
    fun drawer_hasCorrectSemantics() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    SidebarHeader(isCollapsed = false)
                }
            }
        }

        // Check accessibility content description
        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer, press Escape to toggle")
            .assertIsDisplayed()
    }

    @Test
    fun drawer_appliesMaterial3Theming() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = true,
                        onClick = {}
                    )
                }
            }
        }

        // Component should render without errors
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun drawer_darkTheme_rendersCorrectly() {
        composeTestRule.setContent {
            ConversionTheme(darkTheme = true) {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    SidebarHeader(isCollapsed = false)
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = true,
                        onClick = {}
                    )
                }
            }
        }

        // Content should be visible in dark theme
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
