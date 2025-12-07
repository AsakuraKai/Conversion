package com.example.conversion.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for PresetQRData.
 */
class PresetQRDataTest {

    private val validQRData = PresetQRData(
        version = 1,
        templateName = "Test Template",
        pattern = "Photo_{number}",
        prefix = "Photo_",
        startNumber = 1,
        digitCount = 3,
        preserveExtension = true,
        sortStrategy = "NATURAL",
        createdAt = System.currentTimeMillis()
    )

    @Test
    fun `valid QR data passes validation`() {
        assertTrue(validQRData.isValid())
    }

    @Test
    fun `invalid version fails validation`() {
        val invalidData = validQRData.copy(version = 999)
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `blank template name fails validation`() {
        val invalidData = validQRData.copy(templateName = "")
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `blank pattern fails validation`() {
        val invalidData = validQRData.copy(pattern = "")
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `blank prefix fails validation`() {
        val invalidData = validQRData.copy(prefix = "")
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `negative start number fails validation`() {
        val invalidData = validQRData.copy(startNumber = -1)
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `digit count below 1 fails validation`() {
        val invalidData = validQRData.copy(digitCount = 0)
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `digit count above 10 fails validation`() {
        val invalidData = validQRData.copy(digitCount = 11)
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `invalid sort strategy fails validation`() {
        val invalidData = validQRData.copy(sortStrategy = "INVALID_STRATEGY")
        assertFalse(invalidData.isValid())
    }

    @Test
    fun `prefix with illegal characters fails validation`() {
        val illegalChars = listOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')
        illegalChars.forEach { char ->
            val invalidData = validQRData.copy(prefix = "Photo${char}_")
            assertFalse("Should fail for character: $char", invalidData.isValid())
        }
    }

    @Test
    fun `all valid sort strategies pass validation`() {
        val strategies = listOf("NATURAL", "DATE_MODIFIED", "SIZE", "ORIGINAL_ORDER")
        strategies.forEach { strategy ->
            val data = validQRData.copy(sortStrategy = strategy)
            assertTrue("Should pass for strategy: $strategy", data.isValid())
        }
    }

    @Test
    fun `toRenameTemplate creates valid template`() {
        val template = validQRData.toRenameTemplate()

        assertEquals(validQRData.templateName, template.name)
        assertEquals(validQRData.pattern, template.pattern)
        assertEquals(validQRData.prefix, template.config.prefix)
        assertEquals(validQRData.startNumber, template.config.startNumber)
        assertEquals(validQRData.digitCount, template.config.digitCount)
        assertEquals(validQRData.preserveExtension, template.config.preserveExtension)
        assertEquals(validQRData.sortStrategy, template.config.sortStrategy.name)
        assertFalse(template.isFavorite)
        assertNull(template.lastUsedAt)
    }

    @Test
    fun `toRenameTemplate generates unique ID`() {
        val template1 = validQRData.toRenameTemplate()
        val template2 = validQRData.toRenameTemplate()

        assertNotEquals(template1.id, template2.id)
    }

    @Test
    fun `fromRenameTemplate creates valid QR data`() {
        val template = RenameTemplate(
            id = "test-id",
            name = "My Template",
            pattern = "IMG_{number}",
            config = RenameConfig(
                prefix = "IMG_",
                startNumber = 100,
                digitCount = 5,
                preserveExtension = false,
                sortStrategy = SortStrategy.DATE_MODIFIED
            ),
            createdAt = 123456789L
        )

        val qrData = PresetQRData.fromRenameTemplate(template)

        assertEquals(PresetQRData.CURRENT_VERSION, qrData.version)
        assertEquals(template.name, qrData.templateName)
        assertEquals(template.pattern, qrData.pattern)
        assertEquals(template.config.prefix, qrData.prefix)
        assertEquals(template.config.startNumber, qrData.startNumber)
        assertEquals(template.config.digitCount, qrData.digitCount)
        assertEquals(template.config.preserveExtension, qrData.preserveExtension)
        assertEquals(template.config.sortStrategy.name, qrData.sortStrategy)
        assertEquals(template.createdAt, qrData.createdAt)
    }

    @Test
    fun `round trip conversion preserves data`() {
        val originalTemplate = RenameTemplate(
            id = "original-id",
            name = "Round Trip Test",
            pattern = "File_{number}",
            config = RenameConfig(
                prefix = "File_",
                startNumber = 50,
                digitCount = 4,
                preserveExtension = true,
                sortStrategy = SortStrategy.SIZE
            ),
            createdAt = System.currentTimeMillis()
        )

        val qrData = PresetQRData.fromRenameTemplate(originalTemplate)
        val reconstructedTemplate = qrData.toRenameTemplate()

        // Note: ID will be different as it's regenerated
        assertEquals(originalTemplate.name, reconstructedTemplate.name)
        assertEquals(originalTemplate.pattern, reconstructedTemplate.pattern)
        assertEquals(originalTemplate.config.prefix, reconstructedTemplate.config.prefix)
        assertEquals(originalTemplate.config.startNumber, reconstructedTemplate.config.startNumber)
        assertEquals(originalTemplate.config.digitCount, reconstructedTemplate.config.digitCount)
        assertEquals(originalTemplate.config.preserveExtension, reconstructedTemplate.config.preserveExtension)
        assertEquals(originalTemplate.config.sortStrategy, reconstructedTemplate.config.sortStrategy)
    }

    @Test
    fun `CURRENT_VERSION is 1`() {
        assertEquals(1, PresetQRData.CURRENT_VERSION)
    }

    @Test
    fun `boundary values for digit count`() {
        val validMin = validQRData.copy(digitCount = 1)
        val validMax = validQRData.copy(digitCount = 10)

        assertTrue(validMin.isValid())
        assertTrue(validMax.isValid())
    }

    @Test
    fun `zero start number is valid`() {
        val data = validQRData.copy(startNumber = 0)
        assertTrue(data.isValid())
    }

    @Test
    fun `large start number is valid`() {
        val data = validQRData.copy(startNumber = 999999)
        assertTrue(data.isValid())
    }

    @Test
    fun `both preserve extension values are valid`() {
        val preserveTrue = validQRData.copy(preserveExtension = true)
        val preserveFalse = validQRData.copy(preserveExtension = false)

        assertTrue(preserveTrue.isValid())
        assertTrue(preserveFalse.isValid())
    }
}
