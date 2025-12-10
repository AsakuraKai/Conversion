# AI & Machine Learning - Mock Implementations

**Last Updated:** December 10, 2025  
**Category:** Data Layer - Machine Learning  
**Tool Decision:** ✅ Google ML Kit (On-Device) - Zero external setup, just build.gradle dependencies  
**Related Chunks:** CHUNKS 6, 10, 11

---

## 📋 Overview

This group covers AI and machine learning features using mock implementations with deterministic outputs for development. All three implementations will upgrade to **Google ML Kit** for production, providing a unified on-device AI solution that's privacy-friendly and works offline.

**Implementations in this group:**
- **CHUNK 6:** MLRepositoryImpl - AI-powered image analysis and filename suggestions
- **CHUNK 10:** QRRepositoryImpl - QR code generation and scanning for preset sharing
- **CHUNK 11:** OCRRepositoryImpl - Optical character recognition for text extraction

**Common theme:** AI/ML features with mock data, upgrading to Google ML Kit for on-device, privacy-friendly processing

**Why ML Kit (Unified Strategy):**
- **Most versatile:** Handles OCR, QR/Barcode scanning, and Image Labeling in one SDK
- **Zero console setup:** No API keys, no project configuration, no external accounts
- **On-device processing:** Privacy-friendly, works completely offline, no data leaves device
- **Just add dependencies:** Simple build.gradle additions to get started
- **Continuous improvements:** Google's pre-trained models improve over time
- **Production-ready:** Used by major apps, proven at scale

**ML Kit Dependencies (Production):**
```kotlin
dependencies {
    // All three features in one unified SDK
    implementation("com.google.mlkit:image-labeling:17.0.7")      // CHUNK 6
    implementation("com.google.mlkit:barcode-scanning:17.2.0")    // CHUNK 10
    implementation("com.google.mlkit:text-recognition:16.0.0")    // CHUNK 11
}
```

**Total SDK Size:** ~15-20MB (one-time download for all features)  
**Setup Time:** 15 minutes (just dependencies, no external configuration)  
**Privacy:** 100% on-device, zero data transmission

---

## 6️⃣ MLRepositoryImpl.kt

**Location:** `data/repository/MLRepositoryImpl.kt`  
**Chunk:** 13 (AI-Powered Filename Suggestions)  
**Priority:** Medium  
**Production Tool:** ✅ Google ML Kit Image Labeling

### Strategic Implementation
Uses hash-based mock AI responses with 5 predefined pattern categories to provide consistent, testable image analysis results. The implementation simulates ML Kit's ImageLabel data structure, making the upgrade to production ML Kit straightforward (just swap the data source).

**Mock Strategy:**
- Analyzes image URI hash to select pattern category
- Returns realistic labels with confidence scores (0.60-0.95 range)
- Covers diverse image types: Animals, Food, Nature, Objects, Sports
- Provides 5-10 labels per image for rich suggestions
- Hash-based = consistent results for testing

### Fully Functional Features
✅ Complete image analysis with confidence scores  
✅ 5 diverse label pattern categories  
✅ Hash-based consistent results for testing  
✅ Filename suggestion generation from labels  
✅ Label filtering by confidence threshold  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (43+ tests)

### Production Enhancements Needed
🔄 Integrate ML Kit Image Labeling API  
🔄 Support 400+ real-world label categories  
🔄 Adjust confidence threshold tuning  
🔄 Add image quality validation  
🔄 Implement model caching optimization  
🔄 Handle ML processing errors gracefully

### Production Upgrade
```kotlin
// 1. Add ML Kit Dependency (build.gradle.kts)
dependencies {
    implementation("com.google.mlkit:image-labeling:17.0.7")
}

// 2. Implement Real ML Repository
@Singleton
class MLRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MLRepository {
    
    private val labeler: ImageLabeler by lazy {
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.6f)
            .build()
        ImageLabeling.getClient(options)
    }
    
    override suspend fun analyzeImage(
        imageUri: Uri,
        confidenceThreshold: Float
    ): Result<List<ImageLabel>> = withContext(ioDispatcher) {
        try {
            // Load image from URI
            val inputImage = InputImage.fromFilePath(context, imageUri)
            
            // Process with ML Kit
            val labels = suspendCancellableCoroutine<List<Label>> { continuation ->
                labeler.process(inputImage)
                    .addOnSuccessListener { labels ->
                        continuation.resume(labels)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            // Convert to domain models
            val imageLabels = labels
                .filter { it.confidence >= confidenceThreshold }
                .map { label ->
                    ImageLabel(
                        text = label.text,
                        confidence = label.confidence,
                        index = label.index
                    )
                }
            
            Result.Success(imageLabels)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun generateFilenameSuggestions(
        imageUri: Uri,
        maxSuggestions: Int
    ): Result<List<String>> = withContext(ioDispatcher) {
        when (val result = analyzeImage(imageUri, confidenceThreshold = 0.7f)) {
            is Result.Success -> {
                val suggestions = result.data
                    .take(maxSuggestions)
                    .map { label ->
                        label.text
                            .lowercase()
                            .replace(" ", "_")
                            .filter { it.isLetterOrDigit() || it == '_' }
                    }
                Result.Success(suggestions)
            }
            is Result.Error -> Result.Error(result.exception)
        }
    }
    
    // Cleanup
    fun close() {
        labeler.close()
    }
}

// 3. Update DI Module
@Module
@InstallIn(SingletonComponent::class)
abstract class MLDataModule {
    
    @Binds
    @Singleton
    abstract fun bindMLRepository(impl: MLRepositoryImpl): MLRepository
    
    companion object {
        @Provides
        fun provideImageLabeler(): ImageLabeler {
            val options = ImageLabelerOptions.Builder()
                .setConfidenceThreshold(ImageLabel.DEFAULT_CONFIDENCE_THRESHOLD)
                .build()
            return ImageLabeling.getClient(options)
        }
    }
}
```

### Trade-offs
**Current Implementation:**
- ✅ No Google Play Services dependency
- ✅ Works offline without model downloads
- ✅ Consistent results for testing
- ✅ Zero latency for development
- ⚠️ Mock data only (5 predefined patterns)

**Production Implementation:**
- ✅ Real AI-powered label detection
- ✅ Accurate image content analysis
- ✅ Supports 400+ label categories
- ✅ Continuous model improvements from Google
- ⚠️ Requires Google Play Services
- ⚠️ First-run model download (15-20MB)
- ⚠️ Processing latency (200-500ms per image)
- ⚠️ Potential ML failures need error handling

---

## 🔟 QRRepositoryImpl.kt + QRScannerScreen.kt

**Location:** `data/repository/QRRepositoryImpl.kt`, `presentation/qr/QRScannerScreen.kt`  
**Chunk:** 18 (QR Code Generation for Presets)  
**Priority:** Low  
**Production Tool:** ✅ Google ML Kit Barcode Scanning (Unified with ML strategy)

### Strategic Implementation
**Backend (QRRepositoryImpl):** Uses pattern-based bitmap generation to simulate QR codes without requiring external libraries. Provides complete QR code functionality with JSON serialization, enabling immediate UI development for template sharing features.

**Frontend (QRScannerScreen):** Uses image picker instead of camera for QR code scanning. This allows full template import workflow without CameraX integration during development.

**Why ML Kit for Production:**
- Unified with image labeling and OCR (one SDK for all AI/ML features)
- Superior barcode/QR detection compared to standalone libraries
- On-device processing, no internet required
- Supports multiple barcode formats beyond QR (UPC, EAN, etc.)

### Fully Functional Features
✅ QR code generation from RenameTemplate (512x512 default)  
✅ QR code parsing with cache-based decoding  
✅ JSON serialization/deserialization (kotlinx.serialization)  
✅ Pattern-based bitmap with QR-like appearance  
✅ Finder patterns (corner squares) for visual realism  
✅ Size validation (256-2048px range)  
✅ PresetQRData model with schema versioning  
✅ Round-trip template conversion preserves all data  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (54 tests)  
✅ Mock scanner UI using image picker for QR images  
✅ Import confirmation dialog with template preview  
✅ QR display screen with multiple size options  
✅ Share functionality for QR code bitmaps

### Production Enhancements Needed
🔄 Integrate ML Kit Barcode Scanning for real QR code generation and scanning  
🔄 Implement actual QR code scanning from camera (CameraX + ML Kit)  
🔄 Add error correction levels support  
🔄 Support scanning external QR codes (multi-format barcodes)  
🔄 Add QR code customization (colors, logo overlay)  
🔄 Real-time camera preview for scanning  
🔄 Torch/flash control for scanning in low light  
🔄 Batch QR scanning for multiple presets

### Production Upgrade
```kotlin
// 1. Add ML Kit Barcode Scanning dependencies (build.gradle.kts)
dependencies {
    // ML Kit Barcode Scanning (unified with OCR and Image Labeling)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // CameraX for camera integration
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
}

// 2. Real QR Code Generation (still uses ZXing for encoding)
dependencies {
    // ZXing for QR generation only (ML Kit handles scanning)
    implementation("com.google.zxing:core:3.5.2")
}

override suspend fun generateQRCode(template: RenameTemplate, size: Int): Result<Bitmap> =
    withContext(ioDispatcher) {
        try {
            val qrData = PresetQRData.fromRenameTemplate(template)
            val jsonString = json.encodeToString(qrData)
            
            // Encode to QR code with ZXing
            val bitMatrix = MultiFormatWriter().encode(
                jsonString, 
                BarcodeFormat.QR_CODE, 
                size, 
                size,
                mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M)
            )
            
            // Convert to Bitmap
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
            
            Result.Success(bitmap)
        } catch (e: Exception) {
            Result.Error("Failed to generate QR code: ${e.message}")
        }
    }

// 3. ML Kit Barcode Scanning for Camera
@Singleton
class QRRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : QRRepository {
    
    private val scanner: BarcodeScanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }
    
    override suspend fun scanQRCodeFromImage(imageUri: Uri): Result<RenameTemplate> =
        withContext(ioDispatcher) {
            try {
                // Load image from URI
                val inputImage = InputImage.fromFilePath(context, imageUri)
                
                // Scan with ML Kit
                val barcodes = suspendCancellableCoroutine<List<Barcode>> { continuation ->
                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            continuation.resume(barcodes)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                
                // Find QR code and parse
                val qrCode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                    ?: return@withContext Result.Error("No QR code found in image")
                
                val jsonString = qrCode.rawValue
                    ?: return@withContext Result.Error("QR code has no data")
                
                // Deserialize and convert
                val qrData = json.decodeFromString<PresetQRData>(jsonString)
                if (!qrData.isValid()) {
                    return@withContext Result.Error("Invalid QR code data")
                }
                
                Result.Success(qrData.toRenameTemplate())
            } catch (e: Exception) {
                Result.Error("Failed to scan QR code: ${e.message}")
            }
        }
    
    // Cleanup
    fun close() {
        scanner.close()
    }
}

// 4. Real-time Camera Scanning (Composable)
@Composable
fun QRScannerScreen(
    onQRCodeDetected: (RenameTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    // ML Kit scanner
    val scanner = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
            .let { BarcodeScanning.getClient(it) }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    // Preview
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    // Image analysis for QR scanning
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analyzer ->
                            analyzer.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                processImageProxy(imageProxy, scanner) { template ->
                                    onQRCodeDetected(template)
                                }
                            }
                        }
                    
                    // Bind to lifecycle
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("QRScanner", "Camera binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Scanning overlay
        ScanningOverlay()
        
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, "Close scanner")
        }
    }
}

private fun processImageProxy(
    imageProxy: ImageProxy,
    scanner: BarcodeScanner,
    onSuccess: (RenameTemplate) -> Unit
) {
    val mediaImage = imageProxy.image ?: return
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    
    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { rawValue ->
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val qrData = json.decodeFromString<PresetQRData>(rawValue)
                    if (qrData.isValid()) {
                        onSuccess(qrData.toRenameTemplate())
                    }
                } catch (e: Exception) {
                    Log.e("QRScanner", "Failed to parse QR code", e)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("QRScanner", "Barcode scanning failed", e)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
```

### Trade-offs
**Current Implementation:**
- ✅ Zero dependencies, instant development
- ✅ Full JSON encode/decode working
- ✅ Consistent results for testing
- ✅ Visual QR-like appearance
- ✅ Image picker workflow fully functional
- ✅ Complete UI/UX for template import
- ⚠️ Cannot scan real external QR codes
- ⚠️ Generated codes only work within app
- ⚠️ Requires selecting image from gallery

**Production Implementation:**
- ✅ Real QR codes scannable by any app
- ✅ Can import templates from other users/apps
- ✅ Industry-standard QR format
- ✅ Camera-based real-time scanning
- ✅ Better UX with instant scanning
- ⚠️ ~2MB library size (ZXing)
- ⚠️ Requires camera + CameraX permissions
- ⚠️ More complex error handling
- ⚠️ CameraX integration complexity

---

## 1️⃣1️⃣ OCRRepositoryImpl.kt

**Location:** `data/repository/OCRRepositoryImpl.kt`  
**Chunk:** 19 (OCR Integration)  
**Priority:** Medium  
**Production Tool:** ✅ Google ML Kit Text Recognition (Unified with ML strategy)

### Strategic Implementation
Uses simulated OCR (Optical Character Recognition) responses with predefined text patterns to unblock UI development. Provides realistic mock data based on common document types (receipts, business cards, signs, menus) without requiring ML Kit setup.

**Why ML Kit Text Recognition for Production:**
- Unified with image labeling and barcode scanning (one SDK for all AI/ML)
- Supports 50+ languages with on-device models
- High accuracy with Google's pre-trained models
- Zero external setup (no API console, no keys)

### Fully Functional Features
✅ Complete text extraction with confidence scores  
✅ 7 diverse document pattern types (Document, Receipt, Business Card, Sign, Menu, Poster, Mixed)  
✅ Bounding box coordinates for text positioning  
✅ Language detection support ("en")  
✅ Confidence threshold filtering  
✅ Combined text extraction for simple use cases  
✅ Hash-based consistent results for testing  
✅ Text sanitization for filename generation  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (52+ tests)

### Production Enhancements Needed
🔄 Integrate ML Kit Text Recognition API  
🔄 Add multi-language support (50+ languages)  
🔄 Implement handwriting recognition  
🔄 Add document type detection  
🔄 Optimize text block grouping and merging  
🔄 Add on-device model caching  
🔄 Implement text orientation correction

### Production Upgrade
```kotlin
// 1. Add ML Kit Dependency (build.gradle.kts)
dependencies {
    implementation("com.google.mlkit:text-recognition:16.0.0")
    // Optional: Specific language scripts
    implementation("com.google.mlkit:text-recognition-chinese:16.0.0")
    implementation("com.google.mlkit:text-recognition-devanagari:16.0.0")
}

// 2. Implement Real OCR Repository
@Singleton
class OCRRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OCRRepository {
    
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    
    override suspend fun extractTextFromImage(
        imageUri: Uri,
        confidenceThreshold: Float
    ): Result<List<ExtractedText>> = withContext(ioDispatcher) {
        try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            
            suspendCoroutine { continuation ->
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val textBlocks = visionText.textBlocks.flatMap { block ->
                            block.lines.map { line ->
                                ExtractedText(
                                    text = line.text,
                                    confidence = line.confidence ?: 0f,
                                    boundingBox = line.boundingBox ?: Rect(0, 0, 0, 0),
                                    language = line.recognizedLanguage
                                )
                            }
                        }.filter { it.confidence >= confidenceThreshold }
                        
                        continuation.resume(Result.Success(textBlocks))
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(Result.Error(exception))
                    }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun extractCombinedText(
        imageUri: Uri,
        confidenceThreshold: Float
    ): Result<String> = withContext(ioDispatcher) {
        when (val result = extractTextFromImage(imageUri, confidenceThreshold)) {
            is Result.Success -> {
                val combinedText = result.data.joinToString(" ") { it.text }
                Result.Success(combinedText)
            }
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
```

### Trade-offs
**Current Implementation:**
- ✅ No ML Kit dependency required
- ✅ Works offline without model downloads
- ✅ Instant setup for development
- ✅ Consistent results for testing
- ✅ 7 diverse document patterns
- ✅ Zero processing latency
- ⚠️ Mock data only (predetermined text patterns)
- ⚠️ Limited to English language simulation

**Production Implementation:**
- ✅ Real text recognition from images
- ✅ Accurate OCR with 50+ language support
- ✅ Handwriting recognition capability
- ✅ On-device processing (privacy-friendly)
- ✅ Continuous model improvements from Google
- ⚠️ Requires ML Kit setup (~15-20MB)
- ⚠️ Processing latency (500-1500ms per image)
- ⚠️ First-run model download required
- ⚠️ May fail on poor quality images

---

## 📚 Related Documentation

- **Main Index:** [MOCK_IMPLEMENTATIONS.md](../MOCK_IMPLEMENTATIONS.md)
- **CHUNK_13_COMPLETION.md:** AI-powered filename suggestions implementation details
- **CHUNK_18_COMPLETION.md:** QR code generation implementation details
- **CHUNK_19_COMPLETION.md:** OCR integration implementation details
- **DATA_PERSISTENCE_MOCKS.md:** Data storage implementations
- **CLOUD_SYNC_MOCKS.md:** Cloud integration implementations
- **README.md - Implementation Strategy:** Technology selection rationale

---

📚 **Back to:** [Main Index](../MOCK_IMPLEMENTATIONS.md)
