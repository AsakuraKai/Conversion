package com.example.conversion.domain.usecase.template

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for retrieving a specific template by its ID.
 *
 * Input: String - The template ID
 * Output: RenameTemplate? - The template if found, null otherwise
 */
class GetTemplateByIdUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, RenameTemplate?>(dispatcher) {

    /**
     * Executes the use case to get a template by ID.
     *
     * @param params The template ID
     * @return The template if found, null otherwise
     * @throws IllegalArgumentException if template ID is blank
     */
    override suspend fun execute(params: String): RenameTemplate? {
        if (params.isBlank()) {
            throw IllegalArgumentException("Template ID cannot be blank")
        }

        return when (val result = templateRepository.getTemplateById(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
