package com.example.conversion.data.util

import android.content.Context
import com.example.conversion.domain.util.LocalizedStringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android implementation of LocalizedStringProvider.
 * 
 * Provides localized strings from Android resources using Context.
 * This is a mock implementation that uses string resource names as keys.
 * 
 * **Production Upgrade:**
 * - Use actual string resource IDs from R.string
 * - Add resource ID mapping for type safety
 * - Handle missing resources gracefully
 * - Support custom locales beyond system default
 */
@Singleton
class AndroidLocalizedStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalizedStringProvider {
    
    override fun getString(key: String): String {
        return try {
            // Mock implementation: Use key as-is for development
            // Production: Use context.resources.getIdentifier() or R.string mapping
            val resourceId = context.resources.getIdentifier(key, "string", context.packageName)
            if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                // Fallback: Return key with formatting
                formatKeyAsFallback(key)
            }
        } catch (e: Exception) {
            formatKeyAsFallback(key)
        }
    }
    
    override fun getString(key: String, vararg formatArgs: Any): String {
        return try {
            val resourceId = context.resources.getIdentifier(key, "string", context.packageName)
            if (resourceId != 0) {
                context.getString(resourceId, *formatArgs)
            } else {
                formatKeyAsFallback(key)
            }
        } catch (e: Exception) {
            formatKeyAsFallback(key)
        }
    }
    
    override fun getQuantityString(key: String, quantity: Int, vararg formatArgs: Any): String {
        return try {
            val resourceId = context.resources.getIdentifier(key, "plurals", context.packageName)
            if (resourceId != 0) {
                context.resources.getQuantityString(resourceId, quantity, *formatArgs)
            } else {
                formatKeyAsFallback(key)
            }
        } catch (e: Exception) {
            formatKeyAsFallback(key)
        }
    }
    
    /**
     * Format a resource key as a readable fallback string.
     * 
     * Example: "error_file_not_found" -> "Error File Not Found"
     */
    private fun formatKeyAsFallback(key: String): String {
        return key.split('_')
            .joinToString(" ") { word -> 
                word.replaceFirstChar { it.uppercase() }
            }
    }
}

/* Production Implementation Example:

@Singleton
class AndroidLocalizedStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalizedStringProvider {
    
    // Type-safe resource ID mapping
    private val stringResourceMap = mapOf(
        StringKeys.ERROR_NO_PERMISSION to R.string.error_no_permission,
        StringKeys.ERROR_FILE_NOT_FOUND to R.string.error_file_not_found,
        StringKeys.RENAME_COMPLETE to R.string.rename_complete,
        // ... all string keys
    )
    
    private val pluralResourceMap = mapOf(
        StringKeys.FILES_COUNT to R.plurals.files_count,
        StringKeys.FOLDERS_COUNT to R.plurals.folders_count,
        // ... all plural keys
    )
    
    override fun getString(key: String): String {
        val resourceId = stringResourceMap[key] ?: return key
        return context.getString(resourceId)
    }
    
    override fun getString(key: String, vararg formatArgs: Any): String {
        val resourceId = stringResourceMap[key] ?: return key
        return context.getString(resourceId, *formatArgs)
    }
    
    override fun getQuantityString(key: String, quantity: Int, vararg formatArgs: Any): String {
        val resourceId = pluralResourceMap[key] ?: return key
        return context.resources.getQuantityString(resourceId, quantity, *formatArgs)
    }
}

*/
