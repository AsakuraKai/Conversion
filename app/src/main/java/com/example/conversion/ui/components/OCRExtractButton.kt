package com.example.conversion.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.conversion.domain.model.ExtractedText
import com.example.conversion.presentation.ocr.OCRContract
import com.example.conversion.presentation.ocr.OCRViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * OCR text extraction button component.
 * Allows users to extract text from images and select text blocks for filename generation.
 * 
 * @param imageUri URI of the image to extract text from
 * @param onTextExtracted Callback when text is extracted and selected
 * @param modifier Optional modifier for customization
 * @param viewModel ViewModel for OCR operations (injected by Hilt)
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK 19 - OCR Integration UI
 */
@Composable
fun OCRExtractButton(
    imageUri: Uri?,
    onTextExtracted: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OCRViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is OCRContract.Event.TextExtracted -> {
                    // Event handled by state updates
                }
                is OCRContract.Event.TextBlockSelected -> {
                    onTextExtracted(event.text)
                }
                is OCRContract.Event.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is OCRContract.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Column(modifier = modifier) {
        // Extract text button
        Button(
            onClick = {
                imageUri?.let {
                    viewModel.handleAction(OCRContract.Action.ExtractText(it, combineText = false))
                }
            },
            enabled = imageUri != null && !state.isExtracting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.TextFields,
                contentDescription = "Extract Text",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (state.isExtracting) "Extracting..." else "Extract Text from Image")
        }

        // Loading indicator
        AnimatedVisibility(visible = state.isExtracting) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // Error display
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Extracted text blocks
        AnimatedVisibility(visible = state.hasExtractedText) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Detected Text (${state.extractedTextBlocks.size} blocks)",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Combined text card (if available)
                state.combinedText?.let { combined ->
                    ExtractedTextCard(
                        text = combined,
                        confidence = 1.0f,
                        onUseText = { viewModel.handleAction(OCRContract.Action.UseCombinedText(combined)) }
                    )
                }

                // Individual text blocks
                state.extractedTextBlocks.forEach { textBlock ->
                    ExtractedTextCard(
                        text = textBlock.text,
                        confidence = textBlock.confidence,
                        onUseText = { viewModel.handleAction(OCRContract.Action.SelectTextBlock(textBlock)) },
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Suggested filename
                state.suggestedFilename?.let { suggested ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Suggested Filename:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = suggested,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Snackbar host
        SnackbarHost(hostState = snackbarHostState)
    }

    // Text block details dialog
    if (state.showTextBlockDialog && state.selectedTextBlock != null) {
        TextBlockDetailsDialog(
            textBlock = state.selectedTextBlock!!,
            onDismiss = { viewModel.handleAction(OCRContract.Action.DismissTextBlockDialog) },
            onUse = { viewModel.handleAction(OCRContract.Action.SelectTextBlock(it)) }
        )
    }
}

/**
 * Card displaying extracted text with confidence score.
 */
@Composable
private fun ExtractedTextCard(
    text: String,
    confidence: Float,
    onUseText: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onUseText),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Confidence: ${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Use this text",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }
    }
}

/**
 * Dialog showing text block details.
 */
@Composable
private fun TextBlockDetailsDialog(
    textBlock: ExtractedText,
    onDismiss: () -> Unit,
    onUse: (ExtractedText) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Text Block Details") },
        text = {
            Column {
                Text(
                    text = "Text:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = textBlock.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Text(
                    text = "Confidence:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "${(textBlock.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                textBlock.language?.let { lang ->
                    Text(
                        text = "Language:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = lang,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Text(
                    text = "Sanitized for filename:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = textBlock.toFilenameFragment(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onUse(textBlock) }) {
                Text("Use This Text")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
