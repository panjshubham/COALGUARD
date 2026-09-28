package com.example.coalguard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coalguard.data.model.Violation
import com.example.coalguard.domain.SlaEscalationEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViolationDetailScreen(
    violation: Violation,
    onBack: () -> Unit,
    onComputeHash: () -> Unit
) {
    val slaEngine = remember { SlaEscalationEngine() }
    val slaResult = remember(violation) { slaEngine.evaluateSlaAndEscalation(violation) }

    val geofenceStatus = if (violation.description.contains("FLAGGED_PROXY_AUDIT")) "FLAGGED_PROXY_AUDIT" else "VERIFIED_LOCATION"
    val cmrCitation = if (violation.description.contains("CMR")) {
        violation.description.substringAfter("[Regulation: ").substringBefore("]")
    } else {
        "CMR 2017 Regulation 115"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breach Detail: #${violation.id}") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (slaResult.isBreached) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Statutory SLA Timer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        SuggestionChip(
                            onClick = {},
                            label = { Text("${slaResult.slaHours}h Deadline") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (slaResult.isBreached) {
                            "⚠️ SLA EXPIRED! Automatically Escalated to: ${slaResult.escalatedRole}"
                        } else {
                            "⏳ ${slaResult.hoursRemaining} Hours Remaining until Escalation to: ${slaResult.escalatedRole}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (slaResult.isBreached) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("DGMS Legal Clause Citation", style = MaterialTheme.typography.labelMedium)
                        Text(cmrCitation, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (geofenceStatus == "VERIFIED_LOCATION") Color(0xFF10B981) else MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Raw Satellite Geofence Stamp", style = MaterialTheme.typography.labelMedium)
                            Text("Lat: 23.812°, Lng: 86.441°", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    AssistChip(
                        onClick = {},
                        label = { Text(geofenceStatus, fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (geofenceStatus == "VERIFIED_LOCATION") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }

            if (violation.inspectionId != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("PARENT INSPECTION REPORT", style = MaterialTheme.typography.labelMedium)
                            Text("Discovered during Inspection #INSP-2026-${violation.inspectionId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Title: ${violation.title}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Description: ${violation.description}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status: ${violation.status.uppercase()}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("Severity: ${violation.severity.uppercase()}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHA-256 Ledger Fingerprint", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Data Hash: ${violation.dataHash?.take(32) ?: "Not Sealed"}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text("Previous Hash: ${violation.prevHash?.take(32) ?: "Genesis Link"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }

            Button(onClick = onComputeHash, modifier = Modifier.fillMaxWidth()) {
                Text("RE-COMPUTE HASH & SEAL LEDGER", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Dashboard")
            }
        }
    }
}
