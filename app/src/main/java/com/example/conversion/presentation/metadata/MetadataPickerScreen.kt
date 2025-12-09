package com.example.conversion.presentation.metadata

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.ImageMetadata
import com.example.conversion.domain.model.MetadataVariable
import com.example.conversion.presentation.metadata.MetadataPickerContract.Action
import com.example.conversion.presentation.metadata.MetadataPickerContract.Event
import com.example.conversion.ui.components.MetadataPreviewCard
import com.example.conversion.ui.components.MetadataVariableChip
import com.example.conversion.ui.theme.ConversionTheme
import com.google.accompanist.flowlayout.FlowRow

/**
 * EXIF Metadata Variable Picker Screen.
 * Allows users to insert metadata variables into rename patterns.
 */
@Composable
fun MetadataPickerScreen(
    viewModel: MetadataPickerViewModel = hiltViewModel(),
    onNavigateBack: (String) -> Unit,
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
                is Event.NavigateBack -> {
                    onNavigateBack(event.pattern)
                }
                is Event.VariableInserted -> {
                    // Could show a brief animation or feedback here
                }
            }
        }
    }

    MetadataPickerContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetadataPickerContent(
    state: MetadataPickerContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insert Metadata Variables") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    if (state.currentPattern.isNotEmpty()) {
                        IconButton(onClick = { onAction(Action.ClearPattern) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear pattern"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onAction(Action.ApplyPattern) },
                        modifier = Modifier.weight(1f),
                        enabled = state.currentPattern.isNotEmpty()
                    ) {
                        Text("Apply Pattern")
                    }
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
            // Current Pattern Display
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Current Pattern",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.currentPattern.ifEmpty { "No variables added yet" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Live Preview with Sample Metadata
            if (state.hasMetadata && state.previewFilename.isNotEmpty()) {
                MetadataPreviewCard(
                    metadata = state.sampleMetadata!!,
                    pattern = state.currentPattern,
                    preview = state.previewFilename
                )
            }

            // Date/Time Variables Section
            VariableSection(
                title = "Date & Time",
                variables = state.dateTimeVariables,
                onVariableClick = { onAction(Action.InsertVariable(it)) }
            )

            // Location Variables Section
            VariableSection(
                title = "GPS Location",
                variables = state.locationVariables,
                onVariableClick = { onAction(Action.InsertVariable(it)) }
            )

            // Camera Variables Section
            VariableSection(
                title = "Camera Settings",
                variables = state.cameraVariables,
                onVariableClick = { onAction(Action.InsertVariable(it)) }
            )

            // Dimension Variables Section
            VariableSection(
                title = "Image Dimensions",
                variables = state.dimensionVariables,
                onVariableClick = { onAction(Action.InsertVariable(it)) }
            )

            // Loading state
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Error state
            if (state.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun VariableSection(
    title: String,
    variables: List<MetadataVariable>,
    onVariableClick: (MetadataVariable) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            variables.forEach { variable ->
                MetadataVariableChip(
                    variable = variable,
                    onClick = { onVariableClick(variable) }
                )
            }
        }
    }
}

// ============= Previews =============

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun MetadataPickerScreenPreview() {
    ConversionTheme {
        MetadataPickerContent(
            state = MetadataPickerContract.State(
                currentPattern = "IMG_{date}_{camera}",
                previewFilename = "IMG_20231215_Pixel_7_Pro",
                sampleMetadata = ImageMetadata(
                    dateTaken = System.currentTimeMillis(),
                    cameraModel = "Pixel 7 Pro",
                    dimensions = 4080 to 3072
                )
            ),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun MetadataPickerEmptyStatePreview() {
    ConversionTheme {
        MetadataPickerContent(
            state = MetadataPickerContract.State(),
            onAction = {},
            onBack = {}
        )
    }
}
