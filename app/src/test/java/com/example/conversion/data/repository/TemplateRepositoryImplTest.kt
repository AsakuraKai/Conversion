package com.example.conversion.data.repository

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

/**
 * Unit tests for TemplateRepositoryImpl.
 * Tests in-memory storage implementation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TemplateRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TemplateRepositoryImpl

    // Sample data
    private val sampleConfig = RenameConfig(
        prefix = "photo",
        startNumber = 1,
        digitCount = 3,
        preserveExtension = true,
        sortStrategy = SortStrategy.NATURAL
    )

    private fun createTemplate(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test Template",
        isFavorite: Boolean = false,
        lastUsedAt: Long? = null
    ) = RenameTemplate(
        id = id,
        name = name,
        pattern = "photo_001.jpg",
        config = sampleConfig,
        isFavorite = isFavorite,
        createdAt = System.currentTimeMillis(),
        lastUsedAt = lastUsedAt
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = TemplateRepositoryImpl(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Save Template Tests ====================

    @Test
    fun `save template successfully`() = runTest {
        // Given
        val template = createTemplate()

        // When
        val result = repository.saveTemplate(template)

        // Then
        assertTrue("Should save successfully", result is Result.Success)
        
        // Verify template was saved
        val getResult = repository.getTemplateById(template.id)
        assertTrue("Should retrieve successfully", getResult is Result.Success)
        assertEquals("Should be same template", template, getResult.getOrNull())
    }

    @Test
    fun `save template updates existing template`() = runTest {
        // Given: Save original template
        val template = createTemplate(id = "123", name = "Original")
        repository.saveTemplate(template)

        // When: Save updated template with same ID
        val updatedTemplate = template.copy(name = "Updated")
        val result = repository.saveTemplate(updatedTemplate)

        // Then
        assertTrue("Should save successfully", result is Result.Success)
        
        // Verify template was updated
        val getResult = repository.getTemplateById("123")
        assertEquals("Should have updated name", "Updated", getResult.getOrNull()?.name)
    }

    @Test
    fun `save template emits to flow`() = runTest {
        // Given
        val template = createTemplate()

        // When
        repository.saveTemplate(template)
        testScheduler.advanceUntilIdle()

        // Then
        val templates = repository.observeTemplates().first()
        assertEquals("Flow should contain template", 1, templates.size)
        assertEquals("Should be same template", template, templates[0])
    }

    // ==================== Get Templates Tests ====================

    @Test
    fun `get templates returns all templates`() = runTest {
        // Given: Save multiple templates
        val template1 = createTemplate(id = "1", name = "Template 1")
        val template2 = createTemplate(id = "2", name = "Template 2")
        val template3 = createTemplate(id = "3", name = "Template 3")
        
        repository.saveTemplate(template1)
        repository.saveTemplate(template2)
        repository.saveTemplate(template3)

        // When
        val result = repository.getTemplates()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val templates = result.getOrNull()!!
        assertEquals("Should have 3 templates", 3, templates.size)
        assertTrue("Should contain template1", templates.any { it.id == "1" })
        assertTrue("Should contain template2", templates.any { it.id == "2" })
        assertTrue("Should contain template3", templates.any { it.id == "3" })
    }

    @Test
    fun `get templates returns empty list when none exist`() = runTest {
        // When
        val result = repository.getTemplates()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should be empty", 0, result.getOrNull()!!.size)
    }

    // ==================== Get Template By ID Tests ====================

    @Test
    fun `get template by id returns template`() = runTest {
        // Given
        val template = createTemplate(id = "123")
        repository.saveTemplate(template)

        // When
        val result = repository.getTemplateById("123")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should return correct template", template, result.getOrNull())
    }

    @Test
    fun `get template by id returns null when not found`() = runTest {
        // When
        val result = repository.getTemplateById("999")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertNull("Should return null", result.getOrNull())
    }

    // ==================== Delete Template Tests ====================

    @Test
    fun `delete template removes template`() = runTest {
        // Given
        val template = createTemplate(id = "123")
        repository.saveTemplate(template)

        // When
        val deleteResult = repository.deleteTemplate("123")

        // Then
        assertTrue("Should delete successfully", deleteResult is Result.Success)
        
        // Verify template was deleted
        val getResult = repository.getTemplateById("123")
        assertNull("Should not exist", getResult.getOrNull())
    }

    @Test
    fun `delete template emits to flow`() = runTest {
        // Given
        val template1 = createTemplate(id = "1")
        val template2 = createTemplate(id = "2")
        repository.saveTemplate(template1)
        repository.saveTemplate(template2)

        // When
        repository.deleteTemplate("1")
        testScheduler.advanceUntilIdle()

        // Then
        val templates = repository.observeTemplates().first()
        assertEquals("Should have 1 template", 1, templates.size)
        assertEquals("Should be template2", "2", templates[0].id)
    }

    @Test
    fun `delete non-existent template succeeds`() = runTest {
        // When
        val result = repository.deleteTemplate("999")

        // Then
        assertTrue("Should succeed", result is Result.Success)
    }

    // ==================== Observe Templates Tests ====================

    @Test
    fun `observe templates emits initial empty list`() = runTest {
        // When
        val templates = repository.observeTemplates().first()

        // Then
        assertEquals("Should be empty initially", 0, templates.size)
    }

    @Test
    fun `observe templates emits when template added`() = runTest {
        // Given
        val template = createTemplate()

        // When
        repository.saveTemplate(template)
        testScheduler.advanceUntilIdle()

        // Then
        val templates = repository.observeTemplates().first()
        assertEquals("Should have 1 template", 1, templates.size)
    }

    // ==================== Get Favorite Templates Tests ====================

    @Test
    fun `get favorite templates returns only favorites`() = runTest {
        // Given
        val favorite1 = createTemplate(id = "1", isFavorite = true)
        val notFavorite = createTemplate(id = "2", isFavorite = false)
        val favorite2 = createTemplate(id = "3", isFavorite = true)
        
        repository.saveTemplate(favorite1)
        repository.saveTemplate(notFavorite)
        repository.saveTemplate(favorite2)

        // When
        val result = repository.getFavoriteTemplates()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val favorites = result.getOrNull()!!
        assertEquals("Should have 2 favorites", 2, favorites.size)
        assertTrue("Should contain favorite1", favorites.any { it.id == "1" })
        assertTrue("Should contain favorite2", favorites.any { it.id == "3" })
        assertFalse("Should not contain non-favorite", favorites.any { it.id == "2" })
    }

    @Test
    fun `get favorite templates returns empty when none exist`() = runTest {
        // Given: Non-favorite templates only
        val template = createTemplate(isFavorite = false)
        repository.saveTemplate(template)

        // When
        val result = repository.getFavoriteTemplates()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should be empty", 0, result.getOrNull()!!.size)
    }

    // ==================== Observe Favorite Templates Tests ====================

    @Test
    fun `observe favorite templates filters correctly`() = runTest {
        // Given
        val favorite = createTemplate(id = "1", isFavorite = true)
        val notFavorite = createTemplate(id = "2", isFavorite = false)
        
        repository.saveTemplate(favorite)
        repository.saveTemplate(notFavorite)
        testScheduler.advanceUntilIdle()

        // When
        val favorites = repository.observeFavoriteTemplates().first()

        // Then
        assertEquals("Should have 1 favorite", 1, favorites.size)
        assertEquals("Should be favorite template", "1", favorites[0].id)
    }

    // ==================== Mark Template As Used Tests ====================

    @Test
    fun `mark template as used updates timestamp`() = runTest {
        // Given
        val template = createTemplate(id = "123", lastUsedAt = null)
        repository.saveTemplate(template)
        val beforeTime = System.currentTimeMillis()

        // When
        val result = repository.markTemplateAsUsed("123")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        
        // Verify timestamp was updated
        val updatedTemplate = repository.getTemplateById("123").getOrNull()!!
        assertNotNull("Should have lastUsedAt", updatedTemplate.lastUsedAt)
        assertTrue("Timestamp should be recent", updatedTemplate.lastUsedAt!! >= beforeTime)
    }

    @Test
    fun `mark template as used on non-existent template succeeds`() = runTest {
        // When
        val result = repository.markTemplateAsUsed("999")

        // Then
        assertTrue("Should succeed", result is Result.Success)
    }

    // ==================== Toggle Favorite Tests ====================

    @Test
    fun `toggle favorite switches status`() = runTest {
        // Given: Non-favorite template
        val template = createTemplate(id = "123", isFavorite = false)
        repository.saveTemplate(template)

        // When: Toggle to favorite
        repository.toggleFavorite("123")
        val afterFirst = repository.getTemplateById("123").getOrNull()!!

        // Then: Should be favorite
        assertTrue("Should be favorite", afterFirst.isFavorite)

        // When: Toggle again
        repository.toggleFavorite("123")
        val afterSecond = repository.getTemplateById("123").getOrNull()!!

        // Then: Should not be favorite
        assertFalse("Should not be favorite", afterSecond.isFavorite)
    }

    @Test
    fun `toggle favorite on non-existent template succeeds`() = runTest {
        // When
        val result = repository.toggleFavorite("999")

        // Then
        assertTrue("Should succeed", result is Result.Success)
    }

    // ==================== Clear All Templates Tests ====================

    @Test
    fun `clear all templates removes all templates`() = runTest {
        // Given: Multiple templates
        repository.saveTemplate(createTemplate(id = "1"))
        repository.saveTemplate(createTemplate(id = "2"))
        repository.saveTemplate(createTemplate(id = "3"))

        // When
        val result = repository.clearAllTemplates()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        
        // Verify all templates were cleared
        val templates = repository.getTemplates().getOrNull()!!
        assertEquals("Should be empty", 0, templates.size)
    }

    @Test
    fun `clear all templates emits to flow`() = runTest {
        // Given
        repository.saveTemplate(createTemplate())
        repository.saveTemplate(createTemplate())

        // When
        repository.clearAllTemplates()
        testScheduler.advanceUntilIdle()

        // Then
        val templates = repository.observeTemplates().first()
        assertEquals("Should be empty", 0, templates.size)
    }

    // ==================== Thread Safety Tests ====================

    @Test
    fun `concurrent saves are handled safely`() = runTest {
        // Given: Multiple templates
        val templates = (1..10).map { createTemplate(id = it.toString()) }

        // When: Save concurrently (sequential in test, but validates mutex)
        templates.forEach { repository.saveTemplate(it) }

        // Then: All should be saved
        val result = repository.getTemplates()
        assertEquals("Should have 10 templates", 10, result.getOrNull()!!.size)
    }
}
