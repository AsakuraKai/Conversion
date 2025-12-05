package com.example.conversion.presentation.renameconfig

import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.usecase.rename.GenerateFilenameUseCase
import com.example.conversion.domain.usecase.rename.ValidateFilenameUseCase
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Action
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Event
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RenameConfigViewModel focusing on Sort Strategy functionality.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RenameConfigViewModelSortTest {

    private lateinit var viewModel: RenameConfigViewModel
    private lateinit var generateFilenameUseCase: GenerateFilenameUseCase
    private lateinit var validateFilenameUseCase: ValidateFilenameUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        generateFilenameUseCase = mockk(relaxed = true)
        validateFilenameUseCase = mockk(relaxed = true)
        
        viewModel = RenameConfigViewModel(
            generateFilenameUseCase = generateFilenameUseCase,
            validateFilenameUseCase = validateFilenameUseCase,
            savedStateHandle = mockk(relaxed = true)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state has NATURAL sort strategy`() {
        // Given: ViewModel is initialized
        
        // When: Getting initial state
        val state = viewModel.state.value
        
        // Then: Sort strategy should be NATURAL by default
        assertEquals(SortStrategy.NATURAL, state.config.sortStrategy)
    }

    @Test
    fun `UpdateSortStrategy action updates strategy in config`() = runTest {
        // Given: Initial state with NATURAL strategy
        assertEquals(SortStrategy.NATURAL, viewModel.state.value.config.sortStrategy)
        
        // When: User selects DATE_MODIFIED strategy
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.DATE_MODIFIED))
        advanceUntilIdle()
        
        // Then: State should be updated with new strategy
        assertEquals(SortStrategy.DATE_MODIFIED, viewModel.state.value.config.sortStrategy)
    }

    @Test
    fun `UpdateSortStrategy to SIZE updates correctly`() = runTest {
        // Given: Initial state
        
        // When: User selects SIZE strategy
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.SIZE))
        advanceUntilIdle()
        
        // Then: Strategy should be SIZE
        assertEquals(SortStrategy.SIZE, viewModel.state.value.config.sortStrategy)
    }

    @Test
    fun `UpdateSortStrategy to ORIGINAL_ORDER updates correctly`() = runTest {
        // Given: Initial state
        
        // When: User selects ORIGINAL_ORDER strategy
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.ORIGINAL_ORDER))
        advanceUntilIdle()
        
        // Then: Strategy should be ORIGINAL_ORDER
        assertEquals(SortStrategy.ORIGINAL_ORDER, viewModel.state.value.config.sortStrategy)
    }

    @Test
    fun `UpdateSortStrategy preserves other config properties`() = runTest {
        // Given: State with custom config
        viewModel.handleAction(Action.UpdatePrefix("vacation_"))
        viewModel.handleAction(Action.UpdateStartNumber(5))
        viewModel.handleAction(Action.UpdateDigitCount(4))
        viewModel.handleAction(Action.TogglePreserveExtension(false))
        advanceUntilIdle()
        
        val beforeStrategy = viewModel.state.value.config
        
        // When: Updating sort strategy
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.DATE_MODIFIED))
        advanceUntilIdle()
        
        val afterStrategy = viewModel.state.value.config
        
        // Then: Other properties should be preserved
        assertEquals(beforeStrategy.prefix, afterStrategy.prefix)
        assertEquals(beforeStrategy.startNumber, afterStrategy.startNumber)
        assertEquals(beforeStrategy.digitCount, afterStrategy.digitCount)
        assertEquals(beforeStrategy.preserveExtension, afterStrategy.preserveExtension)
        assertEquals(SortStrategy.DATE_MODIFIED, afterStrategy.sortStrategy)
    }

    @Test
    fun `multiple UpdateSortStrategy actions work correctly`() = runTest {
        // Given: Initial state
        assertEquals(SortStrategy.NATURAL, viewModel.state.value.config.sortStrategy)
        
        // When: User changes strategy multiple times
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.SIZE))
        advanceUntilIdle()
        assertEquals(SortStrategy.SIZE, viewModel.state.value.config.sortStrategy)
        
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.DATE_MODIFIED))
        advanceUntilIdle()
        assertEquals(SortStrategy.DATE_MODIFIED, viewModel.state.value.config.sortStrategy)
        
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.ORIGINAL_ORDER))
        advanceUntilIdle()
        assertEquals(SortStrategy.ORIGINAL_ORDER, viewModel.state.value.config.sortStrategy)
        
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.NATURAL))
        advanceUntilIdle()
        
        // Then: Final strategy should be NATURAL
        assertEquals(SortStrategy.NATURAL, viewModel.state.value.config.sortStrategy)
    }

    @Test
    fun `Confirm action includes selected sort strategy in event`() = runTest {
        // Given: Valid configuration with specific sort strategy
        viewModel.handleAction(Action.Initialize(5))
        viewModel.handleAction(Action.UpdatePrefix("photo_"))
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.SIZE))
        advanceUntilIdle()
        
        var capturedConfig: RenameConfig? = null
        
        // Collect events
        val job = kotlinx.coroutines.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { event ->
                if (event is Event.NavigateToPreview) {
                    capturedConfig = event.config
                }
            }
        }
        
        // When: User confirms configuration
        viewModel.handleAction(Action.Confirm)
        advanceUntilIdle()
        
        // Then: Event should contain the selected sort strategy
        assertNotNull(capturedConfig)
        assertEquals(SortStrategy.SIZE, capturedConfig?.sortStrategy)
        
        job.cancel()
    }

    @Test
    fun `sort strategy does not trigger preview regeneration`() = runTest {
        // Given: Valid prefix
        viewModel.handleAction(Action.UpdatePrefix("test_"))
        advanceUntilIdle()
        
        // Clear invocations
        clearMocks(generateFilenameUseCase, answers = false)
        
        // When: Changing sort strategy
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.DATE_MODIFIED))
        advanceUntilIdle()
        
        // Then: Generate filename should NOT be called again
        // (Sort strategy only affects the order, not the filename pattern)
        verify(exactly = 0) { generateFilenameUseCase.invoke(any()) }
    }

    @Test
    fun `all SortStrategy enum values can be set`() = runTest {
        // Given: All available sort strategies
        val strategies = SortStrategy.entries
        
        // When: Setting each strategy
        strategies.forEach { strategy ->
            viewModel.handleAction(Action.UpdateSortStrategy(strategy))
            advanceUntilIdle()
            
            // Then: Strategy should be set correctly
            assertEquals(strategy, viewModel.state.value.config.sortStrategy)
        }
    }

    @Test
    fun `config with sort strategy is valid for confirmation`() = runTest {
        // Given: Valid configuration with all required fields
        viewModel.handleAction(Action.Initialize(3))
        viewModel.handleAction(Action.UpdatePrefix("doc_"))
        viewModel.handleAction(Action.UpdateSortStrategy(SortStrategy.NATURAL))
        advanceUntilIdle()
        
        // When: Checking if can proceed
        val canProceed = viewModel.state.value.canProceed
        
        // Then: Should be able to proceed
        assertTrue(canProceed)
    }
}
