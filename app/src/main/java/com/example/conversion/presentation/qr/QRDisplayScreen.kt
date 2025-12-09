package com.example.conversion.presentation.qr

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.presentation.qr.QRContract.Action
import com.example.conversion.presentation.qr.QRContract.Event
import com.example.conversion.ui.theme.ConversionTheme

/**
 * QR Display Screen.
 * Shows a QR code generated from a rename template.
 */
@Composable
fun QRDisplayScreen(
    template: RenameTemplate,
    viewModel: QRViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onShare: (Bitmap) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Generate QR code when screen loads or size changes
    LaunchedEffect(template, state.qrSize) {
        viewModel.handleAction(Action.GenerateQR(template, state.qrSize))
    }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.QRGenerated -> {
                    // QR generated successfully
                }
                is Event.QRShared -> {
                    state.qrBitmap?.let { onShare(it) }
                }
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is Event.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                else -> { /* Other events not relevant here */ }
            }
        }
    }

    QRDisplayScreenContent(
        state = state,
        template = template,
        onAction = viewModel::handleAction,
        onNavigateBack = onNavigateBack,
        onShare = onShare,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QRDisplayScreenContent(
    state: QRContract.State,
    template: RenameTemplate,
    onAction: (Action) -> Unit,
    onNavigateBack: () -> Unit,
    onShare: (Bitmap) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Template") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Template info card
            TemplateInfoCard(template = template)

            Spacer(modifier = Modifier.height(24.dp))

            // QR Code display
            if (state.isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(300.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Generating QR code...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (state.qrBitmap != null) {
                QRCodeDisplay(
                    bitmap = state.qrBitmap!!,
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Size selector
                QRSizeSelector(
                    currentSize = state.qrSize,
                    onSizeSelected = { size ->
                        onAction(Action.ChangeQRSize(size))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Share button
                Button(
                    onClick = { state.qrBitmap?.let { onShare(it) } },
                    enabled = state.canShare,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share QR Code")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Help text
                Text(
                    text = "Share this QR code with others to let them import your template configuration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (state.error != null) {
                ErrorDisplay(
                    error = state.error!!,
                    onRetry = { onAction(Action.GenerateQR(template, state.qrSize)) }
                )
            }
        }
    }
}

@Composable
private fun TemplateInfoCard(
    template: RenameTemplate,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pattern: ${template.pattern}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Prefix: ${template.config.prefix}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Starting Number: ${template.config.startNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Digits: ${template.config.digitCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun QRCodeDisplay(
    bitmap: Bitmap,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QRSizeSelector(
    currentSize: Int,
    onSizeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${currentSize}x$currentSize px",
            onValueChange = {},
            readOnly = true,
            label = { Text("QR Code Size") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            QRContract.AVAILABLE_SIZES.forEach { size ->
                DropdownMenuItem(
                    text = { Text("${size}x$size px") },
                    onClick = {
                        onSizeSelected(size)
                        expanded = false
                    },
                    leadingIcon = {
                        if (size == currentSize) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorDisplay(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

// Preview
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun QRDisplayScreenPreview() {
    val sampleTemplate = RenameTemplate(
        id = "1",
        name = "Photo Rename",
        pattern = "Photo_001.jpg",
        config = RenameConfig(
            prefix = "Photo_",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true,
            sortStrategy = SortStrategy.NATURAL
        )
    )

    ConversionTheme {
        Surface {
            QRDisplayScreenContent(
                state = QRContract.State(
                    selectedTemplate = sampleTemplate,
                    qrBitmap = null,
                    isGenerating = false
                ),
                template = sampleTemplate,
                onAction = {},
                onNavigateBack = {},
                onShare = {}
            )
        }
    }
}
