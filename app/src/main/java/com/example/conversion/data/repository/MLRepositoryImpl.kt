package com.example.conversion.data.repository

import android.content.Context
import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.domain.repository.MLRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PRODUCTION IMPLEMENTATION: ML Kit Image Labeling for AI-powered filename suggestions.
 * 
 * Upgraded from mock implementation to real Google ML Kit integration.
 * Provides on-device image analysis with 400+ label categories.
 * 
 * **Features:**
 * ✅ Real ML Kit Image Labeling API
 * ✅ On-device processing (privacy-friendly, works offline)
 * ✅ 400+ label categories with confidence scores
 * ✅ Smart filename suggestion generation
 * ✅ Comprehensive error handling
 * ✅ Efficient resource management
 * 
 * **Improvements over mock:**
 * ✅ Real AI-powered image analysis
 * ✅ Accurate object/scene detection
 * ✅ Contextual label categories
 * ✅ Continuous model improvements from Google
 * ✅ Professional-grade confidence scores
 * 
 * Upgraded from: MOCK_IMPLEMENTATIONS.md - CHUNK 13
 *
 * @property context Application context for image loading
 * @property ioDispatcher Coroutine dispatcher for background operations
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions (Production)
 */
@Singleton
class MLRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @com.example.conversion.di.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MLRepository {
    
    /**
     * ML Kit image labeler instance.
     * Configured with default options (confidence threshold handled in analyzeImage).
     */
    private val labeler: ImageLabeler by lazy {
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f) // Default threshold, filtered further in method
            .build()
        ImageLabeling.getClient(options)
    }
    
    /**
     * Analyzes an image using ML Kit Image Labeling.
     * 
     * **Production Behavior:** Uses Google ML Kit to detect objects, scenes, activities,
     * and concepts in the image. Returns labels with confidence scores 0.0-1.0.
     *
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for labels (0.0-1.0)
     * @param maxResults Maximum number of labels to return
     * @return Result containing ImageLabels detected by ML Kit
     */
    override suspend fun analyzeImage(
        imageUri: Uri,
        confidenceThreshold: Float,
        maxResults: Int
    ): Result<List<ImageLabel>> = withContext(ioDispatcher) {
        try {
            // Validate confidence threshold
            if (confidenceThreshold !in 0.0f..1.0f) {
                return@withContext Result.Error(
                    IllegalArgumentException("Confidence threshold must be between 0.0 and 1.0")
                )
            }
            
            // Load image from URI
            val inputImage = try {
                InputImage.fromFilePath(context, imageUri)
            } catch (e: Exception) {
                return@withContext Result.Error(
                    IllegalArgumentException("Failed to load image from URI: ${e.message}", e)
                )
            }
            
            // Process image with ML Kit
            val mlkitLabels = labeler.process(inputImage).await()
            
            // Convert ML Kit labels to domain ImageLabels
            val imageLabels = mlkitLabels
                .filter { it.confidence >= confidenceThreshold }
                .sortedByDescending { it.confidence }
                .take(maxResults)
                .map { mlLabel ->
                    ImageLabel(
                        text = mlLabel.text,
                        confidence = mlLabel.confidence,
                        category = inferCategory(mlLabel.text)
                    )
                }
            
            Result.Success(imageLabels)
        } catch (e: Exception) {
            Result.Error(
                Exception("ML Kit image analysis failed: ${e.message}", e)
            )
        }
    }
    
    /**
     * Generates filename suggestions from ML labels.
     * 
     * **Production Behavior:** Creates smart filename combinations using real ML labels,
     * considering confidence scores and semantic relationships.
     *
     * @param labels List of detected image labels from ML Kit
     * @param maxSuggestions Maximum number of suggestions to generate
     * @return Result containing suggested filenames
     */
    override suspend fun generateFilenameSuggestions(
        labels: List<ImageLabel>,
        maxSuggestions: Int
    ): Result<List<String>> = withContext(ioDispatcher) {
        try {
            if (labels.isEmpty()) {
                return@withContext Result.Success(emptyList())
            }
            
            val suggestions = mutableSetOf<String>()
            
            // Strategy 1: Single high-confidence labels
            labels.take(3).forEach { label ->
                suggestions.add(sanitizeForFilename(label.text))
            }
            
            // Strategy 2: Two-word combinations (semantic pairs)
            for (i in labels.indices) {
                for (j in i + 1 until minOf(i + 3, labels.size)) {
                    val label1 = sanitizeForFilename(labels[i].text)
                    val label2 = sanitizeForFilename(labels[j].text)
                    
                    // Add both orders, prioritizing higher confidence first
                    if (labels[i].confidence >= labels[j].confidence) {
                        suggestions.add("${label1}_${label2}")
                    } else {
                        suggestions.add("${label2}_${label1}")
                    }
                }
            }
            
            // Strategy 3: Three-word combinations for detailed naming
            if (labels.size >= 3) {
                val top3 = labels.take(3).map { sanitizeForFilename(it.text) }
                suggestions.add(top3.joinToString("_"))
            }
            
            // Strategy 4: Category-prefixed names (if category detected)
            if (labels.isNotEmpty()) {
                val topLabel = sanitizeForFilename(labels.first().text)
                labels.first().category?.let { category ->
                    when (category.lowercase()) {
                        "nature", "landscape" -> suggestions.add("scenic_$topLabel")
                        "activity", "sport" -> suggestions.add("action_$topLabel")
                        "food" -> suggestions.add("food_$topLabel")
                        "architecture", "building" -> suggestions.add("${topLabel}_scene")
                        "portrait", "person" -> suggestions.add("portrait_$topLabel")
                    }
                }
            }
            
            // Strategy 5: Descriptive combinations (high + medium confidence)
            if (labels.size >= 2) {
                val high = sanitizeForFilename(labels.first().text)
                labels.drop(1).take(2).forEach { label ->
                    val medium = sanitizeForFilename(label.text)
                    suggestions.add("${high}_with_${medium}")
                }
            }
            
            Result.Success(suggestions.take(maxSuggestions).toList())
        } catch (e: Exception) {
            Result.Error(
                Exception("Filename suggestion generation failed: ${e.message}", e)
            )
        }
    }
    
    /**
     * Infers a general category from the label text.
     * This provides additional context for filename generation.
     */
    private fun inferCategory(labelText: String): String? {
        val text = labelText.lowercase()
        return when {
            // Nature & Landscapes
            text in setOf("sunset", "sunrise", "beach", "ocean", "sea", "mountain", 
                "forest", "trees", "sky", "clouds", "landscape", "nature", "waterfall",
                "lake", "river", "desert", "field") -> "nature"
            
            // Architecture & Buildings
            text in setOf("building", "architecture", "city", "street", "urban",
                "bridge", "tower", "monument", "house", "skyscraper") -> "architecture"
            
            // People & Activities
            text in setOf("person", "people", "face", "portrait", "smile",
                "group", "crowd", "family") -> "portrait"
            
            // Sports & Activities
            text in setOf("sport", "sports", "running", "cycling", "swimming",
                "climbing", "hiking", "exercise", "fitness") -> "activity"
            
            // Food & Dining
            text in setOf("food", "meal", "dish", "cuisine", "dining",
                "restaurant", "breakfast", "lunch", "dinner") -> "food"
            
            // Animals
            text in setOf("animal", "pet", "dog", "cat", "bird", "wildlife",
                "fish", "horse", "insect") -> "animal"
            
            // Indoor & Objects
            text in setOf("indoor", "room", "furniture", "table", "chair",
                "bed", "decoration") -> "indoor"
            
            // Events & Celebrations
            text in setOf("party", "celebration", "event", "wedding",
                "birthday", "festival", "concert") -> "event"
            
            else -> null
        }
    }
    
    /**
     * Sanitizes label text for use in filenames.
     * Removes special characters, normalizes spacing, and handles Unicode.
     */
    private fun sanitizeForFilename(text: String): String {
        return text
            .lowercase()
            .replace(Regex("[^a-z0-9\\s]+"), "") // Remove special characters
            .trim()
            .replace(Regex("\\s+"), "_") // Replace spaces with underscores
            .take(50) // Limit length to avoid extremely long filenames
    }
    
    /**
     * Clean up resources when repository is destroyed.
     */
    fun close() {
        try {
            labeler.close()
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}
