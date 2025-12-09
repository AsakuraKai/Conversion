package com.example.conversion.presentation.regex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.RegexFlag
import com.example.conversion.domain.model.RegexPreset

/**
 * Regex builder screen for creating and testing regex patterns.
 * Provides pattern input, presets, validation, and live preview.
 *
 * @param onNavigateBack Callback when user navigates back
 * @param viewModel ViewModel for managing regex builder state
 */
@Composable
fun RegexBuilderScreen(
    onNavigateBack: () -> Unit,
    viewModel: RegexViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RegexContract.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is RegexContract.Event.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = "${event.title}: ${event.message}",
                        duration = SnackbarDuration.Long
                    )
                }
                is RegexContract.Event.PatternApplied -> {
                    // Pattern applied successfully
                }
                is RegexContract.Event.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    RegexBuilderContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::handleAction,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Content composable for regex builder screen.
 */
@Composable
private fun RegexBuilderContent(
    state: RegexContract.State,
    snackbarHostState: SnackbarHostState,
    onAction: (RegexContract.Action) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            RegexBuilderTopBar(
                canApply = state.canApply,
                onNavigateBack = onNavigateBack,
                onApply = { onAction(RegexContract.Action.ApplyPattern) },
                onReset = { onAction(RegexContract.Action.Reset) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pattern input section
            PatternInputSection(
                pattern = state.pattern,
                replacement = state.replacement,
                validationError = state.validationError,
                validationSuggestion = state.validationSuggestion,
                isValidating = state.isValidating,
                onPatternChange = { onAction(RegexContract.Action.UpdatePattern(it)) },
                onReplacementChange = { onAction(RegexContract.Action.UpdateReplacement(it)) }
            )

            // Flags section
            FlagsSection(
                selectedFlags = state.flags,
                onToggleFlag = { onAction(RegexContract.Action.ToggleFlag(it)) }
            )

            // Options section
            OptionsSection(
                preserveExtension = state.preserveExtension,
                onTogglePreserveExtension = { onAction(RegexContract.Action.TogglePreserveExtension) }
            )

            Divider()

            // Presets section
            PresetsSection(
                selectedPreset = state.selectedPreset,
                onPresetClick = { onAction(RegexContract.Action.ApplyPreset(it)) }
            )

            Divider()

            // Preview section
            PreviewSection(
                previewInput = state.previewInput,
                previewResult = state.previewResult,
                canPreview = state.canPreview,
                onPreviewInputChange = { onAction(RegexContract.Action.UpdatePreviewInput(it)) }
            )
        }
    }
}

/**
 * Top app bar for regex builder screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegexBuilderTopBar(
    canApply: Boolean,
    onNavigateBack: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    TopAppBar(
        title = { Text("Regex Pattern Builder") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset"
                )
            }
            IconButton(
                onClick = onApply,
                enabled = canApply
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Apply"
                )
            }
        }
    )
}

/**
 * Pattern input section with validation.
 */
@Composable
private fun PatternInputSection(
    pattern: String,
    replacement: String,
    validationError: String?,
    validationSuggestion: String?,
    isValidating: Boolean,
    onPatternChange: (String) -> Unit,
    onReplacementChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Pattern",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = pattern,
            onValueChange = onPatternChange,
            label = { Text("Regex Pattern") },
            placeholder = { Text("e.g., \\d+") },
            isError = validationError != null,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                when {
                    isValidating -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Validating...")
                        }
                    }
                    validationError != null -> {
                        Column {
                            Text(
                                text = validationError,
                                color = MaterialTheme.colorScheme.error
                            )
                            validationSuggestion?.let {
                                Text(
                                    text = "Tip: $it",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            trailingIcon = {
                when {
                    isValidating -> null
                    validationError != null -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    pattern.isNotEmpty() -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Valid",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> null
                }
            }
        )

        OutlinedTextField(
            value = replacement,
            onValueChange = onReplacementChange,
            label = { Text("Replacement") },
            placeholder = { Text("e.g., Photo_$1") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Use $1, $2, etc. for capture groups")
            }
        )
    }
}

/**
 * Flags selection section.
 */
@Composable
private fun FlagsSection(
    selectedFlags: Set<RegexFlag>,
    onToggleFlag: (RegexFlag) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Options",
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RegexFlag.values().forEach { flag ->
                FilterChip(
                    selected = flag in selectedFlags,
                    onClick = { onToggleFlag(flag) },
                    label = {
                        Text(
                            when (flag) {
                                RegexFlag.IGNORE_CASE -> "Ignore Case"
                                RegexFlag.MULTILINE -> "Multiline"
                                RegexFlag.DOT_MATCHES_ALL -> "Dot Matches All"
                                RegexFlag.LITERAL -> "Literal"
                            }
                        )
                    },
                    leadingIcon = {
                        if (flag in selectedFlags) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Options section.
 */
@Composable
private fun OptionsSection(
    preserveExtension: Boolean,
    onTogglePreserveExtension: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTogglePreserveExtension)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Preserve File Extension",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Keep the extension unchanged during transformation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = preserveExtension,
            onCheckedChange = { onTogglePreserveExtension() }
        )
    }
}

/**
 * Presets section with categorized chips.
 */
@Composable
private fun PresetsSection(
    selectedPreset: RegexPreset?,
    onPresetClick: (RegexPreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Common Patterns",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Quick presets for common transformations",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Spacing Presets
        PresetCategory(
            title = "Spacing",
            presets = RegexPreset.getSpacingPresets(),
            selectedPreset = selectedPreset,
            onPresetClick = onPresetClick
        )

        // Case Presets
        PresetCategory(
            title = "Case",
            presets = RegexPreset.getCasePresets(),
            selectedPreset = selectedPreset,
            onPresetClick = onPresetClick
        )

        // Cleanup Presets
        PresetCategory(
            title = "Cleanup",
            presets = RegexPreset.getCleanupPresets(),
            selectedPreset = selectedPreset,
            onPresetClick = onPresetClick
        )
    }
}

/**
 * Preset category with horizontal scrollable chips.
 */
@Composable
private fun PresetCategory(
    title: String,
    presets: List<RegexPreset>,
    selectedPreset: RegexPreset?,
    onPresetClick: (RegexPreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presets) { preset ->
                PresetChip(
                    preset = preset,
                    isSelected = preset == selectedPreset,
                    onClick = { onPresetClick(preset) }
                )
            }
        }
    }
}

/**
 * Individual preset chip.
 */
@Composable
private fun PresetChip(
    preset: RegexPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ElevatedFilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Column {
                Text(
                    text = preset.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = preset.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
}

/**
 * Preview section with live transformation.
 */
@Composable
private fun PreviewSection(
    previewInput: String,
    previewResult: String?,
    canPreview: Boolean,
    onPreviewInputChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Live Preview",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = previewInput,
            onValueChange = onPreviewInputChange,
            label = { Text("Sample Input") },
            placeholder = { Text("Enter filename to test") },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = canPreview && previewResult != null) {
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
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Result",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = previewResult ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (previewInput != previewResult && previewResult != null) {
                        Text(
                            text = "Original: $previewInput",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (!canPreview && previewInput.isNotEmpty()) {
            Text(
                text = "Fix validation errors to see preview",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * FlowRow implementation for wrapping items.
 */
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Simple flow row implementation using Column and Row
    // For production, use accompanist FlowRow or wait for Compose 1.4+
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        Row(
            horizontalArrangement = horizontalArrangement
        ) {
            content()
        }
    }
}
