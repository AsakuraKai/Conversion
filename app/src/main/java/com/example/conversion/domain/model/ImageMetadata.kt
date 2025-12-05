package com.example.conversion.domain.model

import android.location.Location

/**
 * Represents EXIF metadata extracted from an image file.
 * Contains information about when and how the photo was taken.
 *
 * @property dateTaken Timestamp when the photo was taken (milliseconds since epoch)
 * @property location GPS coordinates where the photo was taken (if available)
 * @property cameraModel Name and model of the camera/device used
 * @property dimensions Image dimensions as a Pair (width x height)
 * @property orientation Image orientation in degrees (0, 90, 180, 270)
 * @property latitude Latitude coordinate (extracted for convenience)
 * @property longitude Longitude coordinate (extracted for convenience)
 * @property fNumber F-number/aperture value
 * @property exposureTime Exposure time in seconds
 * @property iso ISO speed rating
 * @property focalLength Focal length in millimeters
 * @property flash Whether flash was used
 */
data class ImageMetadata(
    val dateTaken: Long? = null,
    val location: Location? = null,
    val cameraModel: String? = null,
    val dimensions: Pair<Int, Int>? = null,
    val orientation: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val fNumber: String? = null,
    val exposureTime: String? = null,
    val iso: String? = null,
    val focalLength: String? = null,
    val flash: Boolean? = null
) {
    /**
     * Returns true if the image has GPS location data.
     */
    val hasLocation: Boolean
        get() = latitude != null && longitude != null

    /**
     * Returns true if the image has camera information.
     */
    val hasCameraInfo: Boolean
        get() = cameraModel != null

    /**
     * Returns true if the image has technical photo data (aperture, exposure, etc.).
     */
    val hasPhotoInfo: Boolean
        get() = fNumber != null || exposureTime != null || iso != null

    /**
     * Returns formatted location string for display.
     * Format: "latitude, longitude"
     */
    fun getFormattedLocation(): String? {
        return if (hasLocation) {
            String.format("%.6f, %.6f", latitude, longitude)
        } else {
            null
        }
    }

    /**
     * Returns formatted dimensions string.
     * Format: "width x height"
     */
    fun getFormattedDimensions(): String? {
        return dimensions?.let { (width, height) ->
            "$width x $height"
        }
    }

    /**
     * Returns megapixels count from dimensions.
     */
    fun getMegapixels(): Double? {
        return dimensions?.let { (width, height) ->
            (width * height) / 1_000_000.0
        }
    }

    /**
     * Returns orientation as human-readable string.
     */
    fun getOrientationString(): String? {
        return when (orientation) {
            0, 360 -> "Normal"
            90 -> "Rotate 90° CW"
            180 -> "Rotate 180°"
            270 -> "Rotate 90° CCW"
            else -> null
        }
    }
}
