package com.example.conversion.presentation.viewmodel

import app.cash.turbine.test
import com.example.conversion.presentation.viewmodel.SidebarNavigationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SidebarNavigationViewModel.
 * Tests state management, navigation selection, and auto-collapse behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SidebarNavigationViewModelTest {

    private lateinit var viewModel: SidebarNavigationViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SidebarNavigationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is not collapsed`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isCollapsed)
            assertEquals("home", state.selectedItemId)
            assertFalse(state.autoCollapseOnNavigation)
        }
    }

    @Test
    fun `toggleSidebar changes collapsed state`() = runTest {
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isCollapsed)

            // Toggle to collapsed
            viewModel.toggleSidebar()
            advanceUntilIdle()
            val collapsedState = awaitItem()
            assertTrue(collapsedState.isCollapsed)

            // Toggle back to expanded
            viewModel.toggleSidebar()
            advanceUntilIdle()
            val expandedState = awaitItem()
            assertFalse(expandedState.isCollapsed)
        }
    }

    @Test
    fun `collapseSidebar sets collapsed to true`() = runTest {
        viewModel.state.test {
            // Initial state
            awaitItem()

            // Collapse sidebar
            viewModel.collapseSidebar()
            advanceUntilIdle()
            val collapsedState = awaitItem()
            assertTrue(collapsedState.isCollapsed)
        }
    }

    @Test
    fun `expandSidebar sets collapsed to false`() = runTest {
        viewModel.state.test {
            // Initial state
            awaitItem()

            // First collapse
            viewModel.collapseSidebar()
            advanceUntilIdle()
            awaitItem()

            // Then expand
            viewModel.expandSidebar()
            advanceUntilIdle()
            val expandedState = awaitItem()
            assertFalse(expandedState.isCollapsed)
        }
    }

    @Test
    fun `selectNavigationItem updates selected item id`() = runTest {
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertEquals("home", initialState.selectedItemId)

            // Select different item
            viewModel.selectNavigationItem("file_selection")
            advanceUntilIdle()
            val updatedState = awaitItem()
            assertEquals("file_selection", updatedState.selectedItemId)
        }
    }

    @Test
    fun `setAutoCollapseOnNavigation updates auto-collapse flag`() = runTest {
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.autoCollapseOnNavigation)

            // Enable auto-collapse
            viewModel.setAutoCollapseOnNavigation(true)
            advanceUntilIdle()
            val updatedState = awaitItem()
            assertTrue(updatedState.autoCollapseOnNavigation)
        }
    }

    @Test
    fun `onNavigationComplete collapses when auto-collapse is enabled`() = runTest {
        viewModel.state.test {
            // Initial state
            awaitItem()

            // Enable auto-collapse
            viewModel.setAutoCollapseOnNavigation(true)
            advanceUntilIdle()
            val autoCollapseState = awaitItem()
            assertTrue(autoCollapseState.autoCollapseOnNavigation)

            // Trigger navigation completion
            viewModel.onNavigationComplete()
            advanceUntilIdle()
            val collapsedState = awaitItem()
            assertTrue(collapsedState.isCollapsed)
        }
    }

    @Test
    fun `onNavigationComplete does not collapse when auto-collapse is disabled`() = runTest {
        viewModel.state.test {
            // Initial state (auto-collapse is false by default)
            val initialState = awaitItem()
            assertFalse(initialState.autoCollapseOnNavigation)
            assertFalse(initialState.isCollapsed)

            // Trigger navigation completion
            viewModel.onNavigationComplete()
            advanceUntilIdle()

            // State should remain unchanged (no new emission expected)
            expectNoEvents()
        }
    }

    @Test
    fun `multiple toggle operations work correctly`() = runTest {
        viewModel.state.test {
            // Initial state
            awaitItem()

            // Perform multiple toggles
            repeat(3) {
                viewModel.toggleSidebar()
                advanceUntilIdle()
                val state = awaitItem()
                // Odd iterations should be collapsed, even should be expanded
                assertEquals(it % 2 == 0, state.isCollapsed)
            }
        }
    }

    @Test
    fun `selecting navigation item multiple times updates state correctly`() = runTest {
        viewModel.state.test {
            // Initial state
            awaitItem()

            val itemIds = listOf("home", "file_selection", "settings", "history")
            itemIds.forEach { itemId ->
                viewModel.selectNavigationItem(itemId)
                advanceUntilIdle()
                val state = awaitItem()
                assertEquals(itemId, state.selectedItemId)
            }
        }
    }

    @Test
    fun `collapse and expand operations are independent of selection`() = runTest {
        viewModel.state.test {
            // Initial state
            awaitItem()

            // Select an item
            viewModel.selectNavigationItem("file_selection")
            advanceUntilIdle()
            val selectedState = awaitItem()
            assertEquals("file_selection", selectedState.selectedItemId)
            assertFalse(selectedState.isCollapsed)

            // Collapse sidebar
            viewModel.collapseSidebar()
            advanceUntilIdle()
            val collapsedState = awaitItem()
            assertEquals("file_selection", collapsedState.selectedItemId)
            assertTrue(collapsedState.isCollapsed)
        }
    }

    @Test
    fun `updateBadge ignores settings-only routes`() = runTest {
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.badges.isEmpty())

            // Try to update badge for a Settings-only route (should be ignored)
            viewModel.updateBadge("cloud_sync", 5)
            advanceUntilIdle()

            // No state change expected
            expectNoEvents()
        }
    }

    @Test
    fun `updateBadge works for sidebar-visible routes`() = runTest {
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.badges.isEmpty())

            // Update badge for a sidebar-visible route
            viewModel.updateBadge("home", 3)
            advanceUntilIdle()
            val updatedState = awaitItem()
            assertEquals(3, updatedState.badges["home"])
        }
    }
}
