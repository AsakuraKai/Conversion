package com.example.conversion.integration

import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.usecase.tag.*
import com.example.conversion.domain.usecase.template.*
import com.example.conversion.util.FakeTagRepository
import com.example.conversion.util.FakeTemplateRepository
import com.example.conversion.util.TestDataFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Integration tests for template and tag management.
 * Tests the complete flow of saving, retrieving, and managing templates and tags.
 */
class TemplateTagIntegrationTest {

    private lateinit var templateRepository: FakeTemplateRepository
    private lateinit var tagRepository: FakeTagRepository
    
    private lateinit var saveTemplateUseCase: SaveTemplateUseCase
    private lateinit var getTemplatesUseCase: GetTemplatesUseCase
    private lateinit var deleteTemplateUseCase: DeleteTemplateUseCase
    
    private lateinit var createTagUseCase: CreateTagUseCase
    private lateinit var getTagsUseCase: GetTagsUseCase
    private lateinit var tagFileUseCase: TagFileUseCase
    private lateinit var searchByTagUseCase: SearchByTagUseCase

    @Before
    fun setup() {
        templateRepository = FakeTemplateRepository()
        tagRepository = FakeTagRepository()
        
        saveTemplateUseCase = SaveTemplateUseCase(templateRepository)
        getTemplatesUseCase = GetTemplatesUseCase(templateRepository)
        deleteTemplateUseCase = DeleteTemplateUseCase(templateRepository)
        
        createTagUseCase = CreateTagUseCase(tagRepository)
        getTagsUseCase = GetTagsUseCase(tagRepository)
        tagFileUseCase = TagFileUseCase(tagRepository)
        searchByTagUseCase = SearchByTagUseCase(tagRepository)
    }

    @After
    fun teardown() {
        templateRepository.reset()
        tagRepository.reset()
    }

    @Test
    fun `template lifecycle - create, retrieve, delete`() = runTest {
        // Given: New template
        val template = TestDataFactory.createRenameTemplate(
            id = "template_1",
            name = "Photo Template",
            pattern = "{prefix}{number}",
            isFavorite = false
        )

        // When: Save template
        val saveResult = saveTemplateUseCase(template)
        assertTrue(saveResult.isSuccess)

        // Then: Can retrieve it
        val getResult = getTemplatesUseCase()
        assertTrue(getResult.isSuccess)
        val templates = getResult.getOrThrow()
        assertEquals(1, templates.size)
        assertEquals("Photo Template", templates[0].name)

        // When: Delete template
        val deleteResult = deleteTemplateUseCase(template.id)
        assertTrue(deleteResult.isSuccess)

        // Then: Template removed
        val afterDelete = getTemplatesUseCase().getOrThrow()
        assertEquals(0, afterDelete.size)
    }

    @Test
    fun `favorite templates are filtered correctly`() = runTest {
        // Given: Mix of favorite and non-favorite templates
        val templates = listOf(
            TestDataFactory.createRenameTemplate(id = "1", name = "T1", isFavorite = true),
            TestDataFactory.createRenameTemplate(id = "2", name = "T2", isFavorite = false),
            TestDataFactory.createRenameTemplate(id = "3", name = "T3", isFavorite = true)
        )

        // When: Save all templates
        templates.forEach { saveTemplateUseCase(it) }

        // Then: Can observe favorites
        val favorites = templateRepository.observeFavorites().first()
        assertEquals(2, favorites.size)
        assertTrue(favorites.all { it.isFavorite })
    }

    @Test
    fun `tag lifecycle - create, assign, search, delete`() = runTest {
        // Given: New tag
        val tag = TestDataFactory.createFileTag(
            id = "tag_1",
            name = "Important",
            color = 0xFF0000FF.toInt()
        )

        // When: Create tag
        val createResult = createTagUseCase(tag)
        assertTrue(createResult.isSuccess)

        // Then: Can retrieve it
        val tags = getTagsUseCase().getOrThrow()
        assertEquals(1, tags.size)
        assertEquals("Important", tags[0].name)

        // When: Tag files
        val file = TestDataFactory.createFileItem()
        val tagResult = tagFileUseCase(file.uri, tag.id)
        assertTrue(tagResult.isSuccess)

        // Then: Can search by tag
        val searchResult = searchByTagUseCase(tag.id).getOrThrow()
        assertEquals(1, searchResult.size)
        assertEquals(file.uri.toString(), searchResult[0])
    }

    @Test
    fun `multiple tags per file`() = runTest {
        // Given: Multiple tags
        val tags = listOf(
            TestDataFactory.createFileTag(id = "1", name = "Important"),
            TestDataFactory.createFileTag(id = "2", name = "Work"),
            TestDataFactory.createFileTag(id = "3", name = "Personal")
        )
        tags.forEach { createTagUseCase(it) }

        // When: Tag single file with multiple tags
        val file = TestDataFactory.createFileItem()
        tags.forEach { tag ->
            tagFileUseCase(file.uri, tag.id)
        }

        // Then: File has all tags
        val fileTags = tagRepository.getFileTags(file.uri).getOrThrow()
        assertEquals(3, fileTags.size)
        assertTrue(fileTags.map { it.name }.containsAll(listOf("Important", "Work", "Personal")))
    }

    @Test
    fun `deleting tag removes it from all files`() = runTest {
        // Given: Tag assigned to multiple files
        val tag = TestDataFactory.createFileTag(id = "tag_1", name = "ToDelete")
        createTagUseCase(tag)

        val files = TestDataFactory.createFileItems(3)
        files.forEach { file ->
            tagFileUseCase(file.uri, tag.id)
        }

        // When: Delete tag
        val deleteResult = tagRepository.deleteTag(tag.id)
        assertTrue(deleteResult.isSuccess)

        // Then: Tag removed from all files
        files.forEach { file ->
            val fileTags = tagRepository.getFileTags(file.uri).getOrThrow()
            assertFalse(fileTags.any { it.id == tag.id })
        }
    }

    @Test
    fun `template usage tracking`() = runTest {
        // Given: Template with no usage
        val template = TestDataFactory.createRenameTemplate(
            id = "template_1",
            name = "Test Template",
            lastUsedAt = null
        )
        saveTemplateUseCase(template)

        // When: Update last used time
        val now = Instant.now()
        val updatedTemplate = template.copy(lastUsedAt = now)
        val updateResult = templateRepository.updateTemplate(updatedTemplate)
        assertTrue(updateResult.isSuccess)

        // Then: Last used time updated
        val retrieved = templateRepository.getTemplate(template.id).getOrThrow()
        assertNotNull(retrieved.lastUsedAt)
        assertEquals(now, retrieved.lastUsedAt)
    }

    @Test
    fun `concurrent template operations`() = runTest {
        // Given: Multiple templates
        val templates = List(10) { index ->
            TestDataFactory.createRenameTemplate(
                id = "template_$index",
                name = "Template $index"
            )
        }

        // When: Save all concurrently (simulated)
        templates.forEach { template ->
            saveTemplateUseCase(template)
        }

        // Then: All templates saved
        val allTemplates = getTemplatesUseCase().getOrThrow()
        assertEquals(10, allTemplates.size)
    }
}
