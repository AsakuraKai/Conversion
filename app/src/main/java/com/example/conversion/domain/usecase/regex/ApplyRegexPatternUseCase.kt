package com.example.conversion.domain.usecase.regex

import com.example.conversion.domain.model.RegexRule
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for applying regex pattern transformations to filenames.
 *
 * Applies a regex rule to transform filename according to specified pattern
 * and replacement. Handles regex errors gracefully and returns descriptive
 * error messages.
 *
 * Input: ApplyRegexParams (filename and regex rule)
 * Output: Result<String> (transformed filename)
 */
class ApplyRegexPatternUseCase @Inject constructor() : 
    BaseUseCase<ApplyRegexPatternUseCase.ApplyRegexParams, String>(Dispatchers.Default) {
    
    /**
     * Parameters for applying regex pattern.
     *
     * @property filename The filename to transform (without path)
     * @property regexRule The regex rule to apply
     * @property preserveExtension If true, only transform the base name and keep extension intact
     */
    data class ApplyRegexParams(
        val filename: String,
        val regexRule: RegexRule,
        val preserveExtension: Boolean = true
    )
    
    override suspend fun execute(params: ApplyRegexParams): com.example.conversion.domain.model.Result<String> {
        return try {
            // Validate the regex rule first
            val validationResult = params.regexRule.validate()
            if (validationResult is com.example.conversion.domain.model.Result.Error) {
                return validationResult
            }
            
            // Separate filename and extension if needed
            val (baseName, extension) = if (params.preserveExtension) {
                separateExtension(params.filename)
            } else {
                params.filename to ""
            }
            
            // Apply regex transformation
            val regex = params.regexRule.pattern.toRegex(params.regexRule.buildRegexOptions())
            val transformedBaseName = baseName.replace(regex, params.regexRule.replacement)
            
            // Reconstruct filename
            val transformedFilename = if (extension.isNotEmpty()) {
                "$transformedBaseName.$extension"
            } else {
                transformedBaseName
            }
            
            // Validate result is not empty
            if (transformedFilename.isEmpty() || transformedFilename == ".") {
                return com.example.conversion.domain.model.Result.Error(
                    IllegalArgumentException("Regex transformation resulted in empty filename")
                )
            }
            
            com.example.conversion.domain.model.Result.Success(transformedFilename)
        } catch (e: Exception) {
            com.example.conversion.domain.model.Result.Error(
                IllegalArgumentException("Failed to apply regex pattern: ${e.message}", e)
            )
        }
    }
    
    /**
     * Separates filename into base name and extension.
     *
     * @param filename The complete filename
     * @return Pair of (baseName, extension) where extension is without the dot
     */
    private fun separateExtension(filename: String): Pair<String, String> {
        val lastDotIndex = filename.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < filename.length - 1) {
            val baseName = filename.substring(0, lastDotIndex)
            val extension = filename.substring(lastDotIndex + 1)
            baseName to extension
        } else {
            filename to ""
        }
    }
}
