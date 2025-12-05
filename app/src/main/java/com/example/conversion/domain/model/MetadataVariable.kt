package com.example.conversion.domain.model

/**
 * Enum representing metadata variables that can be used in filename patterns.
 * These variables can be extracted from image EXIF data and used in rename templates.
 */
enum class MetadataVariable(
    val variable: String,
    val description: String,
    val example: String
) {
    /**
     * Date when the photo was taken.
     * Format: YYYYMMDD
     */
    DATE(
        variable = "{date}",
        description = "Date photo was taken",
        example = "20231215"
    ),

    /**
     * Year when the photo was taken.
     * Format: YYYY
     */
    YEAR(
        variable = "{year}",
        description = "Year photo was taken",
        example = "2023"
    ),

    /**
     * Month when the photo was taken.
     * Format: MM (zero-padded)
     */
    MONTH(
        variable = "{month}",
        description = "Month photo was taken",
        example = "12"
    ),

    /**
     * Day when the photo was taken.
     * Format: DD (zero-padded)
     */
    DAY(
        variable = "{day}",
        description = "Day photo was taken",
        example = "15"
    ),

    /**
     * Time when the photo was taken.
     * Format: HHMMSS
     */
    TIME(
        variable = "{time}",
        description = "Time photo was taken",
        example = "143025"
    ),

    /**
     * GPS latitude coordinate.
     * Format: Decimal degrees with 6 decimal places
     */
    LATITUDE(
        variable = "{lat}",
        description = "GPS latitude",
        example = "37.774929"
    ),

    /**
     * GPS longitude coordinate.
     * Format: Decimal degrees with 6 decimal places
     */
    LONGITUDE(
        variable = "{lon}",
        description = "GPS longitude",
        example = "-122.419418"
    ),

    /**
     * Combined location string.
     * Format: lat_lon
     */
    LOCATION(
        variable = "{location}",
        description = "GPS coordinates",
        example = "37.774929_-122.419418"
    ),

    /**
     * Camera model used to take the photo.
     * Format: Sanitized string (spaces replaced with underscores)
     */
    CAMERA(
        variable = "{camera}",
        description = "Camera model",
        example = "Pixel_7_Pro"
    ),

    /**
     * Image width in pixels.
     */
    WIDTH(
        variable = "{width}",
        description = "Image width",
        example = "4032"
    ),

    /**
     * Image height in pixels.
     */
    HEIGHT(
        variable = "{height}",
        description = "Image height",
        example = "3024"
    ),

    /**
     * Image resolution in megapixels.
     * Format: One decimal place
     */
    MEGAPIXELS(
        variable = "{mp}",
        description = "Megapixels",
        example = "12.2"
    ),

    /**
     * Image orientation in degrees.
     */
    ORIENTATION(
        variable = "{orientation}",
        description = "Image orientation",
        example = "90"
    ),

    /**
     * F-number (aperture).
     */
    FNUMBER(
        variable = "{fnumber}",
        description = "Aperture f-number",
        example = "f1.8"
    ),

    /**
     * Exposure time.
     */
    EXPOSURE(
        variable = "{exposure}",
        description = "Exposure time",
        example = "1_60"
    ),

    /**
     * ISO speed rating.
     */
    ISO(
        variable = "{iso}",
        description = "ISO speed",
        example = "100"
    ),

    /**
     * Focal length in millimeters.
     */
    FOCAL_LENGTH(
        variable = "{focal}",
        description = "Focal length",
        example = "24mm"
    );

    companion object {
        /**
         * Returns all available variable names as a list.
         */
        fun getAllVariables(): List<String> = values().map { it.variable }

        /**
         * Finds a MetadataVariable by its variable string.
         * Returns null if not found.
         */
        fun fromVariable(variable: String): MetadataVariable? {
            return values().find { it.variable.equals(variable, ignoreCase = true) }
        }

        /**
         * Checks if a string contains any metadata variables.
         */
        fun containsVariables(text: String): Boolean {
            return values().any { text.contains(it.variable, ignoreCase = true) }
        }

        /**
         * Returns all variables found in a given text.
         */
        fun findVariables(text: String): List<MetadataVariable> {
            return values().filter { text.contains(it.variable, ignoreCase = true) }
        }
    }
}
