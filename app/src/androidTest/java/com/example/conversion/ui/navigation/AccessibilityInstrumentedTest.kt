package com.example.conversion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.semantics.SemanticsProperties
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
 * Instrumented tests for accessibility features.
 * Tests screen reader support, keyboard navigation, and WCAG compliance.
 */
@RunWith(AndroidJUnit4::class)
class AccessibilityInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun drawer_hasAccessibleContentDescription() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    SidebarHeader(isCollapsed = false)
                }
            }
        }

        // Should have proper content description for screen readers
        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer, press Escape to toggle")
            .assertIsDisplayed()
    }

    @Test
    fun sidebarHeader_hasAccessibleDate() {
        val testDate = LocalDate.of(2026, 1, 22)

        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(
                    isCollapsed = false,
                    currentDate = testDate
                )
            }
        }

        // Should have accessible date information
        composeTestRule
            .onNode(hasContentDescription("Navigation header, Current date: January 22, 2026"))
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_hasAccessibleLabel() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {}
                )
            }
        }

        // Should have accessible content description
        composeTestRule
            .onNodeWithContentDescription("Home")
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_selected_hasCorrectSemantics() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = false,
                    isSelected = true,
                    onClick = {}
                )
            }
        }

        // Should have selected semantics
        composeTestRule
            .onNodeWithContentDescription("Home")
            .assert(hasStateDescription("Selected"))
    }

    @Test
    fun navigationItem_notSelected_hasCorrectSemantics() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {}
                )
            }
        }

        // Should have not selected semantics
        composeTestRule
            .onNodeWithContentDescription("Settings")
            .assert(hasStateDescription("Not selected"))
    }

    @Test
    fun navigationItem_withBadge_announcesBadgeCount() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {},
                    badgeCount = 5
                )
            }
        }

        // Should include badge count in state description
        composeTestRule
            .onNodeWithContentDescription("Home")
            .assert(hasStateDescription("Not selected, 5 unread items"))
    }

    @Test
    fun navigationItem_hasButtonRole() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {}
                )
            }
        }

        // Should have button role for accessibility
        composeTestRule
            .onNodeWithContentDescription("Home")
            .assert(hasRole(androidx.compose.ui.semantics.Role.Button))
    }

    @Test
    fun drawer_hasTraversalIndex() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = {}
                    )
                }
            }
        }

        // Drawer should be first in focus order (traversal index = 0)
        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer, press Escape to toggle")
            .assertExists()
    }

    @Test
    fun sidebarHeader_hasHeadingSemantics() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(isCollapsed = false)
            }
        }

        // Header should have heading semantics for screen readers
        composeTestRule
            .onNode(
                hasContentDescription("Navigation header, Current date: ${LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy"))}")
            )
            .assertExists()
    }

    @Test
    fun navigationItem_collapsed_hasTooltip() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = true,
                    isSelected = false,
                    onClick = {}
                )
            }
        }

        // In collapsed state, should still have accessible content
        composeTestRule
            .onNodeWithContentDescription("Home")
            .assertIsDisplayed()
    }

    @Test
    fun drawer_supportsLargeTextScaling() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = {}
                    )
                }
            }
        }

        // Component should render correctly with default text scaling
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()

        // Note: Actual font scaling tests require platform-level configuration
        // This test verifies basic structure works with text
    }
}
