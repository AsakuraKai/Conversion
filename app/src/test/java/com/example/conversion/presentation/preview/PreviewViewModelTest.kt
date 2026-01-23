package com.example.conversion.presentation.preview

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.PreviewItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.usecase.preview.GeneratePreviewUseCase
import com.example.conversion.presentation.preview.PreviewContract.Action
import com.example.conversion.presentation.preview.PreviewContract.Event
import com.example.conversion.presentation.preview.PreviewContract.State
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PreviewViewModelTest {

    private lateinit var viewModel: PreviewViewModel
    private lateinit var generatePreviewUseCase: GeneratePreviewUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    private val testFiles = listOf(
        FileItem(id = "1", name = "file1.jpg", path = "/test/file1.jpg", size = 1024),
        FileItem(id = "2", name = "file2.jpg", path = "/test/file2.jpg", size = 2048),
        FileItem(id = "3", name = "file3.jpg", path = "/test/file3.jpg", size = 3072)
    )

    private val testConfig = RenameConfig(
        pattern = "IMG_{counter}",
        startCounter = 1
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        generatePreviewUseCase = mockk()
        savedStateHandle = SavedStateHandle()
        viewModel = PreviewViewModel(generatePreviewUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialize should generate preview successfully`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg"),
            PreviewItem.success(testFiles[1], "IMG_2.jpg"),
            PreviewItem.success(testFiles[2], "IMG_3.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        // When
        viewModel.handleAction(Action.Initialize(testFiles, testConfig))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertIs<State.Success>(state)
        assertEquals(3, state.previews.size)
        assertEquals(3, state.summary.validRenames)
    }

    @Test
    fun `initialize with empty files should show error`() = runTest {
        // When
        viewModel.handleAction(Action.Initialize(emptyList(), testConfig))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertIs<State.Error>(state)
        assertTrue(state.message.contains("No files selected"))
    }

    @Test
    fun `editItem should update editingItemId in state`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        // When
        viewModel.handleAction(Action.EditItem("1"))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertIs<State.Success>(state)
        assertEquals("1", state.editingItemId)
    }

    @Test
    fun `saveCustomName should update customNames map`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.handleAction(Action.SaveCustomName("1", "MyCustomName.jpg"))
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertIs<State.Success>(state)
            assertEquals("MyCustomName.jpg", state.customNames["1"])
            assertEquals(null, state.editingItemId) // Should close edit dialog

            // Should emit success message
            val event = awaitItem()
            assertIs<Event.ShowMessage>(event)
            assertTrue(event.message.contains("saved"))
        }
    }

    @Test
    fun `saveCustomName with empty name should show error`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.handleAction(Action.SaveCustomName("1", "   "))
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertIs<Event.ShowMessage>(event)
            assertTrue(event.message.contains("cannot be empty"))
        }
    }

    @Test
    fun `cancelEdit should clear editingItemId`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        viewModel.handleAction(Action.EditItem("1"))
        advanceUntilIdle()

        // When
        viewModel.handleAction(Action.CancelEdit)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertIs<State.Success>(state)
        assertEquals(null, state.editingItemId)
    }

    @Test
    fun `resetCustomName should remove custom name`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.handleAction(Action.SaveCustomName("1", "Custom.jpg"))
            advanceUntilIdle()
            awaitItem() // Consume save message

            // When
            viewModel.handleAction(Action.ResetCustomName("1"))
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertIs<State.Success>(state)
            assertEquals(null, state.customNames["1"])

            val event = awaitItem()
            assertIs<Event.ShowMessage>(event)
            assertTrue(event.message.contains("Reset"))
        }
    }

    @Test
    fun `confirmRename should navigate with correct files`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg"),
            PreviewItem.success(testFiles[1], "IMG_2.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(2), testConfig))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.handleAction(Action.ConfirmRename)
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertIs<Event.NavigateToRenameProgress>(event)
            assertEquals(2, event.files.size)
        }
    }

    @Test
    fun `confirmRename with conflicts should not proceed`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.withConflict(testFiles[0], "IMG_1.jpg", "Duplicate name")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.handleAction(Action.ConfirmRename)
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertIs<Event.ShowMessage>(event)
            assertTrue(event.message.contains("Cannot proceed"))
        }
    }

    @Test
    fun `retry should regenerate preview`() = runTest {
        // Given
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Error(Exception("Network error"))

        viewModel.handleAction(Action.Initialize(testFiles, testConfig))
        advanceUntilIdle()

        val errorState = viewModel.state.value
        assertIs<State.Error>(errorState)

        // Update mock to return success
        val previews = listOf(PreviewItem.success(testFiles[0], "IMG_1.jpg"))
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        // When
        viewModel.handleAction(Action.Retry)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertIs<State.Success>(state)
    }

    @Test
    fun `back action should navigate back`() = runTest {
        // When
        viewModel.events.test {
            viewModel.handleAction(Action.Back)
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertIs<Event.NavigateBack>(event)
        }
    }

    @Test
    fun `getEffectiveName should return custom name if exists`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.handleAction(Action.SaveCustomName("1", "Custom.jpg"))
            advanceUntilIdle()
            awaitItem() // Consume message

            // Then
            val state = viewModel.state.value
            assertIs<State.Success>(state)
            assertEquals("Custom.jpg", state.getEffectiveName("1", "IMG_1.jpg"))
        }
    }

    @Test
    fun `getEffectiveName should return default name if no custom name`() = runTest {
        // Given
        val previews = listOf(
            PreviewItem.success(testFiles[0], "IMG_1.jpg")
        )
        
        coEvery { 
            generatePreviewUseCase(any()) 
        } returns Result.Success(previews)

        viewModel.handleAction(Action.Initialize(testFiles.take(1), testConfig))
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertIs<State.Success>(state)
        assertEquals("IMG_1.jpg", state.getEffectiveName("1", "IMG_1.jpg"))
    }
}
