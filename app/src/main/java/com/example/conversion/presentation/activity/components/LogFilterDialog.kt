package com.example.conversion.presentation.activity.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.ActivityStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Dialog for filtering activity logs.
 * Allows users to filter by date range, status, and action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogFilterDialog(
    currentStartDate: LocalDateTime?,
    currentEndDate: LocalDateTime?,
    currentStatus: ActivityStatus?,
    currentAction: String?,
    onApplyFilters: (
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        status: ActivityStatus?,
        action: String?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var startDate by remember { mutableStateOf(currentStartDate) }
    var endDate by remember { mutableStateOf(currentEndDate) }
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var actionFilter by remember { mutableStateOf(currentAction ?: "") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Activity Logs") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Range Section
                Text(
                    text = "Date Range",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Start Date
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showStartDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Start Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = startDate?.let { formatDate(it) } ?: "Not set",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (startDate != null) {
                            TextButton(onClick = { startDate = null }) {
                                Text("Clear")
                            }
                        }
                    }
                }

                // End Date
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showEndDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "End Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = endDate?.let { formatDate(it) } ?: "Not set",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (endDate != null) {
                            TextButton(onClick = { endDate = null }) {
                                Text("Clear")
                            }
                        }
                    }
                }

                Divider()

                // Status Section
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Status options
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == null,
                            onClick = { selectedStatus = null }
                        )
                        Text(
                            text = "All Statuses",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        )
                    }

                    ActivityStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Text(
                                text = status.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

                Divider()

                // Action Filter Section
                Text(
                    text = "Action",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = actionFilter,
                    onValueChange = { actionFilter = it },
                    label = { Text("Action name") },
                    placeholder = { Text("e.g., FILE_RENAMED") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApplyFilters(
                        startDate,
                        endDate,
                        selectedStatus,
                        actionFilter.takeIf { it.isNotBlank() }
                    )
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Date picker dialogs would require additional implementation
    // For now, we'll use simple date selection
    // In a real app, you would use DatePickerDialog or a custom date picker
}

private fun formatDate(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}
