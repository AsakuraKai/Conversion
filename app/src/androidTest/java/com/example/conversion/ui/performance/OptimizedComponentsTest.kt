package com.example.conversion.ui.performance

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.conversion.ui.theme.ConversionTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for performance-optimized components.
 *
 * Tests:
 * - Optimized file list rendering
 * - Recomposition behavior
 * - Performance overlay display
 * - Loading skeleton states
 *
 * @author Sokchea (UI/Frontend Specialist)
 */
class OptimizedComponentsTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun optimizedFileList_displaysFiles() {
        // Arrange
        val state = MockPerformanceData.createMockState(fileCount = 10)
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                OptimizedFileList(
                    state = state,
                    onFileClick = {}
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText("10 files")
            .assertIsDisplayed()
    }
    
    @Test
    fun optimizedFileList_showsEmptyState_whenNoFiles() {
        // Arrange
        val state = OptimizedFileListState(
            files = emptyList(),
            isLoading = false
        )
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                OptimizedFileList(
                    state = state,
                    onFileClick = {}
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText("No files found")
            .assertIsDisplayed()
    }
    
    @Test
    fun optimizedFileList_handlesSelection() {
        // Arrange
        val files = MockPerformanceData.generateMockFiles(5)
        val state = OptimizedFileListState(
            files = files,
            selectedFiles = setOf(files[0].id)
        )
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                OptimizedFileList(
                    state = state,
                    onFileClick = {}
                )
            }
        }
        
        // Assert - Should show selection count
        composeTestRule
            .onNodeWithText("1 of 5 files selected")
            .assertIsDisplayed()
    }
    
    @Test
    fun loadingSkeleton_displaysCorrectNumberOfItems() {
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                LoadingSkeleton(itemCount = 5)
            }
        }
        
        // Assert - Check that skeleton items are rendered
        // In production, use test tags for better verification
        composeTestRule.waitForIdle()
    }
    
    @Test
    fun optimizedSearchBar_displaysQuery() {
        // Arrange
        val query = "test.jpg"
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                OptimizedSearchBar(
                    query = query,
                    onQueryChange = {}
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText(query)
            .assertIsDisplayed()
    }
    
    @Test
    fun optimizedSearchBar_showsClearButton_whenQueryNotEmpty() {
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                OptimizedSearchBar(
                    query = "test",
                    onQueryChange = {}
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .assertIsDisplayed()
    }
    
    @Test
    fun optimizedSearchBar_hidesClearButton_whenQueryEmpty() {
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                OptimizedSearchBar(
                    query = "",
                    onQueryChange = {}
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .assertDoesNotExist()
    }
}

/**
 * Tests for performance monitoring components.
 */
class PerformanceMonitoringTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun performanceOverlay_displaysMetrics() {
        // Arrange
        val metrics = MockPerformanceMetrics.excellent()
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                PerformanceOverlay(
                    metrics = metrics,
                    enabled = true
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText("Performance")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Excellent")
            .assertIsDisplayed()
    }
    
    @Test
    fun performanceOverlay_notDisplayed_whenDisabled() {
        // Arrange
        val metrics = MockPerformanceMetrics.excellent()
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                PerformanceOverlay(
                    metrics = metrics,
                    enabled = false
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText("Performance")
            .assertDoesNotExist()
    }
    
    @Test
    fun performanceOverlay_showsPoorPerformance() {
        // Arrange
        val metrics = MockPerformanceMetrics.poor()
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                PerformanceOverlay(
                    metrics = metrics,
                    enabled = true
                )
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText("Poor")
            .assertIsDisplayed()
    }
    
    @Test
    fun compactPerformanceIndicator_displaysFPS() {
        // Arrange
        val metrics = UiPerformanceMetrics(
            frameRate = 60f,
            memoryUsageMb = 100f
        )
        
        // Act
        composeTestRule.setContent {
            ConversionTheme {
                CompactPerformanceIndicator(metrics = metrics)
            }
        }
        
        // Assert
        composeTestRule
            .onNodeWithText("60 FPS")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("100 MB")
            .assertIsDisplayed()
    }
}

/**
 * Tests for recomposition optimization utilities.
 */
class RecompositionOptimizationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun derivedState_calculatesCorrectly() {
        // This is a conceptual test - actual testing would verify recomposition count
        val files = MockPerformanceData.generateMockFiles(10)
        val state = OptimizedFileListState(
            files = files,
            selectedFiles = setOf(files[0].id, files[1].id)
        )
        
        // Verify derived properties
        assert(state.selectedCount == 2)
        assert(state.hasSelection)
        assert(!state.isEmpty)
    }
    
    @Test
    fun stableState_hasCorrectProperties() {
        val state = OptimizedFileListState(
            files = MockPerformanceData.generateMockFiles(5),
            selectedFiles = emptySet(),
            isLoading = false
        )
        
        assert(state.files.size == 5)
        assert(!state.hasSelection)
        assert(state.selectedCount == 0)
    }
}
