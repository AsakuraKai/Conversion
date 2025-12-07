package com.example.conversion.domain.usecase.regex

import com.example.conversion.domain.model.RegexFlag
import com.example.conversion.domain.model.RegexPreset
import com.example.conversion.domain.model.RegexRule
import com.example.conversion.domain.common.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ApplyRegexPatternUseCase.
 * Tests regex pattern application to filenames with various scenarios.
 */
class ApplyRegexPatternUseCaseTest {

    private lateinit var useCase: ApplyRegexPatternUseCase

    @Before
    fun setup() {
        useCase = ApplyRegexPatternUseCase()
    }

    // ============================================================
    // Basic Regex Application Tests
    // ============================================================

    @Test
    fun `apply simple replacement pattern`() = runTest {
        val rule = RegexRule(pattern = "test", replacement = "demo")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "test_file.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertTrue("Result should be success", result is Result.Success)
        assertEquals("demo_file.jpg", result.getOrNull())
    }

    @Test
    fun `apply regex with preserveExtension true keeps extension intact`() = runTest {
        val rule = RegexRule(pattern = "\\d+", replacement = "")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo123.jpg",
            regexRule = rule,
            preserveExtension = true
        )
        
        val result = useCase(params)
        
        assertEquals("photo.jpg", result.getOrNull())
    }

    @Test
    fun `apply regex with preserveExtension false transforms entire filename`() = runTest {
        val rule = RegexRule(pattern = "\\d+", replacement = "")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo123.jpg",
            regexRule = rule,
            preserveExtension = false
        )
        
        val result = useCase(params)
        
        assertEquals("photo.jpg", result.getOrNull()) // Extension treated as part of name
    }

    // ============================================================
    // Preset Application Tests
    // ============================================================

    @Test
    fun `apply REMOVE_SPACES preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my vacation photo.jpg",
            regexRule = RegexPreset.REMOVE_SPACES.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("myvacationphoto.jpg", result.getOrNull())
    }

    @Test
    fun `apply SPACES_TO_UNDERSCORES preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my vacation photo.jpg",
            regexRule = RegexPreset.SPACES_TO_UNDERSCORES.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("my_vacation_photo.jpg", result.getOrNull())
    }

    @Test
    fun `apply SPACES_TO_HYPHENS preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my vacation photo.jpg",
            regexRule = RegexPreset.SPACES_TO_HYPHENS.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("my-vacation-photo.jpg", result.getOrNull())
    }

    @Test
    fun `apply REMOVE_SPECIAL_CHARS preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo@#!2024.jpg",
            regexRule = RegexPreset.REMOVE_SPECIAL_CHARS.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("photo2024.jpg", result.getOrNull())
    }

    @Test
    fun `apply REMOVE_NUMBERS preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo123abc456.jpg",
            regexRule = RegexPreset.REMOVE_NUMBERS.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("photoabc.jpg", result.getOrNull())
    }

    @Test
    fun `apply REMOVE_PARENTHESES preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo (copy).jpg",
            regexRule = RegexPreset.REMOVE_PARENTHESES.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("photo .jpg", result.getOrNull())
    }

    @Test
    fun `apply REMOVE_BRACKETS preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo [edited].jpg",
            regexRule = RegexPreset.REMOVE_BRACKETS.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("photo .jpg", result.getOrNull())
    }

    @Test
    fun `apply TRIM_SPACES preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "  photo  .jpg",
            regexRule = RegexPreset.TRIM_SPACES.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("photo.jpg", result.getOrNull())
    }

    @Test
    fun `apply COLLAPSE_SPACES preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my    vacation    photo.jpg",
            regexRule = RegexPreset.COLLAPSE_SPACES.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("my vacation photo.jpg", result.getOrNull())
    }

    @Test
    fun `apply REMOVE_LEADING_ZEROS preset`() = runTest {
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "image0042.jpg",
            regexRule = RegexPreset.REMOVE_LEADING_ZEROS.toRegexRule()
        )
        
        val result = useCase(params)
        
        assertEquals("image42.jpg", result.getOrNull())
    }

    // ============================================================
    // Regex Flags Tests
    // ============================================================

    @Test
    fun `apply regex with IGNORE_CASE flag`() = runTest {
        val rule = RegexRule(
            pattern = "photo",
            replacement = "image",
            flags = setOf(RegexFlag.IGNORE_CASE)
        )
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "PHOTO123.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("image123.jpg", result.getOrNull())
    }

    @Test
    fun `apply regex without IGNORE_CASE flag is case-sensitive`() = runTest {
        val rule = RegexRule(pattern = "photo", replacement = "image")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "PHOTO123.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("PHOTO123.jpg", result.getOrNull()) // No match
    }

    // ============================================================
    // Advanced Regex Patterns Tests
    // ============================================================

    @Test
    fun `apply capture group replacement`() = runTest {
        val rule = RegexRule(pattern = "(\\d+)_(\\d+)", replacement = "$2-$1")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo_123_456.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("photo_456-123.jpg", result.getOrNull())
    }

    @Test
    fun `apply lookahead pattern`() = runTest {
        val rule = RegexRule(pattern = "\\d+(?=\\.jpg)", replacement = "000")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo123.jpg",
            regexRule = rule,
            preserveExtension = false
        )
        
        val result = useCase(params)
        
        assertEquals("photo000.jpg", result.getOrNull())
    }

    @Test
    fun `apply multiple occurrences replacement`() = runTest {
        val rule = RegexRule(pattern = "_", replacement = "-")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my_vacation_photo_2024.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("my-vacation-photo-2024.jpg", result.getOrNull())
    }

    // ============================================================
    // Edge Cases and Error Handling Tests
    // ============================================================

    @Test
    fun `apply regex resulting in empty filename returns error`() = runTest {
        val rule = RegexRule(pattern = ".+", replacement = "")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertTrue("Result should be error", result is Result.Error)
        assertTrue(
            "Error should mention empty filename",
            result.errorOrNull()?.message?.contains("empty filename") == true
        )
    }

    @Test
    fun `apply invalid regex pattern returns error`() = runTest {
        val rule = RegexRule(pattern = "[invalid", replacement = "test") // Unclosed bracket
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertTrue("Result should be error", result is Result.Error)
    }

    @Test
    fun `apply regex to filename without extension`() = runTest {
        val rule = RegexRule(pattern = "\\d+", replacement = "")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo123",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("photo", result.getOrNull())
    }

    @Test
    fun `apply regex to filename with multiple dots`() = runTest {
        val rule = RegexRule(pattern = "\\d+", replacement = "")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my.file.123.tar.gz",
            regexRule = rule,
            preserveExtension = true
        )
        
        val result = useCase(params)
        
        // Only last extension preserved
        assertEquals("my.file..tar.gz", result.getOrNull())
    }

    @Test
    fun `apply regex that matches nothing returns original filename`() = runTest {
        val rule = RegexRule(pattern = "xyz", replacement = "abc")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "photo.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("photo.jpg", result.getOrNull())
    }

    // ============================================================
    // Complex Real-World Scenarios Tests
    // ============================================================

    @Test
    fun `chain multiple preset effects - remove spaces and special chars`() = runTest {
        // First remove special chars
        val rule1 = RegexPreset.REMOVE_SPECIAL_CHARS.toRegexRule()
        val params1 = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "my photo@2024!.jpg",
            regexRule = rule1
        )
        val result1 = useCase(params1)
        
        // Then remove spaces
        val rule2 = RegexPreset.REMOVE_SPACES.toRegexRule()
        val params2 = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = result1.getOrNull()!!,
            regexRule = rule2
        )
        val result2 = useCase(params2)
        
        assertEquals("myphoto2024.jpg", result2.getOrNull())
    }

    @Test
    fun `sanitize filename for web use`() = runTest {
        // Remove special chars, replace spaces with hyphens
        val rule1 = RegexPreset.REMOVE_SPECIAL_CHARS.toRegexRule()
        val params1 = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "My Vacation Photo #1 (2024).jpg",
            regexRule = rule1
        )
        val result1 = useCase(params1)
        
        val rule2 = RegexPreset.SPACES_TO_HYPHENS.toRegexRule()
        val params2 = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = result1.getOrNull()!!,
            regexRule = rule2
        )
        val result2 = useCase(params2)
        
        assertEquals("My-Vacation-Photo-1-2024.jpg", result2.getOrNull())
    }

    @Test
    fun `normalize sequential numbering`() = runTest {
        val rule = RegexRule(pattern = "IMG_(\\d+)", replacement = "Photo_$1")
        val params = ApplyRegexPatternUseCase.ApplyRegexParams(
            filename = "IMG_0042.jpg",
            regexRule = rule
        )
        
        val result = useCase(params)
        
        assertEquals("Photo_0042.jpg", result.getOrNull())
    }
}
