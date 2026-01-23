package com.example.conversion.domain.model

import android.net.Uri
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OperationHistoryTest {

    private val operation1 = RenameOperation(
        id = "op1",
        originalUri = Uri.parse("content://media/external/images/1"),
        newUri = Uri.parse("content://media/external/images/2"),
        originalName = "IMG_001.jpg",
        newName = "vacation_001.jpg",
        timestamp = 1000L
    )

    private val operation2 = RenameOperation(
        id = "op2",
        originalUri = Uri.parse("content://media/external/images/3"),
        newUri = Uri.parse("content://media/external/images/4"),
        originalName = "IMG_002.jpg",
        newName = "vacation_002.jpg",
        timestamp = 2000L
    )

    private val operation3 = RenameOperation(
        id = "op3",
        originalUri = Uri.parse("content://media/external/images/5"),
        newUri = Uri.parse("content://media/external/images/6"),
        originalName = "IMG_003.jpg",
        newName = "vacation_003.jpg",
        timestamp = 3000L
    )

    @Test
    fun `empty history has no operations`() {
        val history = OperationHistory.empty()
        
        assertTrue(history.operations.isEmpty())
        assertEquals(-1, history.currentIndex)
        assertFalse(history.canUndo())
        assertFalse(history.canRedo())
    }

    @Test
    fun `addOperation adds first operation correctly`() {
        val history = OperationHistory.empty()
        
        val newHistory = history.addOperation(operation1)
        
        assertEquals(1, newHistory.operations.size)
        assertEquals(0, newHistory.currentIndex)
        assertEquals(operation1, newHistory.operations[0])
    }

    @Test
    fun `addOperation adds multiple operations correctly`() {
        val history = OperationHistory.empty()
        
        val history1 = history.addOperation(operation1)
        val history2 = history1.addOperation(operation2)
        
        assertEquals(2, history2.operations.size)
        assertEquals(1, history2.currentIndex)
        assertEquals(operation1, history2.operations[0])
        assertEquals(operation2, history2.operations[1])
    }

    @Test
    fun `addOperation discards operations after current index`() {
        // Given: history with 3 operations, undo once (currentIndex = 1)
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .addOperation(operation2)
            .addOperation(operation3)
            .undo()
        
        assertEquals(2, history.currentIndex)
        
        // When: add new operation
        val newOperation = operation1.copy(id = "op4")
        val newHistory = history.addOperation(newOperation)
        
        // Then: operation3 should be discarded
        assertEquals(3, newHistory.operations.size)
        assertEquals(2, newHistory.currentIndex)
        assertEquals(operation1, newHistory.operations[0])
        assertEquals(operation2, newHistory.operations[1])
        assertEquals(newOperation, newHistory.operations[2])
    }

    @Test
    fun `canUndo returns true when operations exist`() {
        val history = OperationHistory.empty().addOperation(operation1)
        
        assertTrue(history.canUndo())
    }

    @Test
    fun `canUndo returns false when no operations`() {
        val history = OperationHistory.empty()
        
        assertFalse(history.canUndo())
    }

    @Test
    fun `canUndo returns false after undoing all operations`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .undo()
        
        assertFalse(history.canUndo())
    }

    @Test
    fun `canRedo returns false initially`() {
        val history = OperationHistory.empty().addOperation(operation1)
        
        assertFalse(history.canRedo())
    }

    @Test
    fun `canRedo returns true after undo`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .undo()
        
        assertTrue(history.canRedo())
    }

    @Test
    fun `undo decrements currentIndex`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .addOperation(operation2)
        
        assertEquals(1, history.currentIndex)
        
        val undoneHistory = history.undo()
        assertEquals(0, undoneHistory.currentIndex)
    }

    @Test
    fun `undo does nothing when already at start`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .undo()
        
        assertEquals(-1, history.currentIndex)
        
        val undoneHistory = history.undo()
        assertEquals(-1, undoneHistory.currentIndex)
    }

    @Test
    fun `redo increments currentIndex`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .addOperation(operation2)
            .undo()
        
        assertEquals(0, history.currentIndex)
        
        val redoneHistory = history.redo()
        assertEquals(1, redoneHistory.currentIndex)
    }

    @Test
    fun `redo does nothing when at end`() {
        val history = OperationHistory.empty().addOperation(operation1)
        
        assertEquals(0, history.currentIndex)
        
        val redoneHistory = history.redo()
        assertEquals(0, redoneHistory.currentIndex)
    }

    @Test
    fun `getUndoOperation returns current operation`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .addOperation(operation2)
        
        assertEquals(operation2, history.getUndoOperation())
    }

    @Test
    fun `getUndoOperation returns null when cannot undo`() {
        val history = OperationHistory.empty()
        
        assertNull(history.getUndoOperation())
    }

    @Test
    fun `getRedoOperation returns next operation`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .addOperation(operation2)
            .undo()
        
        assertEquals(operation2, history.getRedoOperation())
    }

    @Test
    fun `getRedoOperation returns null when cannot redo`() {
        val history = OperationHistory.empty().addOperation(operation1)
        
        assertNull(history.getRedoOperation())
    }

    @Test
    fun `clear returns empty history`() {
        val history = OperationHistory.empty()
            .addOperation(operation1)
            .addOperation(operation2)
        
        val clearedHistory = history.clear()
        
        assertTrue(clearedHistory.operations.isEmpty())
        assertEquals(-1, clearedHistory.currentIndex)
    }

    @Test
    fun `complex undo redo sequence works correctly`() {
        // Start with empty history
        var history = OperationHistory.empty()
        assertFalse(history.canUndo())
        assertFalse(history.canRedo())
        
        // Add three operations
        history = history.addOperation(operation1)
        history = history.addOperation(operation2)
        history = history.addOperation(operation3)
        assertEquals(2, history.currentIndex)
        assertTrue(history.canUndo())
        assertFalse(history.canRedo())
        
        // Undo once
        history = history.undo()
        assertEquals(1, history.currentIndex)
        assertTrue(history.canUndo())
        assertTrue(history.canRedo())
        assertEquals(operation3, history.getRedoOperation())
        
        // Undo again
        history = history.undo()
        assertEquals(0, history.currentIndex)
        assertTrue(history.canUndo())
        assertTrue(history.canRedo())
        
        // Redo once
        history = history.redo()
        assertEquals(1, history.currentIndex)
        
        // Add new operation (should discard operation3)
        val newOp = operation1.copy(id = "op4")
        history = history.addOperation(newOp)
        assertEquals(2, history.currentIndex)
        assertEquals(3, history.operations.size)
        assertEquals(newOp, history.operations[2])
        assertFalse(history.canRedo())
    }
}
