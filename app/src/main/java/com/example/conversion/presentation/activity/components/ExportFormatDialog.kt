package com.example.conversion.presentation.activity.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.ExportFormat

/**
 * Dialog for selecting export format for activity logs.
 * Allows users to choose between CSV and JSON formats.
 */
@Composable
fun ExportFormatDialog(
    onExport: (ExportFormat) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf(ExportFormat.CSV) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null
            )
        },
        title = { Text("Export Activity Logs") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Choose export format:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CSV Option
                ExportFormatOption(
                    format = ExportFormat.CSV,
                    title = "CSV (Comma-Separated Values)",
                    description = "Spreadsheet-friendly format for Excel, Google Sheets, etc.",
                    icon = Icons.Default.Description,
                    isSelected = selectedFormat == ExportFormat.CSV,
                    onClick = { selectedFormat = ExportFormat.CSV }
                )

                // JSON Option
                ExportFormatOption(
                    format = ExportFormat.JSON,
                    title = "JSON (JavaScript Object Notation)",
                    description = "Structured format for developers and data analysis",
                    icon = Icons.Default.Code,
                    isSelected = selectedFormat == ExportFormat.JSON,
                    onClick = { selectedFormat = ExportFormat.JSON }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onExport(selectedFormat) }
            ) {
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ExportFormatOption(
    format: ExportFormat,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null // null because the whole card is clickable
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
