package com.example.conversion.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * UI Performance optimization utilities and patterns for Jetpack Compose.
 *
 * This file provides recomposition optimization techniques:
 * - Stable annotations for state classes
 * - DerivedStateOf for computed values
 * - Remember blocks for expensive calculations
 * - Key usage patterns for LazyColumn items
 *
 * @author Sokchea (UI/Frontend Specialist)
 */

/**
 * Marker annotation to indicate a class is stable for Compose recomposition.
 * Use this on state classes that don't change frequently or have immutable properties.
 *
 * Example:
 * ```
 * @Stable
 * data class FileListState(
 *     val files: List<FileItem>,
 *     val selectedCount: Int
 * )
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class StableState

/**
 * Examples of optimized state classes using @Stable annotation.
 */

@Stable
data class OptimizedFileListState(
    val files: List<FileItem> = emptyList(),
    val selectedFiles: Set<String> = emptySet(),
    val isLoading: Boolean = false
) {
    // Derived properties using computed values
    val selectedCount: Int get() = selectedFiles.size
    val hasSelection: Boolean get() = selectedFiles.isNotEmpty()
    val isEmpty: Boolean get() = files.isEmpty() && !isLoading
}

@Stable
data class FileItem(
    val id: String,
    val name: String,
    val size: Long,
    val path: String
)

/**
 * Performance optimization utilities for Compose UI.
 */
object RecompositionOptimizer {
    
    /**
     * Creates a derived state for computed values that depend on other state.
     * Only recomputes when dependencies change.
     *
     * Example:
     * ```
     * val filteredFiles by rememberDerivedState(files, filter) {
     *     files.filter { it.matches(filter) }
     * }
     * ```
     */
    @Composable
    fun <T, R> rememberDerivedState(
        key1: T,
        calculation: (T) -> R
    ): R {
        return remember(key1) {
            derivedStateOf { calculation(key1) }
        }.value
    }
    
    /**
     * Creates a derived state with two dependencies.
     */
    @Composable
    fun <T1, T2, R> rememberDerivedState(
        key1: T1,
        key2: T2,
        calculation: (T1, T2) -> R
    ): R {
        return remember(key1, key2) {
            derivedStateOf { calculation(key1, key2) }
        }.value
    }
    
    /**
     * Remembers an expensive calculation result.
     * Only recalculates when keys change.
     *
     * Example:
     * ```
     * val sortedFiles = rememberExpensiveCalculation(files, sortOrder) {
     *     files.sortedWith(sortOrder.comparator)
     * }
     * ```
     */
    @Composable
    fun <T> rememberExpensiveCalculation(
        vararg keys: Any?,
        calculation: () -> T
    ): T {
        return remember(*keys) { calculation() }
    }
}

/**
 * Modifier extensions for performance optimization.
 */
object OptimizedModifiers {
    
    /**
     * Creates a cached modifier that only rebuilds when dependencies change.
     *
     * Example:
     * ```
     * modifier = Modifier.cachedModifier(isSelected) {
     *     if (isSelected) background(Color.Blue) else Modifier
     * }
     * ```
     */
    @Composable
    fun Modifier.cachedModifier(
        vararg keys: Any?,
        block: () -> Modifier
    ): Modifier {
        val cachedModifier = remember(*keys) { block() }
        return this.then(cachedModifier)
    }
}

/**
 * Performance measurement utilities for Compose.
 */
object CompositionTracker {
    
    private var recompositionCount = 0
    
    /**
     * Tracks and logs recomposition count for debugging.
     * Use this to identify components that recompose too frequently.
     *
     * Example:
     * ```
     * @Composable
     * fun MyComponent() {
     *     trackRecomposition("MyComponent")
     *     // ... component content
     * }
     * ```
     */
    @Composable
    fun trackRecomposition(tag: String) {
        remember {
            recompositionCount++
            println("[$tag] Recomposition #$recompositionCount")
            recompositionCount
        }
    }
    
    /**
     * Resets the recomposition counter.
     */
    fun resetCounter() {
        recompositionCount = 0
    }
}

/**
 * Best practices for LazyColumn/Grid optimization.
 */
object LazyListOptimization {
    
    /**
     * Creates a stable key for LazyColumn items.
     * Always provide unique keys for better performance.
     *
     * Example:
     * ```
     * LazyColumn {
     *     items(
     *         items = files,
     *         key = { LazyListOptimization.itemKey(it) }
     *     ) { file ->
     *         FileItemCard(file)
     *     }
     * }
     * ```
     */
    fun itemKey(item: FileItem): String = item.id
    
    /**
     * Creates a content type for better recycling.
     *
     * Example:
     * ```
     * LazyColumn {
     *     items(
     *         items = files,
     *         key = { it.id },
     *         contentType = { LazyListOptimization.contentType(it) }
     *     ) { file ->
     *         FileItemCard(file)
     *     }
     * }
     * ```
     */
    fun contentType(item: FileItem): String = "file_item"
}

/**
 * Mock data for testing performance optimizations.
 */
object MockPerformanceData {
    
    fun generateMockFiles(count: Int = 1000): List<FileItem> {
        return List(count) { index ->
            FileItem(
                id = "file_$index",
                name = "File_${index}.jpg",
                size = (1024 * 1024 * (index % 10 + 1)).toLong(),
                path = "/storage/emulated/0/Pictures/File_${index}.jpg"
            )
        }
    }
    
    fun createMockState(fileCount: Int = 100): OptimizedFileListState {
        return OptimizedFileListState(
            files = generateMockFiles(fileCount),
            selectedFiles = emptySet(),
            isLoading = false
        )
    }
}

/**
 * Performance optimization checklist documentation.
 */
object PerformanceChecklist {
    
    const val OPTIMIZATION_GUIDE = """
    ## UI Performance Optimization Checklist
    
    ### Recomposition Optimization
    ✅ Use @Stable annotation on state classes
    ✅ Use derivedStateOf for computed values
    ✅ Use remember {} for expensive calculations
    ✅ Avoid creating new objects in composition
    ✅ Use remember(key) to control recomposition scope
    
    ### LazyColumn/Grid Optimization
    ✅ Always provide unique keys for items
    ✅ Use contentType for better recycling
    ✅ Avoid nested scrollable layouts
    ✅ Add proper content padding
    ✅ Use Modifier.fillParentMaxWidth() for items
    
    ### Modifier Optimization
    ✅ Chain modifiers efficiently (order matters)
    ✅ Avoid creating modifiers in loops
    ✅ Use remember {} for complex modifiers
    ✅ Reuse common modifier chains
    
    ### Animation Optimization
    ✅ Use animateContentSize sparingly
    ✅ Prefer AnimatedVisibility over manual animations
    ✅ Use updateTransition for complex animations
    ✅ Avoid animations on large lists
    
    ### Memory Optimization
    ✅ Don't hold references to Activity/Context
    ✅ Use rememberCoroutineScope for scoped operations
    ✅ Cancel flows in DisposableEffect
    ✅ Avoid memory leaks with ViewModel scope
    
    ### Layout Optimization
    ✅ Use Layout Inspector to find overdraw
    ✅ Minimize layout depth
    ✅ Use intrinsic measurements sparingly
    ✅ Test on low-end devices
    
    ### Image Loading Optimization
    ✅ Configure Coil with proper cache sizes
    ✅ Load thumbnails with size restrictions
    ✅ Use placeholder and error images
    ✅ Enable disk cache for remote images
    """
    
    fun printChecklist() {
        println(OPTIMIZATION_GUIDE)
    }
}
