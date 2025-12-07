package com.example.conversion.domain.usecase.ai

import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.domain.repository.MLRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for generating intelligent filename suggestions from image labels.
 * 
 * Takes detected labels and combines them into meaningful, human-readable filenames.
 * Applies smart naming strategies like combining related labels, handling duplicates,
 * and creating natural-sounding file names.
 *
 * Example input: [ImageLabel("sunset", 0.9), ImageLabel("beach", 0.85), ImageLabel("ocean", 0.8)]
 * Example output: ["sunset_beach", "beach_sunset", "ocean_sunset", "beach_ocean", "sunset_ocean"]
 *
 * @property mlRepository Repository providing ML operations
 * @property dispatcher Coroutine dispatcher for background execution
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
class GenerateSuggestionsUseCase @Inject constructor(
    private val mlRepository: MLRepository,
    @com.example.conversion.di.IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<GenerateSuggestionsUseCase.Params, List<String>>(dispatcher) {
    
    /**
     * Parameters for filename suggestion generation.
     *
     * @property labels List of detected image labels to use for suggestions
     * @property maxSuggestions Maximum number of suggestions to generate (default: 5)
     * @property allowSingleLabel Whether to allow suggestions with only one label (default: true)
     */
    data class Params(
        val labels: List<ImageLabel>,
        val maxSuggestions: Int = 5,
        val allowSingleLabel: Boolean = true
    ) {
        init {
            require(labels.isNotEmpty()) {
                "Labels list cannot be empty"
            }
            require(maxSuggestions > 0) {
                "Max suggestions must be greater than 0"
            }
        }
    }
    
    /**
     * Executes filename suggestion generation.
     * 
     * @param params Suggestion generation parameters
     * @return List of suggested filenames, sorted by relevance
     * @throws Exception if suggestion generation fails
     */
    override suspend fun execute(params: Params): List<String> {
        return when (val result = mlRepository.generateFilenameSuggestions(
            labels = params.labels,
            maxSuggestions = params.maxSuggestions
        )) {
            is com.example.conversion.domain.common.Result.Success -> {
                val suggestions = result.data
                
                // Filter out single-label suggestions if not allowed
                if (params.allowSingleLabel) {
                    suggestions
                } else {
                    suggestions.filter { it.contains("_") }
                }.take(params.maxSuggestions)
            }
            is com.example.conversion.domain.common.Result.Error -> {
                throw result.exception
            }
            is com.example.conversion.domain.common.Result.Loading -> {
                throw IllegalStateException("Unexpected Loading state")
            }
        }
    }
    
    companion object {
        /**
         * Default maximum number of suggestions to generate.
         */
        const val DEFAULT_MAX_SUGGESTIONS = 5
        
        /**
         * Maximum number of suggestions the system can generate.
         */
        const val MAX_SUGGESTIONS_LIMIT = 20
    }
}
