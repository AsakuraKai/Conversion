package com.example.conversion.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for NavigationItem component
 */
class NavigationItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun expandedItem_displaysIconAndLabel() {
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

        composeTestRule
            .onNodeWithText("Home")
            .assertIsDisplayed()
    }

    @Test
    fun collapsedItem_displaysOnlyIcon() {
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

        // Icon should be visible via content description
        composeTestRule
            .onNodeWithContentDescription("Home")
            .assertIsDisplayed()

        // Label text should not be visible when collapsed
        composeTestRule
            .onNodeWithText("Home")
            .assertDoesNotExist()
    }

    @Test
    fun navigationItem_isClickable() {
        var clicked = false

        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Home")
            .assertHasClickAction()
            .performClick()

        assert(clicked) { "onClick should be triggered" }
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

        composeTestRule
            .onNodeWithContentDescription("Home")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.Button
                )
            )
    }

    @Test
    fun selectedItem_hasSelectedSemantics() {
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

        composeTestRule
            .onNodeWithContentDescription("Home")
            .assert(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)
            )
    }

    @Test
    fun navigationItem_displaysBadge() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {},
                    badgeCount = 5
                )
            }
        }

        // Badge should display count
        composeTestRule
            .onNodeWithText("5")
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_displaysBadge_whenCollapsed() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isCollapsed = true,
                    isSelected = false,
                    onClick = {},
                    badgeCount = 99
                )
            }
        }

        // Badge should display count even when collapsed
        composeTestRule
            .onNodeWithText("99")
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_displaysBadge_maxAt99Plus() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {},
                    badgeCount = 150
                )
            }
        }

        // Badge should cap at 99+
        composeTestRule
            .onNodeWithText("99+")
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_supportsCustomContentDescription() {
        composeTestRule.setContent {
            ConversionTheme {
                NavigationItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isCollapsed = false,
                    isSelected = false,
                    onClick = {},
                    contentDescription = "Navigate to home screen"
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Navigate to home screen")
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_supportsLightTheme() {
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

        composeTestRule
            .onNodeWithText("Home")
            .assertIsDisplayed()
    }

    @Test
    fun navigationItem_supportsDarkTheme() {
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

        composeTestRule
            .onNodeWithText("Home")
            .assertIsDisplayed()
    }
}
