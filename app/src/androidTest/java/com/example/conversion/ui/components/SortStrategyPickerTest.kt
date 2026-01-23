package com.example.conversion.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for SortStrategyPicker component.
 */
class SortStrategyPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sortStrategyPicker_displaysAllStrategies() {
        // Given: SortStrategyPicker is displayed
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {}
                )
            }
        }

        // Then: All four strategies should be displayed
        composeTestRule.onNodeWithText("Natural (IMG_1, IMG_2, IMG_10)", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Date Modified").assertIsDisplayed()
        composeTestRule.onNodeWithText("File Size").assertIsDisplayed()
        composeTestRule.onNodeWithText("Original Selection Order").assertIsDisplayed()
    }

    @Test
    fun sortStrategyPicker_displaysDescriptions() {
        // Given: SortStrategyPicker is displayed
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {}
                )
            }
        }

        // Then: All descriptions should be visible
        composeTestRule.onNodeWithText("Smart number sorting").assertIsDisplayed()
        composeTestRule.onNodeWithText("Newest to oldest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Largest to smallest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Keep selection order").assertIsDisplayed()
    }

    @Test
    fun sortStrategyPicker_naturalIsSelected_radioButtonChecked() {
        // Given: NATURAL strategy is selected
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {}
                )
            }
        }

        // Then: NATURAL radio button should be selected
        composeTestRule.onAllNodesWithContentDescription("").apply {
            // First radio button (NATURAL) should be selected
            get(0).assertIsSelected()
        }
    }

    @Test
    fun sortStrategyPicker_dateModifiedIsSelected_radioButtonChecked() {
        // Given: DATE_MODIFIED strategy is selected
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.DATE_MODIFIED,
                    onStrategySelected = {}
                )
            }
        }

        // Then: DATE_MODIFIED radio button should be selected (second one)
        composeTestRule.onAllNodesWithContentDescription("").apply {
            get(1).assertIsSelected()
        }
    }

    @Test
    fun sortStrategyPicker_clickOnStrategy_callsCallback() {
        // Given: Callback to track clicks
        var selectedStrategy: SortStrategy? = null
        
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = { selectedStrategy = it }
                )
            }
        }

        // When: Clicking on "Date Modified" option
        composeTestRule.onNodeWithText("Date Modified").performClick()

        // Then: Callback should be called with DATE_MODIFIED
        assertEquals(SortStrategy.DATE_MODIFIED, selectedStrategy)
    }

    @Test
    fun sortStrategyPicker_clickOnRadioButton_callsCallback() {
        // Given: Callback to track clicks
        var selectedStrategy: SortStrategy? = null
        
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = { selectedStrategy = it }
                )
            }
        }

        // When: Clicking on File Size radio button (third option)
        composeTestRule.onAllNodesWithContentDescription("").apply {
            get(2).performClick()
        }

        // Then: Callback should be called with SIZE
        assertEquals(SortStrategy.SIZE, selectedStrategy)
    }

    @Test
    fun sortStrategyPicker_withTitle_displaysTitle() {
        // Given: SortStrategyPicker with custom title
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {},
                    title = "Custom Sort Title"
                )
            }
        }

        // Then: Custom title should be displayed
        composeTestRule.onNodeWithText("Custom Sort Title").assertIsDisplayed()
    }

    @Test
    fun sortStrategyPicker_withoutTitle_doesNotDisplayTitle() {
        // Given: SortStrategyPicker without title
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {},
                    title = null
                )
            }
        }

        // Then: Default title should not be displayed
        composeTestRule.onNodeWithText("Sort Order").assertDoesNotExist()
    }

    @Test
    fun sortStrategyPicker_multipleClicks_updatesSelection() {
        // Given: Mutable state to track selection
        var currentStrategy = SortStrategy.NATURAL
        
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = currentStrategy,
                    onStrategySelected = { currentStrategy = it }
                )
            }
        }

        // When: Clicking SIZE option
        composeTestRule.onNodeWithText("File Size").performClick()
        composeTestRule.waitForIdle()
        assertEquals(SortStrategy.SIZE, currentStrategy)

        // When: Clicking ORIGINAL_ORDER option
        composeTestRule.onNodeWithText("Original Selection Order").performClick()
        composeTestRule.waitForIdle()
        
        // Then: Selection should update
        assertEquals(SortStrategy.ORIGINAL_ORDER, currentStrategy)
    }

    @Test
    fun sortStrategyPicker_allOptionsClickable() {
        // Given: Counter to track clicks
        var clickCount = 0
        
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = { clickCount++ }
                )
            }
        }

        // When: Clicking each option
        SortStrategy.entries.forEach { strategy ->
            val displayName = when (strategy) {
                SortStrategy.NATURAL -> "Natural (IMG_1, IMG_2, IMG_10)"
                SortStrategy.DATE_MODIFIED -> "Date Modified"
                SortStrategy.SIZE -> "File Size"
                SortStrategy.ORIGINAL_ORDER -> "Original Selection Order"
            }
            
            composeTestRule.onNodeWithText(displayName, substring = true).performClick()
            composeTestRule.waitForIdle()
        }

        // Then: All clicks should be registered
        assertEquals(SortStrategy.entries.size, clickCount)
    }

    @Test
    fun sortStrategyPicker_accessibilityContentDescriptions() {
        // Given: SortStrategyPicker is displayed
        composeTestRule.setContent {
            ConversionTheme {
                SortStrategyPicker(
                    selectedStrategy = SortStrategy.NATURAL,
                    onStrategySelected = {}
                )
            }
        }

        // Then: All text should be semantically accessible
        composeTestRule.onNodeWithText("Natural (IMG_1, IMG_2, IMG_10)", substring = true)
            .assertHasClickAction()
        composeTestRule.onNodeWithText("Date Modified")
            .assertHasClickAction()
        composeTestRule.onNodeWithText("File Size")
            .assertHasClickAction()
        composeTestRule.onNodeWithText("Original Selection Order")
            .assertHasClickAction()
    }
}
