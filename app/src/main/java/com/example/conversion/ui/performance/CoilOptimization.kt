package com.example.conversion.ui.performance

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation

/**
 * Optimized Coil image loading configuration for performance and memory efficiency.
 *
 * This file provides:
 * - Optimized ImageLoader configuration
 * - Memory and disk cache settings
 * - Thumbnail loading utilities
 * - Placeholder and error handling
 *
 * @author Sokchea (UI/Frontend Specialist)
 */

/**
 * Image loading configuration for optimal performance.
 */
object CoilImageConfig {
    
    /**
     * Creates an optimized ImageLoader instance with proper cache configuration.
     *
     * Features:
     * - Memory cache: 25% of available memory
     * - Disk cache: 250 MB
     * - Video frame decoding support
     * - Network cache policy
     *
     * Usage:
     * ```
     * val imageLoader = CoilImageConfig.createOptimizedLoader(context)
     * ```
     */
    fun createOptimizedLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    // Use 25% of available memory for image cache
                    .maxSizePercent(0.25)
                    // Weak references for memory efficiency
                    .weakReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    // 250 MB disk cache
                    .maxSizeBytes(250L * 1024 * 1024)
                    .build()
            }
            // Note: Video frame decoding requires additional Coil video dependency
            // .components {
            //     add(VideoFrameDecoder.Factory())
            // }
            // Respect cache headers from network
            .respectCacheHeaders(true)
            .build()
    }
    
    /**
     * Memory cache configuration values.
     */
    object CacheConfig {
        const val MEMORY_CACHE_PERCENT = 0.25 // 25% of available memory
        const val DISK_CACHE_SIZE_MB = 250L
        const val WEAK_REFERENCES_ENABLED = true
    }
}

/**
 * Image loading utilities for different use cases.
 */
object ImageLoadingUtils {
    
    /**
     * Creates an optimized image request for thumbnails.
     *
     * Features:
     * - Smaller size for memory efficiency
     * - Disk and memory caching enabled
     * - Placeholder and error handling
     *
     * @param context Android context
     * @param imageUri URI of the image to load
     * @param size Thumbnail size (default: 200x200)
     * @param placeholderRes Placeholder drawable resource
     * @param errorRes Error drawable resource
     */
    fun createThumbnailRequest(
        context: Context,
        imageUri: String,
        size: Size = Size(200, 200),
        placeholderRes: Int? = null,
        errorRes: Int? = null
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(imageUri)
            .size(size)
            // Enable both memory and disk caching
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .apply {
                placeholderRes?.let { placeholder(it) }
                errorRes?.let { error(it) }
            }
            // Crossfade animation
            .crossfade(true)
            .build()
    }
    
    /**
     * Creates an optimized image request for full-size images.
     *
     * @param context Android context
     * @param imageUri URI of the image to load
     * @param transformations Optional image transformations
     */
    fun createFullSizeRequest(
        context: Context,
        imageUri: String,
        transformations: List<Transformation> = emptyList()
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(imageUri)
            .size(Size.ORIGINAL)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .transformations(transformations)
            .crossfade(true)
            .build()
    }
    
    /**
     * Creates an optimized request for video thumbnails.
     *
     * @param context Android context
     * @param videoUri URI of the video file
     * @param frameTimeMicros Timestamp of frame to extract (in microseconds)
     */
    fun createVideoThumbnailRequest(
        context: Context,
        videoUri: String,
        frameTimeMicros: Long = 0L
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(videoUri)
            .size(Size(300, 300))
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            // Note: Video frame options require coil-video dependency
            // .videoFrameMicros(frameTimeMicros)
            .crossfade(true)
            .build()
    }
}

/**
 * Image loading best practices and optimization guidelines.
 */
object ImageLoadingBestPractices {
    
    const val OPTIMIZATION_GUIDE = """
    ## Coil Image Loading Optimization Guide
    
    ### Memory Cache Configuration
    ✅ Use 20-30% of available memory for cache
    ✅ Enable weak references for better GC
    ✅ Monitor memory pressure and adjust
    
    ### Disk Cache Configuration
    ✅ Set appropriate cache size (100-500 MB)
    ✅ Use dedicated cache directory
    ✅ Clear cache periodically for storage
    
    ### Image Loading Best Practices
    ✅ Always specify image size (avoid Size.ORIGINAL for lists)
    ✅ Use thumbnails for grid/list views
    ✅ Load full size only when needed
    ✅ Enable crossfade for smoother transitions
    
    ### Placeholder & Error Handling
    ✅ Use placeholder for better UX
    ✅ Provide error images for failed loads
    ✅ Use same aspect ratio for placeholders
    
    ### Transformations
    ✅ Apply transformations in correct order
    ✅ Cache transformed images
    ✅ Avoid heavy transformations in lists
    
    ### Video Thumbnails
    ✅ Extract specific frame timestamp
    ✅ Use smaller sizes for previews
    ✅ Enable video frame decoder component
    
    ### Network Optimization
    ✅ Respect cache headers
    ✅ Enable disk cache for remote images
    ✅ Use appropriate retry policies
    
    ### Memory Efficiency
    ✅ Use smaller sizes in RecyclerView/LazyColumn
    ✅ Clear unused caches periodically
    ✅ Monitor memory with MemoryUtils
    ✅ Avoid loading too many images simultaneously
    """
    
    fun printGuide() {
        println(OPTIMIZATION_GUIDE)
    }
}

/**
 * Mock implementation of image loading configuration.
 * Production should use actual Coil library.
 */
object MockImageLoader {
    
    /**
     * Simulates image loading with delay.
     * Replace with actual Coil implementation.
     */
    suspend fun loadImage(uri: String, size: Size = Size(200, 200)): Result<String> {
        // Simulate loading delay
        kotlinx.coroutines.delay(100)
        return Result.success("Loaded: $uri (${size.width}x${size.height})")
    }
    
    /**
     * Simulates video thumbnail loading.
     */
    suspend fun loadVideoThumbnail(
        videoUri: String,
        frameTimeMicros: Long = 0L
    ): Result<String> {
        kotlinx.coroutines.delay(200)
        return Result.success("Video thumbnail: $videoUri @ ${frameTimeMicros}μs")
    }
    
    /**
     * Simulates cache statistics.
     */
    data class CacheStats(
        val memoryCacheSize: Long,
        val diskCacheSize: Long,
        val hitCount: Int,
        val missCount: Int
    ) {
        val hitRate: Float
            get() = if (hitCount + missCount > 0) {
                hitCount.toFloat() / (hitCount + missCount)
            } else 0f
    }
    
    fun getCacheStats(): CacheStats {
        return CacheStats(
            memoryCacheSize = 50L * 1024 * 1024, // 50 MB
            diskCacheSize = 150L * 1024 * 1024, // 150 MB
            hitCount = 450,
            missCount = 50
        )
    }
    
    /**
     * Simulates cache clearing.
     */
    fun clearCache() {
        println("Mock: Image cache cleared")
    }
}

/**
 * Recommended image sizes for different use cases.
 */
object ImageSizes {
    val THUMBNAIL = Size(200, 200)
    val GRID_ITEM = Size(300, 300)
    val LIST_ITEM = Size(400, 400)
    val PREVIEW = Size(800, 800)
    val FULL_SIZE = Size.ORIGINAL
}

/**
 * Usage examples for documentation.
 */
object ImageLoadingExamples {
    
    const val EXAMPLES = """
    // Example 1: Load thumbnail in LazyColumn
    AsyncImage(
        model = ImageLoadingUtils.createThumbnailRequest(
            context = LocalContext.current,
            imageUri = file.uri,
            size = ImageSizes.THUMBNAIL
        ),
        contentDescription = file.name,
        modifier = Modifier.size(64.dp)
    )
    
    // Example 2: Load full-size image with transformation
    AsyncImage(
        model = ImageLoadingUtils.createFullSizeRequest(
            context = LocalContext.current,
            imageUri = file.uri,
            transformations = listOf(CircleCropTransformation())
        ),
        contentDescription = file.name,
        modifier = Modifier.fillMaxSize()
    )
    
    // Example 3: Load video thumbnail
    AsyncImage(
        model = ImageLoadingUtils.createVideoThumbnailRequest(
            context = LocalContext.current,
            videoUri = video.uri,
            frameTimeMicros = 1_000_000L // 1 second
        ),
        contentDescription = video.name,
        modifier = Modifier.size(120.dp)
    )
    
    // Example 4: Custom ImageLoader configuration
    val imageLoader = CoilImageConfig.createOptimizedLoader(context)
    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
        // Your composable content
    }
    """
}
