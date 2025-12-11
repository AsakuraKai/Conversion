package com.example.conversion.presentation.qr

import android.content.res.Configuration
import android.graphics.Bitmap
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
 * QR Scanner Screen.
 * Scans QR codes to import rename templates.
 * 
 * Note: This is a MOCK implementation. In production, this would use:
 * - CameraX for camera preview
 * - ZXing for real-time QR code scanning
 * - Image picker for selecting QR images from gallery
 */
@Composable
fun QRScannerScreen(
    viewModel: QRViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTemplateImported: (RenameTemplate) -> Unit,
    onPickImage: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.TemplateScanned -> {
                    // Template scanned, dialog will show
                }
                is Event.TemplateImported -> {
                    onTemplateImported(event.template)
                    snackbarHostState.showSnackbar(
                        message = "Template imported successfully",
                        duration = SnackbarDuration.Short
                    )
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
                else -> { /* Other events */ }
            }
        }
    }

    // Show import dialog when template is scanned
    state.scannedTemplate?.let { template ->
        ImportTemplateDialog(
            template = template,
            onImport = { viewModel.handleAction(Action.ImportTemplate(it)) },
            onDismiss = { viewModel.handleAction(Action.DismissImportDialog) }
        )
    }

    QRScannerScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onNavigateBack = onNavigateBack,
        onPickImage = onPickImage,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QRScannerScreenContent(
    state: QRContract.State,
    onAction: (Action) -> Unit,
    onNavigateBack: () -> Unit,
    onPickImage: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mock scanner UI
            MockScannerDisplay(
                isScanning = state.isScanning,
                onStartScan = {
                    onAction(Action.StartScanning)
                    onPickImage() // Trigger image picker in mock mode
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Instructions
            InstructionsCard()
        }
    }
}

@Composable
private fun MockScannerDisplay(
    isScanning: Boolean,
    onStartScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "QR Code Scanner",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mock Implementation",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isScanning) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Scanning...",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Button(
                    onClick = onStartScan,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select QR Code Image")
                }
            }
        }
    }
}

@Composable
private fun InstructionsCard(
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "How to use",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InstructionItem(
                number = "1",
                text = "Ask someone to share their template QR code with you"
            )
            InstructionItem(
                number = "2",
                text = "Tap 'Select QR Code Image' to pick the QR image"
            )
            InstructionItem(
                number = "3",
                text = "Review the template details and confirm import"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Production Note",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "In production, this will use the camera to scan QR codes in real-time using CameraX and ZXing library.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun InstructionItem(
    number: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun ImportTemplateDialog(
    template: RenameTemplate,
    onImport: (RenameTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null
            )
        },
        title = {
            Text("Import Template?")
        },
        text = {
            Column {
                Text(
                    text = "The following template was found:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pattern: ${template.pattern}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Prefix: ${template.config.prefix}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Start: ${template.config.startNumber}, Digits: ${template.config.digitCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onImport(template) }
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Preview
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun QRScannerScreenPreview() {
    ConversionTheme {
        Surface {
            QRScannerScreenContent(
                state = QRContract.State(
                    isScanning = false
                ),
                onAction = {},
                onNavigateBack = {},
                onPickImage = {}
            )
        }
    }
}

@Preview(name = "Import Dialog", showBackground = true)
@Composable
private fun ImportTemplateDialogPreview() {
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
        ImportTemplateDialog(
            template = sampleTemplate,
            onImport = {},
            onDismiss = {}
        )
    }
}
