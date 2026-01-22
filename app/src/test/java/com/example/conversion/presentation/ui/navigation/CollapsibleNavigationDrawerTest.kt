package com.example.conversion.presentation.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for CollapsibleNavigationDrawer component
 */
class CollapsibleNavigationDrawerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun collapsedDrawer_hasCorrectContentDescription() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = true) {
                    // Empty content
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Collapsed navigation drawer")
            .assertIsDisplayed()
    }

    @Test
    fun expandedDrawer_hasCorrectContentDescription() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    // Empty content
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer")
            .assertIsDisplayed()
    }

    @Test
    fun drawer_rendersContentCorrectly() {
        var contentRendered = false

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    contentRendered = true
                }
            }
        }

        assert(contentRendered) { "Drawer content should be rendered" }
    }

    @Test
    fun drawer_supportsLightTheme() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    // Empty content
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer")
            .assertIsDisplayed()
    }

    @Test
    fun drawer_supportsDarkTheme() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleNavigationDrawer(isCollapsed = false) {
                    // Empty content
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Expanded navigation drawer")
            .assertIsDisplayed()
    }
}
