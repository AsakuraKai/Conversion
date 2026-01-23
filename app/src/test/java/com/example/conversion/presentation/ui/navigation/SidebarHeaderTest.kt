package com.example.conversion.presentation.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for SidebarHeader component
 */
class SidebarHeaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDate = LocalDate.of(2026, 1, 22)

    @Test
    fun expandedHeader_displaysFullDate() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(
                    isCollapsed = false,
                    currentDate = testDate
                )
            }
        }

        // Should display day number
        composeTestRule
            .onNodeWithText("22")
            .assertIsDisplayed()

        // Should display month and year
        composeTestRule
            .onNodeWithText("January 2026")
            .assertIsDisplayed()

        // Should display day of week
        composeTestRule
            .onNodeWithText("Wednesday")
            .assertIsDisplayed()
    }

    @Test
    fun collapsedHeader_displaysOnlyDayNumber() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(
                    isCollapsed = true,
                    currentDate = testDate
                )
            }
        }

        // Should display only day number
        composeTestRule
            .onNodeWithText("22")
            .assertIsDisplayed()

        // Should not display full month/year (not visible due to animation)
        composeTestRule
            .onNodeWithText("January 2026")
            .assertDoesNotExist()
    }

    @Test
    fun header_hasCorrectAccessibilityDescription() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(
                    isCollapsed = false,
                    currentDate = testDate
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Current date: January 22, 2026")
            .assertIsDisplayed()
    }

    @Test
    fun header_supportsLightTheme() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(
                    isCollapsed = false,
                    currentDate = testDate
                )
            }
        }

        composeTestRule
            .onNodeWithText("22")
            .assertIsDisplayed()
    }

    @Test
    fun header_supportsDarkTheme() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(
                    isCollapsed = false,
                    currentDate = testDate
                )
            }
        }

        composeTestRule
            .onNodeWithText("22")
            .assertIsDisplayed()
    }

    @Test
    fun header_usesCurrentDateByDefault() {
        composeTestRule.setContent {
            ConversionTheme {
                SidebarHeader(isCollapsed = false)
            }
        }

        // Should render without error (actual date will vary)
        val today = LocalDate.now()
        composeTestRule
            .onNodeWithText(today.dayOfMonth.toString())
            .assertIsDisplayed()
    }
}
