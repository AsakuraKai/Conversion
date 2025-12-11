package com.example.conversion.presentation.ocr

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.conversion.domain.model.ExtractedText
import com.example.conversion.presentation.ocr.OCRContract.Action
import com.example.conversion.presentation.ocr.OCRContract.Event
import com.example.conversion.ui.theme.ConversionTheme

/**
 * OCR Text Extraction Screen.
 * Allows users to extract text from images using ML Kit.
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK A - Navigation Wiring Foundation
 */
@Composable
fun OCRScreen(
    viewModel: OCRViewModel = hiltViewModel(),
    onNavigateBackWithText: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.TextExtracted -> {
                    snackbarHostState.showSnackbar(
                        message = if (event.combinedText != null) {
                            "Extracted combined text"
                        } else {
                            "Extracted ${event.textBlocks.size} text blocks"
                        },
                        duration = SnackbarDuration.Short
                    )
                }
                is Event.TextBlockSelected -> {
                    onNavigateBackWithText(event.text)
                }
                is Event.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    OCRScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OCRScreenContent(
    state: OCRContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAction(Action.ExtractText(imageUri = it, combineText = false)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OCR Text Extraction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isExtracting -> {
                    LoadingState()
                }
                state.error != null -> {
                    ErrorState(
                        error = state.error,
                        onRetry = {
                            onAction(Action.ClearError)
                            state.imageUri?.let {
                                onAction(Action.ExtractText(imageUri = it, combineText = false))
                            }
                        },
                        onDismiss = { onAction(Action.ClearError) }
                    )
                }
                else -> {
                    OCRContent(
                        state = state,
                        onAction = onAction,
                        onSelectImage = { imagePickerLauncher.launch("image/*") }
                    )
                }
            }
        }
    }
}

@Composable
private fun OCRContent(
    state: OCRContract.State,
    onAction: (Action) -> Unit,
    onSelectImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image Selection Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (state.imageUri != null) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable(onClick = onSelectImage),
                        contentScale = ContentScale.Crop
                    )
                    
                    OutlinedButton(
                        onClick = onSelectImage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change Image")
                    }
                } else {
                    Button(
                        onClick = onSelectImage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Image")
                    }
                }
            }
        }

        // Confidence Threshold Section
        if (state.imageUri != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Confidence Threshold",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${(state.confidenceThreshold * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = state.confidenceThreshold,
                        onValueChange = { onAction(Action.ChangeConfidenceThreshold(it)) },
                        valueRange = 0.0f..1.0f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Only text blocks with confidence above this threshold will be shown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Extract Button
        if (state.imageUri != null && !state.hasExtractedText) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        onAction(Action.ExtractText(
                            imageUri = state.imageUri,
                            combineText = false
                        ))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.canExtractText
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Extract Blocks")
                }

                Button(
                    onClick = { 
                        onAction(Action.ExtractText(
                            imageUri = state.imageUri,
                            combineText = true
                        ))
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.canExtractText
                ) {
                    Icon(Icons.Default.MergeType, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Combine Text")
                }
            }
        }

        // Combined Text Display
        if (state.combinedText != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Combined Text",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = state.combinedText,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = { onAction(Action.UseCombinedText(state.combinedText)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use This Text")
                    }

                    OutlinedButton(
                        onClick = { onAction(Action.ClearExtractedText) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extract Again")
                    }
                }
            }
        }

        // Extracted Text Blocks
        if (state.extractedTextBlocks.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Extracted Text Blocks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.extractedTextBlocks.size} blocks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    state.extractedTextBlocks.forEach { textBlock ->
                        TextBlockItem(
                            textBlock = textBlock,
                            onUse = { onAction(Action.SelectTextBlock(textBlock)) },
                            onShowDetails = { onAction(Action.ShowTextBlockDetails(textBlock)) }
                        )
                    }

                    OutlinedButton(
                        onClick = { onAction(Action.ClearExtractedText) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extract Again")
                    }
                }
            }
        }

        // Suggested Filename (if available)
        state.suggestedFilename?.let { suggestion ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Suggested Filename",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TextBlockItem(
    textBlock: ExtractedText,
    onUse: () -> Unit,
    onShowDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowDetails),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = textBlock.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Confidence: ${(textBlock.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            textBlock.meetsThreshold(ExtractedText.HIGH_CONFIDENCE_THRESHOLD) -> 
                                MaterialTheme.colorScheme.primary
                            textBlock.meetsThreshold(ExtractedText.MEDIUM_CONFIDENCE_THRESHOLD) -> 
                                MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            FilledTonalButton(
                onClick = onUse,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Use")
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Extracting text from image...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dismiss")
                    }
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
