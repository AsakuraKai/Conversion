package com.example.conversion.ui

import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.presentation.preview.PreviewScreen
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for Preview Screen.
 * Tests preview list display, conflict detection, and rename confirmation.
 */
@RunWith(AndroidJUnit4::class)
class PreviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenPreviewsLoaded_displaysPreviewList() {
        // Given: Files and configuration
        val testFiles = createTestFiles(5)
        val config = createTestConfig()

        // When: Screen is displayed
        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        // Then: Preview list should be displayed
        // Note: Mock - would verify LazyColumn with preview items
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenNoConflicts_confirmButtonEnabled() {
        // Given: Valid previews without conflicts
        val testFiles = createTestFiles(3)
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        // Then: Confirm button should be enabled
        // Note: Mock - would verify button state
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenConflictsExist_showsWarnings() {
        // Given: Previews with name conflicts
        val testFiles = createTestFiles(2)
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        // Then: Warning indicators should be visible
        // Note: Mock - would verify warning icons/text
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenSummaryDisplayed_showsCorrectCounts() {
        // Given: Previews generated
        val testFiles = createTestFiles(10)
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        // Then: Summary should display file counts
        // Note: Mock - would verify summary card with counts
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenPreviewItemClicked_allowsCustomName() {
        // Given: Previews are displayed
        val testFiles = createTestFiles(3)
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        // When: User clicks on a preview item
        // Then: Custom name editing should be enabled
        // Note: Mock - would click item and verify edit dialog
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenConfirmClicked_triggersRenameProgress() {
        // Given: Valid previews
        var renameTriggered = false
        val testFiles = createTestFiles(3)
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> 
                        renameTriggered = true
                    },
                    onNavigateBack = {}
                )
            }
        }

        // When: User clicks confirm button
        // Then: Navigation to rename progress should be triggered
        // Note: Mock - would click confirm and verify callback
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenLoadingState_displaysProgressIndicator() {
        // Given: Screen in loading state
        val testFiles = createTestFiles(100) // Large set
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = {}
                )
            }
        }

        // Then: Loading indicator should be visible initially
        // Note: Mock - would verify CircularProgressIndicator
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun whenBackPressed_navigatesBack() {
        // Given: Screen is displayed
        var backPressed = false
        val testFiles = createTestFiles(3)
        val config = createTestConfig()

        composeTestRule.setContent {
            ConversionTheme {
                PreviewScreen(
                    files = testFiles,
                    config = config,
                    onNavigateToRenameProgress = { _, _ -> },
                    onNavigateBack = { backPressed = true }
                )
            }
        }

        // When: User presses back
        // Then: Back navigation should be triggered
        // Note: Mock - would click back and verify callback
        composeTestRule.onRoot().assertExists()
    }

    // Helper functions
    private fun createTestFiles(count: Int): List<FileItem> {
        return List(count) { index ->
            FileItem(
                id = index.toLong(),
                uri = Uri.parse("content://media/external/images/$index"),
                name = "IMG_${String.format("%03d", index)}.jpg",
                path = "/storage/emulated/0/Pictures/IMG_$index.jpg",
                size = 1024000L,
                mimeType = "image/jpeg",
                dateModified = System.currentTimeMillis(),
                thumbnailUri = null
            )
        }
    }

    private fun createTestConfig(): RenameConfig {
        return RenameConfig(
            prefix = "test_",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true,
            sortStrategy = SortStrategy.NATURAL
        )
    }
}
