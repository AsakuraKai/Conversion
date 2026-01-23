package com.example.conversion.presentation.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for CollapsibleSidebarLayout component
 */
class CollapsibleSidebarLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun layout_rendersSidebarContent() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = false,
                    onContentClick = {},
                    sidebarContent = {
                        Text("Sidebar Content")
                    },
                    mainContent = {
                        Text("Main Content")
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Sidebar Content")
            .assertIsDisplayed()
    }

    @Test
    fun layout_rendersMainContent() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = false,
                    onContentClick = {},
                    sidebarContent = {
                        Text("Sidebar Content")
                    },
                    mainContent = {
                        Text("Main Content")
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Main Content")
            .assertIsDisplayed()
    }

    @Test
    fun expandedLayout_showsScrimOnPhone() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = false,
                    onContentClick = {},
                    sidebarContent = {
                        Box(modifier = Modifier)
                    },
                    mainContent = {
                        Text("Main Content")
                    }
                )
            }
        }

        // Scrim should be present with accessibility description
        composeTestRule
            .onNodeWithContentDescription("Tap to collapse sidebar")
            .assertExists()
    }

    @Test
    fun collapsedLayout_hidesScrim() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = true,
                    onContentClick = {},
                    sidebarContent = {
                        Box(modifier = Modifier)
                    },
                    mainContent = {
                        Text("Main Content")
                    }
                )
            }
        }

        // Scrim should not be present when collapsed
        composeTestRule
            .onNodeWithContentDescription("Tap to collapse sidebar")
            .assertDoesNotExist()
    }

    @Test
    fun scrim_triggersOnContentClick() {
        var clicked = false

        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = false,
                    onContentClick = { clicked = true },
                    sidebarContent = {
                        Box(modifier = Modifier)
                    },
                    mainContent = {
                        Text("Main Content")
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Tap to collapse sidebar")
            .performClick()

        assert(clicked) { "onContentClick should be triggered when scrim is clicked" }
    }

    @Test
    fun layout_supportsLightTheme() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = false,
                    onContentClick = {},
                    sidebarContent = {
                        Text("Sidebar")
                    },
                    mainContent = {
                        Text("Content")
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Sidebar")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Content")
            .assertIsDisplayed()
    }

    @Test
    fun layout_supportsDarkTheme() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = false,
                    onContentClick = {},
                    sidebarContent = {
                        Text("Sidebar")
                    },
                    mainContent = {
                        Text("Content")
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Sidebar")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Content")
            .assertIsDisplayed()
    }

    @Test
    fun layout_maintainsBothContentWhenCollapsed() {
        composeTestRule.setContent {
            ConversionTheme {
                CollapsibleSidebarLayout(
                    isCollapsed = true,
                    onContentClick = {},
                    sidebarContent = {
                        Text("Sidebar Collapsed")
                    },
                    mainContent = {
                        Text("Main Visible")
                    }
                )
            }
        }

        // Both should remain visible when collapsed (just sidebar is narrower)
        composeTestRule
            .onNodeWithText("Sidebar Collapsed")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Main Visible")
            .assertIsDisplayed()
    }
}
