package com.example.conversion.domain.model

import android.net.Uri
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RenameOperationTest {

    private val validUri = Uri.parse("content://media/external/images/1")
    private val validOperation = RenameOperation(
        id = "op1",
        originalUri = validUri,
        newUri = Uri.parse("content://media/external/images/2"),
        originalName = "IMG_001.jpg",
        newName = "vacation_001.jpg",
        timestamp = 1000L
    )

    @Test
    fun `isValid returns true for valid operation`() {
        assertTrue(validOperation.isValid())
    }

    @Test
    fun `isValid returns false when id is blank`() {
        val operation = validOperation.copy(id = "")
        assertFalse(operation.isValid())
    }

    @Test
    fun `isValid returns false when originalName is blank`() {
        val operation = validOperation.copy(originalName = "")
        assertFalse(operation.isValid())
    }

    @Test
    fun `isValid returns false when newName is blank`() {
        val operation = validOperation.copy(newName = "")
        assertFalse(operation.isValid())
    }

    @Test
    fun `isValid returns false when timestamp is zero`() {
        val operation = validOperation.copy(timestamp = 0)
        assertFalse(operation.isValid())
    }

    @Test
    fun `isValid returns false when timestamp is negative`() {
        val operation = validOperation.copy(timestamp = -1)
        assertFalse(operation.isValid())
    }

    @Test
    fun `create factory method generates operation with current timestamp`() {
        val before = System.currentTimeMillis()
        
        val operation = RenameOperation.create(
            id = "op1",
            originalUri = validUri,
            newUri = Uri.parse("content://media/external/images/2"),
            originalName = "IMG_001.jpg",
            newName = "vacation_001.jpg"
        )
        
        val after = System.currentTimeMillis()
        
        assertNotNull(operation)
        assertEquals("op1", operation.id)
        assertEquals("IMG_001.jpg", operation.originalName)
        assertEquals("vacation_001.jpg", operation.newName)
        assertTrue(operation.timestamp in before..after)
    }

    @Test
    fun `operation preserves all data correctly`() {
        assertEquals("op1", validOperation.id)
        assertEquals(validUri, validOperation.originalUri)
        assertEquals("IMG_001.jpg", validOperation.originalName)
        assertEquals("vacation_001.jpg", validOperation.newName)
        assertEquals(1000L, validOperation.timestamp)
    }
}
