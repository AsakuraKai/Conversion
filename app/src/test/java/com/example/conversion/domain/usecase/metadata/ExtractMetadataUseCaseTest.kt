package com.example.conversion.domain.usecase.metadata

import android.location.Location
import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageMetadata
import com.example.conversion.domain.repository.MetadataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.FileNotFoundException

/**
 * Unit tests for ExtractMetadataUseCase.
 * Tests EXIF metadata extraction logic and error handling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExtractMetadataUseCaseTest {
    
    private lateinit var metadataRepository: MetadataRepository
    private lateinit var useCase: ExtractMetadataUseCase
    private val testDispatcher = UnconfinedTestDispatcher()
    
    // Mock data
    private val mockUri = Uri.parse("content://media/external/images/1")
    private val mockLocation = Location("exif").apply {
        latitude = 37.774929
        longitude = -122.419418
    }
    
    private val fullMetadata = ImageMetadata(
        dateTaken = 1702627200000L, // 2023-12-15 12:00:00
        location = mockLocation,
        cameraModel = "Google Pixel 7 Pro",
        dimensions = Pair(4032, 3024),
        orientation = 0,
        latitude = 37.774929,
        longitude = -122.419418,
        fNumber = "f/1.8",
        exposureTime = "1/120",
        iso = "100",
        focalLength = "24mm",
        flash = false
    )
    
    @Before
    fun setup() {
        metadataRepository = mockk()
        useCase = ExtractMetadataUseCase(metadataRepository, testDispatcher)
    }
    
    @Test
    fun `extractMetadata returns success with complete metadata`() = runTest {
        // Given
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(fullMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertEquals(fullMetadata, metadata)
        assertTrue(metadata.hasLocation)
        assertTrue(metadata.hasCameraInfo)
        assertTrue(metadata.hasPhotoInfo)
        coVerify(exactly = 1) { metadataRepository.extractMetadata(mockUri) }
    }
    
    @Test
    fun `extractMetadata returns metadata with only date`() = runTest {
        // Given
        val minimalMetadata = ImageMetadata(
            dateTaken = 1702627200000L,
            location = null,
            cameraModel = null,
            dimensions = null,
            orientation = null
        )
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(minimalMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertNotNull(metadata.dateTaken)
        assertFalse(metadata.hasLocation)
        assertFalse(metadata.hasCameraInfo)
    }
    
    @Test
    fun `extractMetadata returns metadata with GPS data`() = runTest {
        // Given
        val gpsMetadata = ImageMetadata(
            latitude = 37.774929,
            longitude = -122.419418,
            location = mockLocation
        )
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(gpsMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertTrue(metadata.hasLocation)
        assertEquals("37.774929, -122.419418", metadata.getFormattedLocation())
    }
    
    @Test
    fun `extractMetadata returns metadata with camera info`() = runTest {
        // Given
        val cameraMetadata = ImageMetadata(
            cameraModel = "Canon EOS 5D Mark IV",
            fNumber = "f/2.8",
            exposureTime = "1/250",
            iso = "400",
            focalLength = "85mm"
        )
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(cameraMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertTrue(metadata.hasCameraInfo)
        assertTrue(metadata.hasPhotoInfo)
        assertEquals("Canon EOS 5D Mark IV", metadata.cameraModel)
    }
    
    @Test
    fun `extractMetadata returns metadata with dimensions`() = runTest {
        // Given
        val dimensionMetadata = ImageMetadata(
            dimensions = Pair(4032, 3024)
        )
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(dimensionMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertEquals("4032 x 3024", metadata.getFormattedDimensions())
        assertEquals(12.19, metadata.getMegapixels()!!, 0.01)
    }
    
    @Test
    fun `extractMetadata returns metadata with orientation`() = runTest {
        // Given
        val orientationMetadata = ImageMetadata(
            orientation = 90
        )
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(orientationMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertEquals(90, metadata.orientation)
        assertEquals("Rotate 90° CW", metadata.getOrientationString())
    }
    
    @Test
    fun `extractMetadata returns empty metadata when no EXIF data`() = runTest {
        // Given
        val emptyMetadata = ImageMetadata()
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(emptyMetadata)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val metadata = (result as Result.Success).data
        assertNull(metadata.dateTaken)
        assertNull(metadata.cameraModel)
        assertFalse(metadata.hasLocation)
        assertFalse(metadata.hasCameraInfo)
        assertFalse(metadata.hasPhotoInfo)
    }
    
    @Test
    fun `extractMetadata returns error when file not found`() = runTest {
        // Given
        val exception = FileNotFoundException("Image file not found")
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Error(exception)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals(exception, error.exception)
    }
    
    @Test
    fun `extractMetadata returns error when URI is invalid`() = runTest {
        // Given
        val exception = IllegalArgumentException("Invalid URI scheme")
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Error(exception)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is IllegalArgumentException)
        assertEquals("Invalid URI scheme", error.exception.message)
    }
    
    @Test
    fun `extractMetadata returns error when EXIF data is corrupted`() = runTest {
        // Given
        val exception = java.io.IOException("Failed to read EXIF data")
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Error(exception)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is java.io.IOException)
    }
    
    @Test
    fun `extractMetadata handles multiple calls with different URIs`() = runTest {
        // Given
        val uri1 = Uri.parse("content://media/external/images/1")
        val uri2 = Uri.parse("content://media/external/images/2")
        val metadata1 = ImageMetadata(cameraModel = "Camera 1")
        val metadata2 = ImageMetadata(cameraModel = "Camera 2")
        
        coEvery { metadataRepository.extractMetadata(uri1) } returns Result.Success(metadata1)
        coEvery { metadataRepository.extractMetadata(uri2) } returns Result.Success(metadata2)
        
        // When
        val result1 = useCase(uri1)
        val result2 = useCase(uri2)
        
        // Then
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertEquals("Camera 1", (result1 as Result.Success).data.cameraModel)
        assertEquals("Camera 2", (result2 as Result.Success).data.cameraModel)
    }
    
    @Test
    fun `extractMetadata verifies repository is called exactly once`() = runTest {
        // Given
        coEvery { metadataRepository.extractMetadata(mockUri) } returns Result.Success(fullMetadata)
        
        // When
        useCase(mockUri)
        
        // Then
        coVerify(exactly = 1) { metadataRepository.extractMetadata(mockUri) }
    }
}
