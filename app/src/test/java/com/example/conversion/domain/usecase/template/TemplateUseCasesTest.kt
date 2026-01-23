package com.example.conversion.domain.usecase.template

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.repository.TemplateRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
 * Unit tests for Template use cases.
 * Tests all template management operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TemplateUseCasesTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var templateRepository: TemplateRepository

    // Use cases
    private lateinit var saveTemplateUseCase: SaveTemplateUseCase
    private lateinit var getTemplatesUseCase: GetTemplatesUseCase
    private lateinit var getTemplateByIdUseCase: GetTemplateByIdUseCase
    private lateinit var deleteTemplateUseCase: DeleteTemplateUseCase
    private lateinit var observeTemplatesUseCase: ObserveTemplatesUseCase
    private lateinit var getFavoriteTemplatesUseCase: GetFavoriteTemplatesUseCase
    private lateinit var markTemplateAsUsedUseCase: MarkTemplateAsUsedUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    // Sample data
    private val sampleConfig = RenameConfig(
        prefix = "photo",
        startNumber = 1,
        digitCount = 3,
        preserveExtension = true,
        sortStrategy = SortStrategy.NATURAL
    )

    private val sampleTemplate = RenameTemplate(
        id = UUID.randomUUID().toString(),
        name = "Photo Template",
        pattern = "photo_001.jpg",
        config = sampleConfig,
        isFavorite = false,
        createdAt = System.currentTimeMillis(),
        lastUsedAt = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        templateRepository = mockk()

        // Initialize use cases
        saveTemplateUseCase = SaveTemplateUseCase(templateRepository, testDispatcher)
        getTemplatesUseCase = GetTemplatesUseCase(templateRepository, testDispatcher)
        getTemplateByIdUseCase = GetTemplateByIdUseCase(templateRepository, testDispatcher)
        deleteTemplateUseCase = DeleteTemplateUseCase(templateRepository, testDispatcher)
        observeTemplatesUseCase = ObserveTemplatesUseCase(templateRepository)
        getFavoriteTemplatesUseCase = GetFavoriteTemplatesUseCase(templateRepository, testDispatcher)
        markTemplateAsUsedUseCase = MarkTemplateAsUsedUseCase(templateRepository, testDispatcher)
        toggleFavoriteUseCase = ToggleFavoriteUseCase(templateRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ==================== SaveTemplateUseCase Tests ====================

    @Test
    fun `save valid template successfully`() = runTest {
        // Given
        coEvery { templateRepository.saveTemplate(any()) } returns Result.Success(Unit)

        // When
        val result = saveTemplateUseCase(sampleTemplate)

        // Then
        assertTrue("Should save successfully", result is Result.Success)
        coVerify(exactly = 1) { templateRepository.saveTemplate(sampleTemplate) }
    }

    @Test
    fun `save template with invalid config fails`() = runTest {
        // Given: Template with invalid config (empty prefix)
        val invalidTemplate = sampleTemplate.copy(
            config = sampleConfig.copy(prefix = "")
        )

        // When
        val result = saveTemplateUseCase(invalidTemplate)

        // Then
        assertTrue("Should fail with error", result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue("Should be IllegalArgumentException", error is IllegalArgumentException)
        coVerify(exactly = 0) { templateRepository.saveTemplate(any()) }
    }

    @Test
    fun `save template with blank name fails`() = runTest {
        // Given: Template with blank name
        val invalidTemplate = sampleTemplate.copy(name = "  ")

        // When
        val result = saveTemplateUseCase(invalidTemplate)

        // Then
        assertTrue("Should fail with error", result is Result.Error)
        coVerify(exactly = 0) { templateRepository.saveTemplate(any()) }
    }

    @Test
    fun `save template with name too long fails`() = runTest {
        // Given: Template with name exceeding max length
        val longName = "a".repeat(RenameTemplate.MAX_NAME_LENGTH + 1)
        val invalidTemplate = sampleTemplate.copy(name = longName)

        // When
        val result = saveTemplateUseCase(invalidTemplate)

        // Then
        assertTrue("Should fail with error", result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue("Error message should mention length", 
            error.message?.contains("too long") == true)
        coVerify(exactly = 0) { templateRepository.saveTemplate(any()) }
    }

    @Test
    fun `save template with pattern too long fails`() = runTest {
        // Given: Template with pattern exceeding max length
        val longPattern = "a".repeat(RenameTemplate.MAX_PATTERN_LENGTH + 1)
        val invalidTemplate = sampleTemplate.copy(pattern = longPattern)

        // When
        val result = saveTemplateUseCase(invalidTemplate)

        // Then
        assertTrue("Should fail with error", result is Result.Error)
        coVerify(exactly = 0) { templateRepository.saveTemplate(any()) }
    }

    // ==================== GetTemplatesUseCase Tests ====================

    @Test
    fun `get all templates returns sorted list`() = runTest {
        // Given: Multiple templates with different creation dates
        val template1 = sampleTemplate.copy(
            id = "1",
            name = "Template 1",
            createdAt = 1000L
        )
        val template2 = sampleTemplate.copy(
            id = "2",
            name = "Template 2",
            createdAt = 3000L
        )
        val template3 = sampleTemplate.copy(
            id = "3",
            name = "Template 3",
            createdAt = 2000L
        )
        val templates = listOf(template1, template2, template3)
        coEvery { templateRepository.getTemplates() } returns Result.Success(templates)

        // When
        val result = getTemplatesUseCase(Unit)

        // Then
        assertTrue("Should get templates successfully", result is Result.Success)
        val resultList = result.getOrNull()!!
        assertEquals("Should have 3 templates", 3, resultList.size)
        
        // Should be sorted by creation date (newest first)
        assertEquals("First should be template2", "2", resultList[0].id)
        assertEquals("Second should be template3", "3", resultList[1].id)
        assertEquals("Third should be template1", "1", resultList[2].id)
    }

    @Test
    fun `get templates returns empty list when none exist`() = runTest {
        // Given
        coEvery { templateRepository.getTemplates() } returns Result.Success(emptyList())

        // When
        val result = getTemplatesUseCase(Unit)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should be empty", 0, result.getOrNull()!!.size)
    }

    // ==================== GetTemplateByIdUseCase Tests ====================

    @Test
    fun `get template by id returns template`() = runTest {
        // Given
        coEvery { templateRepository.getTemplateById("123") } returns Result.Success(sampleTemplate)

        // When
        val result = getTemplateByIdUseCase("123")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should return correct template", sampleTemplate, result.getOrNull())
    }

    @Test
    fun `get template by id returns null when not found`() = runTest {
        // Given
        coEvery { templateRepository.getTemplateById("999") } returns Result.Success(null)

        // When
        val result = getTemplateByIdUseCase("999")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertNull("Should return null", result.getOrNull())
    }

    @Test
    fun `get template with blank id fails`() = runTest {
        // When
        val result = getTemplateByIdUseCase("  ")

        // Then
        assertTrue("Should fail", result is Result.Error)
        coVerify(exactly = 0) { templateRepository.getTemplateById(any()) }
    }

    // ==================== DeleteTemplateUseCase Tests ====================

    @Test
    fun `delete template by id succeeds`() = runTest {
        // Given
        coEvery { templateRepository.deleteTemplate("123") } returns Result.Success(Unit)

        // When
        val result = deleteTemplateUseCase("123")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        coVerify(exactly = 1) { templateRepository.deleteTemplate("123") }
    }

    @Test
    fun `delete template with blank id fails`() = runTest {
        // When
        val result = deleteTemplateUseCase("")

        // Then
        assertTrue("Should fail", result is Result.Error)
        coVerify(exactly = 0) { templateRepository.deleteTemplate(any()) }
    }

    // ==================== ObserveTemplatesUseCase Tests ====================

    @Test
    fun `observe templates returns flow`() = runTest {
        // Given
        val templates = listOf(sampleTemplate)
        every { templateRepository.observeTemplates() } returns flowOf(templates)

        // When
        val flow = observeTemplatesUseCase()

        // Then
        assertNotNull("Flow should not be null", flow)
    }

    // ==================== GetFavoriteTemplatesUseCase Tests ====================

    @Test
    fun `get favorite templates returns only favorites sorted by last used`() = runTest {
        // Given
        val favorite1 = sampleTemplate.copy(
            id = "1",
            isFavorite = true,
            lastUsedAt = 2000L
        )
        val favorite2 = sampleTemplate.copy(
            id = "2",
            isFavorite = true,
            lastUsedAt = 3000L
        )
        val notFavorite = sampleTemplate.copy(
            id = "3",
            isFavorite = false
        )
        
        coEvery { templateRepository.getFavoriteTemplates() } returns 
            Result.Success(listOf(favorite1, favorite2))

        // When
        val result = getFavoriteTemplatesUseCase(Unit)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val favorites = result.getOrNull()!!
        assertEquals("Should have 2 favorites", 2, favorites.size)
        
        // Should be sorted by lastUsedAt (most recent first)
        assertEquals("First should be favorite2", "2", favorites[0].id)
        assertEquals("Second should be favorite1", "1", favorites[1].id)
    }

    @Test
    fun `get favorite templates handles null lastUsedAt`() = runTest {
        // Given
        val favorite1 = sampleTemplate.copy(
            id = "1",
            isFavorite = true,
            lastUsedAt = null
        )
        val favorite2 = sampleTemplate.copy(
            id = "2",
            isFavorite = true,
            lastUsedAt = 1000L
        )
        
        coEvery { templateRepository.getFavoriteTemplates() } returns 
            Result.Success(listOf(favorite1, favorite2))

        // When
        val result = getFavoriteTemplatesUseCase(Unit)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val favorites = result.getOrNull()!!
        assertEquals("Should have 2 favorites", 2, favorites.size)
        assertEquals("Used template should be first", "2", favorites[0].id)
    }

    // ==================== MarkTemplateAsUsedUseCase Tests ====================

    @Test
    fun `mark template as used succeeds`() = runTest {
        // Given
        coEvery { templateRepository.markTemplateAsUsed("123") } returns Result.Success(Unit)

        // When
        val result = markTemplateAsUsedUseCase("123")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        coVerify(exactly = 1) { templateRepository.markTemplateAsUsed("123") }
    }

    @Test
    fun `mark template as used with blank id fails`() = runTest {
        // When
        val result = markTemplateAsUsedUseCase("")

        // Then
        assertTrue("Should fail", result is Result.Error)
        coVerify(exactly = 0) { templateRepository.markTemplateAsUsed(any()) }
    }

    // ==================== ToggleFavoriteUseCase Tests ====================

    @Test
    fun `toggle favorite succeeds`() = runTest {
        // Given
        coEvery { templateRepository.toggleFavorite("123") } returns Result.Success(Unit)

        // When
        val result = toggleFavoriteUseCase("123")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        coVerify(exactly = 1) { templateRepository.toggleFavorite("123") }
    }

    @Test
    fun `toggle favorite with blank id fails`() = runTest {
        // When
        val result = toggleFavoriteUseCase("")

        // Then
        assertTrue("Should fail", result is Result.Error)
        coVerify(exactly = 0) { templateRepository.toggleFavorite(any()) }
    }

    // ==================== RenameTemplate Model Tests ====================

    @Test
    fun `template validation works correctly`() {
        // Valid template
        assertTrue("Valid template should pass", sampleTemplate.isValid())

        // Invalid: blank name
        assertFalse("Blank name should fail", 
            sampleTemplate.copy(name = "").isValid())

        // Invalid: blank pattern
        assertFalse("Blank pattern should fail",
            sampleTemplate.copy(pattern = "  ").isValid())

        // Invalid: invalid config
        assertFalse("Invalid config should fail",
            sampleTemplate.copy(config = sampleConfig.copy(prefix = "")).isValid())
    }

    @Test
    fun `markAsUsed updates timestamp`() {
        // Given: Template with no last used timestamp
        val template = sampleTemplate.copy(lastUsedAt = null)
        val beforeTime = System.currentTimeMillis()

        // When
        val marked = template.markAsUsed()

        // Then
        assertNotNull("Should have lastUsedAt", marked.lastUsedAt)
        assertTrue("Timestamp should be recent", marked.lastUsedAt!! >= beforeTime)
    }

    @Test
    fun `toggleFavorite switches status`() {
        // Given: Non-favorite template
        val template = sampleTemplate.copy(isFavorite = false)

        // When
        val toggled1 = template.toggleFavorite()
        val toggled2 = toggled1.toggleFavorite()

        // Then
        assertTrue("Should be favorite", toggled1.isFavorite)
        assertFalse("Should not be favorite", toggled2.isFavorite)
    }

    @Test
    fun `generatePatternPreview creates correct preview`() {
        // Test with extension
        val preview1 = RenameTemplate.generatePatternPreview(sampleConfig, "jpg")
        assertEquals("Should match pattern", "photo001.jpg", preview1)

        // Test without extension preservation
        val config2 = sampleConfig.copy(preserveExtension = false)
        val preview2 = RenameTemplate.generatePatternPreview(config2, "jpg")
        assertEquals("Should not have extension", "photo001", preview2)

        // Test with different digit count
        val config3 = sampleConfig.copy(digitCount = 5, startNumber = 10)
        val preview3 = RenameTemplate.generatePatternPreview(config3, "png")
        assertEquals("Should have 5 digits", "photo00010.png", preview3)
    }
}
