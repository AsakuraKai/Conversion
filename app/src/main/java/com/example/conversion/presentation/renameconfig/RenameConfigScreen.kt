package com.example.conversion.presentation.renameconfig

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Action
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Event
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Batch Rename Configuration Screen.
 * Allows users to configure rename settings and preview the result.
 */
@Composable
fun RenameConfigScreen(
    viewModel: RenameConfigViewModel = hiltViewModel(),
    onNavigateToPreview: (RenameConfig) -> Unit,
    onNavigateBack: () -> Unit
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
                is Event.NavigateToPreview -> {
                    onNavigateToPreview(event.config)
                }
                is Event.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    RenameConfigContent(
        state = state,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenameConfigContent(
    state: RenameConfigContract.State,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Batch Rename Configuration") },
                navigationIcon = {
                    IconButton(onClick = { onAction(Action.Back) }) {
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { onAction(Action.Confirm) },
                        enabled = state.canProceed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue to Preview")
                    }
                    
                    if (state.selectedFileCount > 0) {
                        Text(
                            text = "${state.selectedFileCount} file${if (state.selectedFileCount > 1) "s" else ""} selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )
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
            // Prefix Input
            PrefixInputSection(
                prefix = state.config.prefix,
                validationError = state.validationError,
                showValidation = state.showValidation,
                onPrefixChange = { onAction(Action.UpdatePrefix(it)) }
            )

            // Start Number Input
            StartNumberInputSection(
                startNumber = state.config.startNumber,
                onStartNumberChange = { onAction(Action.UpdateStartNumber(it)) }
            )

            // Digit Count Slider
            DigitCountSection(
                digitCount = state.config.digitCount,
                onDigitCountChange = { onAction(Action.UpdateDigitCount(it)) }
            )

            // Preserve Extension Toggle
            PreserveExtensionSection(
                preserveExtension = state.config.preserveExtension,
                onToggle = { onAction(Action.TogglePreserveExtension(it)) }
            )

            // Sort Strategy Dropdown
            SortStrategySection(
                sortStrategy = state.config.sortStrategy,
                onStrategyChange = { onAction(Action.UpdateSortStrategy(it)) }
            )

            // Preview Card
            if (state.hasValidPreview) {
                PreviewCard(
                    previewFilename = state.previewFilename
                )
            }
        }
    }
}

@Composable
private fun PrefixInputSection(
    prefix: String,
    validationError: String?,
    showValidation: Boolean,
    onPrefixChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = prefix,
            onValueChange = onPrefixChange,
            label = { Text("Prefix") },
            placeholder = { Text("e.g., vacation_") },
            supportingText = {
                if (showValidation && validationError != null) {
                    Text(validationError)
                } else {
                    Text("Prefix to add before each filename")
                }
            },
            isError = showValidation && validationError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StartNumberInputSection(
    startNumber: Int,
    onStartNumberChange: (Int) -> Unit
) {
    var textValue by remember(startNumber) { mutableStateOf(startNumber.toString()) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                textValue = newValue
                newValue.toIntOrNull()?.let { number ->
                    if (number >= 0) {
                        onStartNumberChange(number)
                    }
                }
            },
            label = { Text("Start Number") },
            placeholder = { Text("1") },
            supportingText = { Text("Starting number for sequential numbering") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DigitCountSection(
    digitCount: Int,
    onDigitCountChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Digit Count",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = digitCount.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = digitCount.toFloat(),
            onValueChange = { onDigitCountChange(it.toInt()) },
            valueRange = 1f..6f,
            steps = 4,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Number of digits for padding (e.g., 3 = 001, 002, 003)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PreserveExtensionSection(
    preserveExtension: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Preserve Extension",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Keep original file extension (.jpg, .png, etc.)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = preserveExtension,
            onCheckedChange = onToggle
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortStrategySection(
    sortStrategy: SortStrategy,
    onStrategyChange: (SortStrategy) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Sort Files By",
            style = MaterialTheme.typography.titleMedium
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = sortStrategy.toDisplayName(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                SortStrategy.entries.forEach { strategy ->
                    DropdownMenuItem(
                        text = { Text(strategy.toDisplayName()) },
                        onClick = {
                            onStrategyChange(strategy)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Text(
            text = "Order in which files will be renamed",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PreviewCard(
    previewFilename: String
) {
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
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = previewFilename,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Example of how your files will be named",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Converts SortStrategy enum to human-readable display name.
 */
private fun SortStrategy.toDisplayName(): String {
    return when (this) {
        SortStrategy.NATURAL -> "Natural Order"
        SortStrategy.DATE_MODIFIED -> "Date Modified"
        SortStrategy.SIZE -> "File Size"
        SortStrategy.ORIGINAL_ORDER -> "Original Order"
    }
}

// ============== Preview Functions ==============

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun RenameConfigScreenPreview() {
    ConversionTheme {
        RenameConfigContent(
            state = RenameConfigContract.State(
                config = RenameConfig(
                    prefix = "vacation_",
                    startNumber = 1,
                    digitCount = 3,
                    preserveExtension = true,
                    sortStrategy = SortStrategy.NATURAL
                ),
                previewFilename = "vacation_001.jpg",
                selectedFileCount = 5
            ),
            onAction = {}
        )
    }
}

@Preview(name = "With Validation Error", showBackground = true)
@Composable
private fun RenameConfigScreenErrorPreview() {
    ConversionTheme {
        RenameConfigContent(
            state = RenameConfigContract.State(
                config = RenameConfig(
                    prefix = "photo/test",
                    startNumber = 1,
                    digitCount = 3,
                    preserveExtension = true,
                    sortStrategy = SortStrategy.NATURAL
                ),
                validationError = "Prefix contains illegal characters (< > : \" / \\ | ? *)",
                selectedFileCount = 3
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun RenameConfigScreenEmptyPreview() {
    ConversionTheme {
        RenameConfigContent(
            state = RenameConfigContract.State(
                selectedFileCount = 0
            ),
            onAction = {}
        )
    }
}
