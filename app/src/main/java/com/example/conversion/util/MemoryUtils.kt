package com.example.conversion.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale

/**
 * Memory management utilities for tracking and optimizing memory usage.
 * Mock implementation for development.
 *
 * Production notes:
 * - Integrate with LeakCanary for real leak detection
 * - Use Android Profiler for actual memory measurements
 * - Implement proper lifecycle observers for automatic cleanup
 */
object MemoryUtils {

    private const val TAG = "MemoryUtils"
    private const val MB = 1024 * 1024

    /**
     * Get current memory usage information.
     * Mock implementation returns simulated values.
     *
     * Production: Use Runtime.getRuntime() for actual values.
     */
    fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()

        return MemoryInfo(
            usedMemoryMB = usedMemory / MB,
            totalMemoryMB = totalMemory / MB,
            maxMemoryMB = maxMemory / MB,
            freeMemoryMB = freeMemory / MB
        )
    }

    /**
     * Log current memory state.
     */
    fun logMemoryState(tag: String = TAG) {
        val info = getMemoryInfo()
        Log.d(tag, "Memory: ${info.usedMemoryMB}MB used / ${info.maxMemoryMB}MB max")
    }

    /**
     * Check if memory usage is approaching limit.
     * Returns true if using more than 80% of max memory.
     */
    fun isMemoryPressureHigh(): Boolean {
        val info = getMemoryInfo()
        val usagePercent = (info.usedMemoryMB.toFloat() / info.maxMemoryMB.toFloat()) * 100
        return usagePercent > 80f
    }

    /**
     * Suggest garbage collection when memory pressure is high.
     * Note: System.gc() is a hint, not a guarantee.
     */
    fun requestGarbageCollection() {
        if (isMemoryPressureHigh()) {
            Log.d(TAG, "High memory pressure detected, requesting GC")
            System.gc()
        }
    }

    /**
     * Create a weak reference to avoid memory leaks.
     * Useful for holding references to contexts, activities, etc.
     */
    fun <T : Any> T.toWeakReference(): WeakReference<T> {
        return WeakReference(this)
    }

    /**
     * Safely access weak reference with null handling.
     */
    inline fun <T : Any, R> WeakReference<T>.use(block: (T) -> R): R? {
        return get()?.let(block)
    }

    /**
     * Monitor memory usage periodically.
     * Returns a Job that can be cancelled to stop monitoring.
     *
     * Mock implementation for development.
     * Production: Integrate with Android Profiler.
     */
    fun startMemoryMonitoring(
        scope: CoroutineScope,
        intervalMillis: Long = 5000L,
        onMemoryUpdate: (MemoryInfo) -> Unit
    ): Job {
        return scope.launch {
            while (isActive) {
                val info = getMemoryInfo()
                onMemoryUpdate(info)
                
                if (isMemoryPressureHigh()) {
                    Log.w(TAG, "High memory pressure: ${info.usedMemoryMB}MB / ${info.maxMemoryMB}MB")
                }
                
                delay(intervalMillis)
            }
        }
    }

    /**
     * Cache manager to track and limit cache sizes.
     * Mock implementation.
     */
    class CacheManager(private val maxSizeMB: Long = 50) {
        private var currentSizeMB: Long = 0

        fun addToCache(sizeMB: Long): Boolean {
            return if (currentSizeMB + sizeMB <= maxSizeMB) {
                currentSizeMB += sizeMB
                true
            } else {
                Log.w(TAG, "Cache full: ${currentSizeMB}MB / ${maxSizeMB}MB")
                false
            }
        }

        fun removeFromCache(sizeMB: Long) {
            currentSizeMB = maxOf(0, currentSizeMB - sizeMB)
        }

        fun clearCache() {
            currentSizeMB = 0
            Log.d(TAG, "Cache cleared")
        }

        fun getCacheUsagePercent(): Float {
            return (currentSizeMB.toFloat() / maxSizeMB.toFloat()) * 100f
        }
    }
}

/**
 * Data class representing memory information.
 */
data class MemoryInfo(
    val usedMemoryMB: Long,
    val totalMemoryMB: Long,
    val maxMemoryMB: Long,
    val freeMemoryMB: Long
) {
    val usagePercent: Float
        get() = (usedMemoryMB.toFloat() / maxMemoryMB.toFloat()) * 100f

    override fun toString(): String {
        return "Memory: ${usedMemoryMB}MB / ${maxMemoryMB}MB (${String.format(Locale.US, "%.1f", usagePercent)}%)"
    }
}

/**
 * Lifecycle-aware resource holder using weak references.
 * Mock implementation to demonstrate proper lifecycle handling.
 */
class LifecycleResourceHolder<T : Any>(resource: T) {
    private val weakReference = WeakReference(resource)

    fun get(): T? = weakReference.get()

    fun release() {
        weakReference.clear()
    }

    fun isValid(): Boolean = weakReference.get() != null
}
