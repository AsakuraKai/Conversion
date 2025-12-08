package com.example.conversion.domain.usecase.template

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for saving a rename template.
 * Validates the template before saving.
 *
 * Input: RenameTemplate - The template to save
 * Output: Unit - Success or failure
 */
class SaveTemplateUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<RenameTemplate, Unit>(dispatcher) {

    /**
     * Executes the use case to save a template.
     *
     * @param params RenameTemplate to save
     * @return Unit on success
     * @throws IllegalArgumentException if template is invalid
     */
    override suspend fun execute(params: RenameTemplate): Unit {
        // Validate template
        if (!params.isValid()) {
            throw IllegalArgumentException("Invalid template: Name, pattern, and config must be valid")
        }

        if (params.name.length > RenameTemplate.MAX_NAME_LENGTH) {
            throw IllegalArgumentException("Template name too long (max ${RenameTemplate.MAX_NAME_LENGTH} characters)")
        }

        if (params.pattern.length > RenameTemplate.MAX_PATTERN_LENGTH) {
            throw IllegalArgumentException("Pattern description too long (max ${RenameTemplate.MAX_PATTERN_LENGTH} characters)")
        }

        // Save through repository
        return when (val result = templateRepository.saveTemplate(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
