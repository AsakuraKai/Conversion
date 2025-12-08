package com.example.conversion.domain.usecase.template

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for retrieving all saved templates.
 * Returns templates sorted by creation date (newest first).
 *
 * Input: Unit - No parameters needed
 * Output: List<RenameTemplate> - All saved templates
 */
class GetTemplatesUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, List<RenameTemplate>>(dispatcher) {

    /**
     * Executes the use case to retrieve all templates.
     *
     * @param params Unit (no parameters)
     * @return List of all saved templates, sorted by creation date
     */
    override suspend fun execute(params: Unit): List<RenameTemplate> {
        return when (val result = templateRepository.getTemplates()) {
            is Result.Success -> result.data.sortedByDescending { it.createdAt }
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
