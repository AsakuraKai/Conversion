package com.example.conversion.domain.usecase.history

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.repository.HistoryRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class HistoryUseCasesTest {

    private lateinit var historyRepository: HistoryRepository
    private val testDispatcher = StandardTestDispatcher()

    private val testOperation = RenameOperation(
        id = "op1",
        originalUri = Uri.parse("content://media/external/images/1"),
        newUri = Uri.parse("content://media/external/images/2"),
        originalName = "IMG_001.jpg",
        newName = "vacation_001.jpg",
        timestamp = 1000L
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        historyRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ============================================
    // SaveOperationUseCase Tests
    // ============================================

    @Test
    fun `saveOperation should save valid operation successfully`() = runTest {
        // Given
        coEvery { historyRepository.saveOperation(testOperation) } returns Result.Success(Unit)
        val useCase = SaveOperationUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(testOperation)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { historyRepository.saveOperation(testOperation) }
    }

    @Test
    fun `saveOperation should fail with invalid operation`() = runTest {
        // Given
        val invalidOperation = testOperation.copy(id = "")
        val useCase = SaveOperationUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(invalidOperation)

        // Then
        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { historyRepository.saveOperation(any()) }
    }

    @Test
    fun `saveOperation should handle repository errors`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { historyRepository.saveOperation(testOperation) } returns Result.Error(exception)
        val useCase = SaveOperationUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(testOperation)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    // ============================================
    // UndoRenameUseCase Tests
    // ============================================

    @Test
    fun `undo should revert file successfully`() = runTest {
        // Given
        val revertedUri = testOperation.originalUri
        coEvery { historyRepository.undoOperation(testOperation) } returns Result.Success(revertedUri)
        val useCase = UndoRenameUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(testOperation)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(revertedUri, (result as Result.Success).data)
        coVerify(exactly = 1) { historyRepository.undoOperation(testOperation) }
    }

    @Test
    fun `undo should fail with invalid operation`() = runTest {
        // Given
        val invalidOperation = testOperation.copy(originalName = "")
        val useCase = UndoRenameUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(invalidOperation)

        // Then
        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { historyRepository.undoOperation(any()) }
    }

    @Test
    fun `undo should handle file operation errors`() = runTest {
        // Given
        val exception = IllegalStateException("File not found")
        coEvery { historyRepository.undoOperation(testOperation) } returns Result.Error(exception)
        val useCase = UndoRenameUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(testOperation)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    // ============================================
    // RedoRenameUseCase Tests
    // ============================================

    @Test
    fun `redo should reapply rename successfully`() = runTest {
        // Given
        val renamedUri = testOperation.newUri
        coEvery { historyRepository.redoOperation(testOperation) } returns Result.Success(renamedUri)
        val useCase = RedoRenameUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(testOperation)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(renamedUri, (result as Result.Success).data)
        coVerify(exactly = 1) { historyRepository.redoOperation(testOperation) }
    }

    @Test
    fun `redo should fail with invalid operation`() = runTest {
        // Given
        val invalidOperation = testOperation.copy(newName = "")
        val useCase = RedoRenameUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(invalidOperation)

        // Then
        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { historyRepository.redoOperation(any()) }
    }

    @Test
    fun `redo should handle file operation errors`() = runTest {
        // Given
        val exception = IllegalStateException("File not found")
        coEvery { historyRepository.redoOperation(testOperation) } returns Result.Error(exception)
        val useCase = RedoRenameUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(testOperation)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    // ============================================
    // GetHistoryUseCase Tests
    // ============================================

    @Test
    fun `getHistory should return all operations`() = runTest {
        // Given
        val operations = listOf(testOperation)
        coEvery { historyRepository.getHistory() } returns Result.Success(operations)
        val useCase = GetHistoryUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(operations, (result as Result.Success).data)
        coVerify(exactly = 1) { historyRepository.getHistory() }
    }

    @Test
    fun `getHistory should return empty list when no history`() = runTest {
        // Given
        coEvery { historyRepository.getHistory() } returns Result.Success(emptyList())
        val useCase = GetHistoryUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `getHistory should handle repository errors`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { historyRepository.getHistory() } returns Result.Error(exception)
        val useCase = GetHistoryUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    // ============================================
    // ClearHistoryUseCase Tests
    // ============================================

    @Test
    fun `clearHistory should remove all operations`() = runTest {
        // Given
        coEvery { historyRepository.clearHistory() } returns Result.Success(Unit)
        val useCase = ClearHistoryUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { historyRepository.clearHistory() }
    }

    @Test
    fun `clearHistory should handle repository errors`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { historyRepository.clearHistory() } returns Result.Error(exception)
        val useCase = ClearHistoryUseCase(historyRepository, testDispatcher)

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    // ============================================
    // ObserveHistoryUseCase Tests
    // ============================================

    @Test
    fun `observeHistory should return flow of operation history`() = runTest {
        // Given
        val history = OperationHistory(
            operations = listOf(testOperation),
            currentIndex = 0
        )
        every { historyRepository.observeHistory() } returns flowOf(history)
        val useCase = ObserveHistoryUseCase(historyRepository)

        // When
        val flow = useCase()

        // Then
        flow.collect { emittedHistory ->
            assertEquals(history, emittedHistory)
        }
        verify(exactly = 1) { historyRepository.observeHistory() }
    }

    @Test
    fun `observeHistory should emit updates when history changes`() = runTest {
        // Given
        val emptyHistory = OperationHistory.empty()
        val historyWithOp = OperationHistory(
            operations = listOf(testOperation),
            currentIndex = 0
        )
        
        every { historyRepository.observeHistory() } returns flowOf(emptyHistory, historyWithOp)
        val useCase = ObserveHistoryUseCase(historyRepository)

        // When
        val flow = useCase()

        // Then
        val emissions = mutableListOf<OperationHistory>()
        flow.collect { emissions.add(it) }
        
        assertEquals(2, emissions.size)
        assertEquals(emptyHistory, emissions[0])
        assertEquals(historyWithOp, emissions[1])
    }
}
