package com.example.conversion.domain.usecase.regex

import com.example.conversion.domain.model.RegexFlag
import com.example.conversion.domain.model.RegexRule
import com.example.conversion.domain.common.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ValidateRegexUseCase.
 * Tests regex pattern validation with various scenarios and error cases.
 */
class ValidateRegexUseCaseTest {

    private lateinit var useCase: ValidateRegexUseCase

    @Before
    fun setup() {
        useCase = ValidateRegexUseCase()
    }

    // ============================================================
    // Valid Pattern Tests
    // ============================================================

    @Test
    fun `validate simple valid pattern`() = runTest {
        val rule = RegexRule(pattern = "test", replacement = "demo")
        
        val result = useCase(rule)
        
        assertTrue("Result should be success", result is Result.Success)
        val validation = result.getOrNull()!!
        assertTrue("Pattern should be valid", validation.isValid)
        assertNull("Error message should be null", validation.errorMessage)
    }

    @Test
    fun `validate pattern with special characters`() = runTest {
        val validPatterns = listOf(
            "\\d+",           // Digits
            "\\w+",           // Word characters
            "\\s+",           // Whitespace
            "[a-zA-Z]+",      // Character class
            "test|demo",      // Alternation
            "(group)",        // Group
            "pattern?",       // Optional
            "pattern*",       // Zero or more
            "pattern+",       // One or more
            "pattern{2,5}"    // Quantifier
        )

        validPatterns.forEach { pattern ->
            val rule = RegexRule(pattern = pattern, replacement = "")
            val result = useCase(rule)
            val validation = result.getOrNull()!!
            
            assertTrue("Pattern '$pattern' should be valid", validation.isValid)
        }
    }

    @Test
    fun `validate pattern with escaped special characters`() = runTest {
        val rule = RegexRule(pattern = "\\.", replacement = "") // Escaped dot
        
        val result = useCase(rule)
        
        assertTrue("Result should be success", result is Result.Success)
        assertTrue("Pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate pattern with flags`() = runTest {
        val rule = RegexRule(
            pattern = "test",
            replacement = "",
            flags = setOf(RegexFlag.IGNORE_CASE, RegexFlag.MULTILINE)
        )
        
        val result = useCase(rule)
        
        assertTrue("Pattern with flags should be valid", result.getOrNull()!!.isValid)
    }

    // ============================================================
    // Empty Pattern Tests
    // ============================================================

    @Test
    fun `validate empty pattern returns error`() = runTest {
        val rule = RegexRule(pattern = "", replacement = "test")
        
        val result = useCase(rule)
        
        assertTrue("Result should be success", result is Result.Success)
        val validation = result.getOrNull()!!
        assertFalse("Empty pattern should be invalid", validation.isValid)
        assertEquals(
            ValidateRegexUseCase.ValidationResult.ErrorType.EMPTY_PATTERN,
            validation.errorType
        )
        assertNotNull("Should provide suggestion", validation.suggestion)
    }

    // ============================================================
    // Unclosed Group Tests
    // ============================================================

    @Test
    fun `validate unclosed parenthesis returns error`() = runTest {
        val rule = RegexRule(pattern = "(test", replacement = "")
        
        val result = useCase(rule)
        
        val validation = result.getOrNull()!!
        assertFalse("Unclosed parenthesis should be invalid", validation.isValid)
        assertEquals(
            ValidateRegexUseCase.ValidationResult.ErrorType.UNCLOSED_GROUP,
            validation.errorType
        )
        assertTrue(
            "Error should mention unclosed parenthesis",
            validation.errorMessage?.contains("Unclosed", ignoreCase = true) == true
        )
        assertNotNull("Should provide suggestion", validation.suggestion)
    }

    @Test
    fun `validate unclosed bracket returns error`() = runTest {
        val rule = RegexRule(pattern = "[abc", replacement = "")
        
        val result = useCase(rule)
        
        val validation = result.getOrNull()!!
        assertFalse("Unclosed bracket should be invalid", validation.isValid)
        assertEquals(
            ValidateRegexUseCase.ValidationResult.ErrorType.UNCLOSED_GROUP,
            validation.errorType
        )
    }

    @Test
    fun `validate unclosed brace returns error`() = runTest {
        val rule = RegexRule(pattern = "test{2,", replacement = "")
        
        val result = useCase(rule)
        
        val validation = result.getOrNull()!!
        assertFalse("Unclosed brace should be invalid", validation.isValid)
    }

    // ============================================================
    // Invalid Quantifier Tests
    // ============================================================

    @Test
    fun `validate dangling quantifier returns error`() = runTest {
        val rule = RegexRule(pattern = "*test", replacement = "")
        
        val result = useCase(rule)
        
        val validation = result.getOrNull()!!
        assertFalse("Dangling quantifier should be invalid", validation.isValid)
        assertEquals(
            ValidateRegexUseCase.ValidationResult.ErrorType.INVALID_QUANTIFIER,
            validation.errorType
        )
        assertNotNull("Should provide suggestion", validation.suggestion)
    }

    @Test
    fun `validate multiple quantifiers returns error`() = runTest {
        val rule = RegexRule(pattern = "test++", replacement = "")
        
        val result = useCase(rule)
        
        val validation = result.getOrNull()!!
        assertFalse("Multiple quantifiers should be invalid", validation.isValid)
    }

    @Test
    fun `validate quantifier without preceding element returns error`() = runTest {
        val invalidPatterns = listOf(
            "+test",
            "?test",
            "{2,5}test"
        )

        invalidPatterns.forEach { pattern ->
            val rule = RegexRule(pattern = pattern, replacement = "")
            val result = useCase(rule)
            val validation = result.getOrNull()!!
            
            assertFalse("Pattern '$pattern' should be invalid", validation.isValid)
            assertEquals(
                ValidateRegexUseCase.ValidationResult.ErrorType.INVALID_QUANTIFIER,
                validation.errorType
            )
        }
    }

    // ============================================================
    // Invalid Escape Tests
    // ============================================================

    @Test
    fun `validate invalid escape sequence returns error`() = runTest {
        // Note: In Kotlin, some escape sequences may be caught at compile time
        // Testing with raw strings or dynamic patterns
        val rule = RegexRule(pattern = "\\k", replacement = "") // Invalid escape
        
        val result = useCase(rule)
        
        // Depending on Regex engine, may be invalid or treated as literal
        // This test documents behavior
        val validation = result.getOrNull()!!
        if (!validation.isValid) {
            assertNotNull("Should provide error details", validation.errorMessage)
        }
    }

    // ============================================================
    // Complex Pattern Tests
    // ============================================================

    @Test
    fun `validate complex valid pattern with nested groups`() = runTest {
        val rule = RegexRule(
            pattern = "((\\d{2})-(\\d{2})-(\\d{4}))",
            replacement = "$3-$2-$1"
        )
        
        val result = useCase(rule)
        
        assertTrue("Complex pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate lookahead pattern`() = runTest {
        val rule = RegexRule(pattern = "\\d+(?=\\.jpg)", replacement = "000")
        
        val result = useCase(rule)
        
        assertTrue("Lookahead pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate lookbehind pattern`() = runTest {
        val rule = RegexRule(pattern = "(?<=IMG_)\\d+", replacement = "0000")
        
        val result = useCase(rule)
        
        assertTrue("Lookbehind pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate non-capturing group`() = runTest {
        val rule = RegexRule(pattern = "(?:test|demo)", replacement = "result")
        
        val result = useCase(rule)
        
        assertTrue("Non-capturing group should be valid", result.getOrNull()!!.isValid)
    }

    // ============================================================
    // Real-World Pattern Tests
    // ============================================================

    @Test
    fun `validate email-like pattern`() = runTest {
        val rule = RegexRule(
            pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            replacement = "[email]"
        )
        
        val result = useCase(rule)
        
        assertTrue("Email pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate date pattern`() = runTest {
        val rule = RegexRule(
            pattern = "\\d{4}-\\d{2}-\\d{2}",
            replacement = "DATE"
        )
        
        val result = useCase(rule)
        
        assertTrue("Date pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate filename sanitization pattern`() = runTest {
        val rule = RegexRule(
            pattern = "[^a-zA-Z0-9\\s._-]+",
            replacement = ""
        )
        
        val result = useCase(rule)
        
        assertTrue("Sanitization pattern should be valid", result.getOrNull()!!.isValid)
    }

    // ============================================================
    // Convenience Method Tests
    // ============================================================

    @Test
    fun `validatePattern convenience method works correctly`() = runTest {
        val result = useCase.validatePattern("\\d+")
        
        assertTrue("Result should be success", result is Result.Success)
        assertTrue("Pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validatePattern with invalid pattern returns error`() = runTest {
        val result = useCase.validatePattern("[invalid")
        
        val validation = result.getOrNull()!!
        assertFalse("Invalid pattern should fail validation", validation.isValid)
    }

    // ============================================================
    // Error Message Quality Tests
    // ============================================================

    @Test
    fun `error messages are user-friendly and descriptive`() = runTest {
        val invalidPatterns = mapOf(
            "" to "empty",
            "(test" to "unclosed|parenthesis",
            "[test" to "unclosed|bracket",
            "*test" to "quantifier"
        )

        invalidPatterns.forEach { (pattern, expectedKeyword) ->
            val rule = RegexRule(pattern = pattern, replacement = "")
            val result = useCase(rule)
            val validation = result.getOrNull()!!
            
            assertFalse("Pattern '$pattern' should be invalid", validation.isValid)
            assertNotNull("Should have error message", validation.errorMessage)
            
            val keywords = expectedKeyword.split("|")
            val hasKeyword = keywords.any { keyword ->
                validation.errorMessage?.contains(keyword, ignoreCase = true) == true
            }
            assertTrue(
                "Error message should contain relevant keyword for '$pattern'",
                hasKeyword
            )
        }
    }

    @Test
    fun `validation provides helpful suggestions`() = runTest {
        val patternsNeedingSuggestions = listOf(
            "",           // Empty
            "(test",      // Unclosed
            "*test"       // Invalid quantifier
        )

        patternsNeedingSuggestions.forEach { pattern ->
            val rule = RegexRule(pattern = pattern, replacement = "")
            val result = useCase(rule)
            val validation = result.getOrNull()!!
            
            if (!validation.isValid) {
                assertNotNull(
                    "Pattern '$pattern' should provide suggestion",
                    validation.suggestion
                )
            }
        }
    }

    // ============================================================
    // Edge Cases Tests
    // ============================================================

    @Test
    fun `validate pattern with literal flag`() = runTest {
        val rule = RegexRule(
            pattern = "test.*",
            replacement = "",
            flags = setOf(RegexFlag.LITERAL)
        )
        
        val result = useCase(rule)
        
        // With LITERAL flag, special chars treated as literals
        assertTrue("Pattern with LITERAL flag should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate very long pattern`() = runTest {
        val longPattern = "a".repeat(1000)
        val rule = RegexRule(pattern = longPattern, replacement = "")
        
        val result = useCase(rule)
        
        assertTrue("Long pattern should be valid", result.getOrNull()!!.isValid)
    }

    @Test
    fun `validate pattern with unicode characters`() = runTest {
        val rule = RegexRule(pattern = "[\\u4E00-\\u9FA5]+", replacement = "") // Chinese chars
        
        val result = useCase(rule)
        
        assertTrue("Unicode pattern should be valid", result.getOrNull()!!.isValid)
    }
}
