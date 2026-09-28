package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ImportHistoryEntry(
    val id: String,
    val filename: String,
    val targetTable: String,
    val rowsCount: Int,
    val status: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataImportScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTargetTable by remember { mutableStateOf("mines") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    val history = remember {
        mutableStateListOf(
            ImportHistoryEntry("imp-101", "cil_mines_master_2025.csv", "mines", 48, "COMPLETED", "2023-10-15 14:20:00"),
            ImportHistoryEntry("imp-102", "contractor_roster_v3.xlsx", "contractors", 12, "COMPLETED", "2023-10-14 11:45:00"),
            ImportHistoryEntry("imp-103", "statutory_shift_logs.csv", "statutory_registers", 154, "COMPLETED", "2023-10-12 09:30:00")
        )
    }

    fun startSimulatedImport() {
        if (isUploading) return
        isUploading = true
        uploadProgress = 0f

        coroutineScope.launch {
            for (i in 1..10) {
                delay(200)
                uploadProgress = i / 10f
            }

            val newEntry = ImportHistoryEntry(
                id = "imp-${(1000..9999).random()}",
                filename = "bulk_import_${selectedTargetTable}_${System.currentTimeMillis().toString().takeLast(4)}.csv",
                targetTable = selectedTargetTable,
                rowsCount = (25..250).random(),
                status = "COMPLETED",
                timestamp = "Just now"
            )

            history.add(0, newEntry)
            isUploading = false
            Toast.makeText(context, "✅ Successfully ingested ${newEntry.rowsCount} records into [${newEntry.targetTable}]!", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bulk Dataset Ingestion Engine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070D18))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF38BDF8), shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DATASET INGESTION & SCHEMATIC MAPPER", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bulk import or update CIL mine records, contractors, and statutory books from CSV/Excel.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                }
            }

            item {
                Text("Select Target Database Table", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "mines" to "Mines Master",
                        "contractors" to "Contractors",
                        "statutory_registers" to "Statutory Books",
                        "violations" to "Directives"
                    ).forEach { (tableKey, label) ->
                        FilterChip(
                            selected = selectedTargetTable == tableKey,
                            onClick = { selectedTargetTable = tableKey },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1E293B),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF0F172A),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF1E293B), shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Drag & drop CSV/Excel dataset file here", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Supports .csv and .xlsx dataset formats with auto-mapping.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isUploading) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Parsing & Validating Scheme...", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                                    Text("${(uploadProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { uploadProgress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFF38BDF8),
                                    trackColor = Color(0xFF1E293B)
                                )
                            }
                        } else {
                            Button(
                                onClick = { startSimulatedImport() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SELECT & IMPORT CSV / EXCEL FILE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text("Recent Ingestion History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }

            items(history) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item.filename, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Surface(
                                color = Color(0xFF064E3B),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Target: [${item.targetTable}]", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("${item.rowsCount} Records Processed", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Imported: ${item.timestamp}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}
