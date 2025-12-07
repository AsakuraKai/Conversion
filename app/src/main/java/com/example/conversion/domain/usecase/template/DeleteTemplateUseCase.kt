package com.example.conversion.domain.usecase.template

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.TemplateRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for deleting a template by its ID.
 *
 * Input: String - The template ID to delete
 * Output: Unit - Success or failure
 */
class DeleteTemplateUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, Unit>(dispatcher) {

    /**
     * Executes the use case to delete a template.
     *
     * @param params The ID of the template to delete
     * @return Unit on success
     * @throws IllegalArgumentException if template ID is blank
     */
    override suspend fun execute(params: String): Unit {
        if (params.isBlank()) {
            throw IllegalArgumentException("Template ID cannot be blank")
        }

        return when (val result = templateRepository.deleteTemplate(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
