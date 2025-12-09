package com.example.conversion.presentation.ai

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.presentation.ai.AISuggestionsContract.Action
import com.example.conversion.presentation.ai.AISuggestionsContract.Event
import com.example.conversion.ui.theme.ConversionTheme

/**
 * AI-Powered Filename Suggestions Screen.
 * Allows users to analyze images and get intelligent filename suggestions.
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
@Composable
fun AISuggestionsScreen(
    viewModel: AISuggestionsViewModel = hiltViewModel(),
    onNavigateBackWithSuggestion: (String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is Event.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is Event.NavigateBackWithSuggestion -> {
                    onNavigateBackWithSuggestion(event.suggestion)
                }
                is Event.SuggestionApplied -> {
                    snackbarHostState.showSnackbar(
                        message = "Applied suggestion: ${event.suggestion}",
                        duration = SnackbarDuration.Short
                    )
                }
                is Event.AnalysisComplete,
                is Event.SuggestionsGenerated,
                is Event.AnalysisFailed,
                is Event.SuggestionGenerationFailed -> {
                    // Handled by state messages
                }
            }
        }
    }

    AISuggestionsScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AISuggestionsScreenContent(
    state: AISuggestionsContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAction(Action.AnalyzeAndSuggest(it)) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Filename Suggestions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    // Settings button
                    IconButton(
                        onClick = { onAction(Action.ToggleConfidenceSettings) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.canApplySuggestion) {
                FloatingActionButton(
                    onClick = { onAction(Action.ApplySuggestion) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Apply suggestion"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings panel (collapsible)
            AnimatedVisibility(
                visible = state.showConfidenceSettings,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                SettingsCard(
                    confidenceThreshold = state.confidenceThreshold,
                    maxSuggestions = state.maxSuggestions,
                    onConfidenceChange = { onAction(Action.UpdateConfidenceThreshold(it)) },
                    onMaxSuggestionsChange = { onAction(Action.UpdateMaxSuggestions(it)) }
                )
            }

            // Image selection section
            ImageSelectionCard(
                selectedImage = state.selectedImage,
                isLoading = state.isAnalyzing,
                onSelectImage = { imagePickerLauncher.launch("image/*") },
                onAnalyze = { onAction(Action.AnalyzeImage) }
            )

            // Error display
            if (state.error != null) {
                ErrorCard(
                    error = state.error,
                    onRetry = { onAction(Action.RetryAnalysis) },
                    onDismiss = { onAction(Action.DismissError) }
                )
            }

            // Detected labels section
            AnimatedVisibility(
                visible = state.hasLabels && !state.isAnalyzing,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                DetectedLabelsCard(
                    labels = state.detectedLabels,
                    onGenerateSuggestions = { onAction(Action.GenerateSuggestions) }
                )
            }

            // Loading indicator
            if (state.isLoading) {
                LoadingCard(
                    message = if (state.isAnalyzing) 
                        "Analyzing image..." 
                    else 
                        "Generating suggestions..."
                )
            }

            // Suggestions section
            AnimatedVisibility(
                visible = state.hasSuggestions && !state.isLoading,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                SuggestionsCard(
                    suggestions = state.suggestions,
                    selectedSuggestion = state.selectedSuggestion,
                    onSuggestionSelected = { onAction(Action.SelectSuggestion(it)) }
                )
            }

            // Empty state
            if (!state.hasImage && !state.isLoading) {
                EmptyStateCard(
                    onSelectImage = { imagePickerLauncher.launch("image/*") }
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    confidenceThreshold: Float,
    maxSuggestions: Int,
    onConfidenceChange: (Float) -> Unit,
    onMaxSuggestionsChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Analysis Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Confidence threshold slider
            Column {
                Text(
                    text = "Confidence Threshold: ${(confidenceThreshold * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = confidenceThreshold,
                    onValueChange = onConfidenceChange,
                    valueRange = 0.5f..1.0f,
                    steps = 9
                )
                Text(
                    text = "Higher threshold = more accurate but fewer labels",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Max suggestions slider
            Column {
                Text(
                    text = "Max Suggestions: $maxSuggestions",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = maxSuggestions.toFloat(),
                    onValueChange = { onMaxSuggestionsChange(it.toInt()) },
                    valueRange = 3f..10f,
                    steps = 6
                )
            }
        }
    }
}

@Composable
private fun ImageSelectionCard(
    selectedImage: Uri?,
    isLoading: Boolean,
    onSelectImage: () -> Unit,
    onAnalyze: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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

            if (selectedImage != null) {
                // Show selected image
                Image(
                    painter = rememberAsyncImagePainter(selectedImage),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable(onClick = onSelectImage),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSelectImage,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Change")
                    }

                    Button(
                        onClick = onAnalyze,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Analyze")
                    }
                }
            } else {
                // Show selection button
                Button(
                    onClick = onSelectImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Select Image")
                }
            }
        }
    }
}

@Composable
private fun DetectedLabelsCard(
    labels: List<ImageLabel>,
    onGenerateSuggestions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
                    text = "Detected Labels (${labels.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onGenerateSuggestions,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Generate")
                }
            }

            // Label chips in a flowing row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(labels.take(10)) { label ->
                    LabelChip(label = label)
                }
            }

            if (labels.size > 10) {
                Text(
                    text = "... and ${labels.size - 10} more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LabelChip(label: ImageLabel) {
    val confidenceColor = when {
        label.confidence >= 0.9f -> MaterialTheme.colorScheme.primary
        label.confidence >= 0.75f -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }

    SuggestionChip(
        onClick = { },
        label = {
            Column {
                Text(
                    text = label.text,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${(label.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = confidenceColor
                )
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = confidenceColor
            )
        }
    )
}

@Composable
private fun SuggestionsCard(
    suggestions: List<String>,
    selectedSuggestion: String?,
    onSuggestionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Suggested Filenames (${suggestions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            suggestions.forEach { suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    isSelected = suggestion == selectedSuggestion,
                    onClick = { onSuggestionSelected(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = if (!isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else null,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
private fun LoadingCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
                Button(onClick = onRetry) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    onSelectImage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .alpha(0.6f),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "AI-Powered Suggestions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Select an image to analyze and get intelligent filename suggestions based on detected content",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Button(
                onClick = onSelectImage,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Get Started")
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun AISuggestionsScreenPreview() {
    ConversionTheme {
        AISuggestionsScreenContent(
            state = AISuggestionsContract.State(
                selectedImage = null,
                detectedLabels = listOf(
                    ImageLabel("sunset", 0.92f, "nature"),
                    ImageLabel("beach", 0.88f, "nature"),
                    ImageLabel("ocean", 0.85f, "nature")
                ),
                suggestions = listOf(
                    "sunset_beach",
                    "beach_sunset",
                    "ocean_sunset",
                    "scenic_sunset",
                    "beach_ocean"
                ),
                selectedSuggestion = "sunset_beach"
            ),
            onAction = {},
            onBack = {}
        )
    }
}
