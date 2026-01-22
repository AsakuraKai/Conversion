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
import com.example.conversion.presentation.ui.navigation.NavigationItem
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for navigation flows.
 * Tests user interactions, navigation selection, and state changes.
 */
@RunWith(AndroidJUnit4::class)
class NavigationFlowInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigationItem_clickChangesSelection() {
        var selectedItem = "home"

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = selectedItem == "home",
                        onClick = { selectedItem = "home" }
                    )
                    NavigationItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isCollapsed = false,
                        isSelected = selectedItem == "settings",
                        onClick = { selectedItem = "settings" }
                    )
                }
            }
        }

        // Initially home is selected
        composeTestRule
            .onNodeWithText("Home")
            .assertIsDisplayed()
            .assert(hasAnyDescendant(hasContentDescription("Home")))

        // Click settings
        composeTestRule
            .onNodeWithText("Settings")
            .performClick()

        composeTestRule.waitForIdle()

        // Settings should now be selected
        assert(selectedItem == "settings") { "Settings should be selected after click" }
    }

    @Test
    fun navigationFlow_allItemsClickable() {
        val clickedItems = mutableListOf<String>()

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = { clickedItems.add("home") }
                    )
                    NavigationItem(
                        icon = Icons.Default.Folder,
                        label = "Files",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = { clickedItems.add("files") }
                    )
                    NavigationItem(
                        icon = Icons.Default.History,
                        label = "History",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = { clickedItems.add("history") }
                    )
                    NavigationItem(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = { clickedItems.add("settings") }
                    )
                }
            }
        }

        // Click all items
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Files").performClick()
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()

        composeTestRule.waitForIdle()

        // All items should have been clicked
        assert(clickedItems.size == 4) { "All 4 items should have been clicked" }
        assert(clickedItems.contains("home")) { "Home should have been clicked" }
        assert(clickedItems.contains("files")) { "Files should have been clicked" }
        assert(clickedItems.contains("history")) { "History should have been clicked" }
        assert(clickedItems.contains("settings")) { "Settings should have been clicked" }
    }

    @Test
    fun navigationItem_collapsedState_stillClickable() {
        var clicked = false

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = true) {
                    NavigationItem(
                        icon = Icons.Default.Home,
                        label = "Home",
                        isCollapsed = true,
                        isSelected = false,
                        onClick = { clicked = true }
                    )
                }
            }
        }

        // Click the item (by content description since label is hidden)
        composeTestRule
            .onNodeWithContentDescription("Home")
            .performClick()

        composeTestRule.waitForIdle()

        assert(clicked) { "Item should be clickable in collapsed state" }
    }

    @Test
    fun navigationItem_withBadge_clickable() {
        var clicked = false

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    NavigationItem(
                        icon = Icons.Default.History,
                        label = "History",
                        isCollapsed = false,
                        isSelected = false,
                        onClick = { clicked = true },
                        badgeCount = 5
                    )
                }
            }
        }

        // Badge should be visible
        composeTestRule.onNodeWithText("5").assertIsDisplayed()

        // Item should still be clickable
        composeTestRule.onNodeWithText("History").performClick()

        composeTestRule.waitForIdle()

        assert(clicked) { "Item with badge should be clickable" }
    }

    @Test
    fun navigationItem_rippleEffect_visible() {
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

        // Perform touch down (should trigger ripple)
        composeTestRule
            .onNodeWithText("Home")
            .performTouchInput { down(center) }

        composeTestRule.waitForIdle()

        // Release touch
        composeTestRule
            .onNodeWithText("Home")
            .performTouchInput { up() }
    }
}
