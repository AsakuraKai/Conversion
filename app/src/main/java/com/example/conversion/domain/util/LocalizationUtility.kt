package com.example.conversion.domain.util

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for locale-aware formatting of dates, numbers, and other values.
 * 
 * This utility uses the device's current locale to format values appropriately
 * for the user's language and region preferences.
 */
object LocalizationUtility {
    
    /**
     * Format a date using the default locale's medium date format.
     * 
     * @param date The date to format
     * @param locale The locale to use (defaults to system default)
     * @return Formatted date string (e.g., "Dec 8, 2025" in English)
     */
    fun formatDate(date: Date, locale: Locale = Locale.getDefault()): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale)
        return dateFormat.format(date)
    }
    
    /**
     * Format a date with time using the default locale's medium format.
     * 
     * @param date The date to format
     * @param locale The locale to use (defaults to system default)
     * @return Formatted date and time string
     */
    fun formatDateTime(date: Date, locale: Locale = Locale.getDefault()): String {
        val dateTimeFormat = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
            locale
        )
        return dateTimeFormat.format(date)
    }
    
    /**
     * Format a date using a custom pattern.
     * 
     * @param date The date to format
     * @param pattern The date pattern (e.g., "yyyy-MM-dd", "dd/MM/yyyy")
     * @param locale The locale to use (defaults to system default)
     * @return Formatted date string
     */
    fun formatDateWithPattern(
        date: Date,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): String {
        val dateFormat = SimpleDateFormat(pattern, locale)
        return dateFormat.format(date)
    }
    
    /**
     * Format a timestamp (milliseconds since epoch) as a date.
     * 
     * @param timestamp Milliseconds since epoch
     * @param locale The locale to use (defaults to system default)
     * @return Formatted date string
     */
    fun formatTimestamp(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return formatDate(Date(timestamp), locale)
    }
    
    /**
     * Format a number using the default locale's number format.
     * 
     * @param number The number to format
     * @param locale The locale to use (defaults to system default)
     * @return Formatted number string (e.g., "1,234.56" in English, "1.234,56" in German)
     */
    fun formatNumber(number: Number, locale: Locale = Locale.getDefault()): String {
        val numberFormat = NumberFormat.getNumberInstance(locale)
        return numberFormat.format(number)
    }
    
    /**
     * Format a number as a percentage.
     * 
     * @param value The value to format (0.0 to 1.0 represents 0% to 100%)
     * @param locale The locale to use (defaults to system default)
     * @return Formatted percentage string (e.g., "75%" in English)
     */
    fun formatPercentage(value: Double, locale: Locale = Locale.getDefault()): String {
        val percentFormat = NumberFormat.getPercentInstance(locale)
        return percentFormat.format(value)
    }
    
    /**
     * Format a file size in bytes to a human-readable string.
     * 
     * @param bytes The size in bytes
     * @param locale The locale to use for number formatting
     * @return Formatted size string (e.g., "1.23 MB", "456 KB")
     */
    fun formatFileSize(bytes: Long, locale: Locale = Locale.getDefault()): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        val numberFormat = NumberFormat.getNumberInstance(locale)
        numberFormat.maximumFractionDigits = 2
        numberFormat.minimumFractionDigits = 0
        
        return "${numberFormat.format(size)} ${units[unitIndex]}"
    }
    
    /**
     * Format a duration in milliseconds to a human-readable string.
     * 
     * @param durationMs Duration in milliseconds
     * @param locale The locale to use
     * @return Formatted duration string (e.g., "2h 30m", "45s")
     */
    fun formatDuration(durationMs: Long, locale: Locale = Locale.getDefault()): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60))
        
        return when {
            hours > 0 -> String.format(locale, "%dh %dm", hours, minutes)
            minutes > 0 -> String.format(locale, "%dm %ds", minutes, seconds)
            else -> String.format(locale, "%ds", seconds)
        }
    }
    
    /**
     * Get a relative time string (e.g., "2 hours ago", "yesterday").
     * 
     * This is a simplified implementation. For production, consider using
     * android.text.format.DateUtils.getRelativeTimeSpanString()
     * 
     * @param timestamp The timestamp to compare against now
     * @param locale The locale to use
     * @return Relative time string
     */
    fun formatRelativeTime(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
            days < 7 -> "$days day${if (days != 1L) "s" else ""} ago"
            else -> formatDate(Date(timestamp), locale)
        }
    }
    
    /**
     * Check if the current locale uses right-to-left (RTL) text direction.
     * 
     * @param locale The locale to check (defaults to system default)
     * @return true if RTL, false otherwise
     */
    fun isRtlLocale(locale: Locale = Locale.getDefault()): Boolean {
        val rtlLanguages = setOf("ar", "he", "fa", "ur", "yi")
        return rtlLanguages.contains(locale.language)
    }
    
    /**
     * Get the current locale's display name in its own language.
     * 
     * @param locale The locale to display
     * @return Display name (e.g., "English", "Español", "العربية")
     */
    fun getLocaleDisplayName(locale: Locale = Locale.getDefault()): String {
        return locale.getDisplayName(locale).replaceFirstChar { it.uppercase() }
    }
}

/* Production Enhancement Example:

// Use Android's built-in relative time formatting
fun formatRelativeTime(timestamp: Long, context: Context): String {
    return android.text.format.DateUtils.getRelativeTimeSpanString(
        timestamp,
        System.currentTimeMillis(),
        android.text.format.DateUtils.MINUTE_IN_MILLIS,
        android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}

// Use ICU4J for advanced locale formatting
fun formatNumberWithCurrency(amount: Double, currencyCode: String, locale: Locale): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(locale)
    currencyFormat.currency = Currency.getInstance(currencyCode)
    return currencyFormat.format(amount)
}

*/
