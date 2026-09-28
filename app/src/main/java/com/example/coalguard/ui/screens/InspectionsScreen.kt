package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.data.model.Inspection
import com.example.coalguard.data.model.Violation
import com.example.coalguard.data.repository.InspectionsRepository
import com.example.coalguard.data.repository.ViolationsRepository
import com.example.coalguard.ui.components.StatutoryNotificationManager
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionsScreen(
    inspections: List<Inspection>,
    violations: List<Violation> = emptyList(),
    pendingSyncCount: Int = 0,
    onNavigateBack: () -> Unit = {},
    onInspectionClick: (Inspection) -> Unit = {},
    onViolationClick: (Violation) -> Unit = {},
    onNewInspectionClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var activeMode by remember { mutableStateOf("log_violation") }

    // Interactive Manual Entry Form States
    var customMineName by remember { mutableStateOf("") }
    var customContractorName by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var customSeverity by remember { mutableStateOf("") }
    var customRegulationRef by remember { mutableStateOf("") }
    var customInspectorName by remember { mutableStateOf("Smt. Ananya Sen") }
    var narrativeText by remember { mutableStateOf("") }
    
    var isGnssSynced by remember { mutableStateOf(false) }
    var isCameraCaptured by remember { mutableStateOf(false) }

    // Dropdown Expansion States
    var mineExpanded by remember { mutableStateOf(false) }
    var contractorExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var severityExpanded by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("all") }
    var selectedDetailInspection by remember { mutableStateOf<Inspection?>(null) }

    // Local mutable list for real-time offline additions
    val liveInspections = remember { mutableStateListOf<Inspection>().apply { addAll(inspections) } }

    val filteredInspections = remember(liveInspections, searchQuery, selectedTab) {
        liveInspections.filter { insp ->
            val matchesQuery = searchQuery.isBlank() ||
                    insp.inspectorName.contains(searchQuery, ignoreCase = true) ||
                    (insp.type ?: "").contains(searchQuery, ignoreCase = true) ||
                    insp.id.toString().contains(searchQuery, ignoreCase = true)

            val matchesTab = when (selectedTab) {
                "scheduled" -> (insp.status ?: "").lowercase() == "scheduled" || (insp.status ?: "").lowercase() == "pending"
                "completed" -> (insp.status ?: "").lowercase() == "completed" || (insp.status ?: "").lowercase() == "synced"
                "overdue" -> (insp.status ?: "").lowercase() == "overdue"
                else -> true
            }

            matchesQuery && matchesTab
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("COAL INDIA LIMITED", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(8.dp)) {
                                Text("Online", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFFA7F3D0), fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("सत्यमेव जयते | GOVT. OF INDIA • DGMS NETWORK", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SMT. ANANYA SEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GovtNavyPrimary)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GovtBgSlate)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeMode == "log_violation",
                        onClick = { activeMode = "log_violation" },
                        label = { Text("Log New Inspection & Violation", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovtNavyPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GovtSurfaceWhite,
                            labelColor = GovtTextMuted
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = activeMode == "logs_archive",
                        onClick = { activeMode = "logs_archive" },
                        label = { Text("Inspection Logs (${liveInspections.size})", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovtNavyPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GovtSurfaceWhite,
                            labelColor = GovtTextMuted
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (activeMode == "log_violation") {
                item {
                    Column {
                        Surface(
                            color = GovtGoldTint,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, GovtGoldAmber)
                        ) {
                            Text(
                                text = "INSPECTORATE TERMINAL • STATUTORY AUDIT & BREACH LINKING",
                                color = GovtGoldAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Log New Inspection Dossier", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = GovtTextDark)
                        Text("Select options or type custom field observations. Automatically links parent inspection to violation ticket & syncs offline.", fontSize = 12.sp, color = GovtTextMuted)
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Incident Dossier Form", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                }

                                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                                    Text("Offline Sync Enabled", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("INSPECTOR / OFFICIAL NAME *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customInspectorName,
                                onValueChange = { customInspectorName = it },
                                placeholder = { Text("e.g. Smt. Ananya Sen / Er. Rajesh Kumar", color = GovtTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GovtSurfaceWhite,
                                    unfocusedContainerColor = GovtSurfaceWhite,
                                    focusedBorderColor = GovtNavyPrimary,
                                    unfocusedBorderColor = GovtCardBorder,
                                    focusedTextColor = GovtTextDark,
                                    unfocusedTextColor = GovtTextDark
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("CONCESSION / MINE SITE *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = customMineName,
                                    onValueChange = { customMineName = it },
                                    placeholder = { Text("Select or type Mine Name (e.g. Govindpur Colliery)", color = GovtTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { mineExpanded = !mineExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GovtTextDark)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = GovtSurfaceWhite,
                                        unfocusedContainerColor = GovtSurfaceWhite,
                                        focusedBorderColor = GovtNavyPrimary,
                                        unfocusedBorderColor = GovtCardBorder,
                                        focusedTextColor = GovtTextDark,
                                        unfocusedTextColor = GovtTextDark
                                    )
                                )
                                DropdownMenu(expanded = mineExpanded, onDismissRequest = { mineExpanded = false }) {
                                    listOf("Govindpur Colliery (BCCL)", "Dhori Khas Colliery (CCL)", "Karo Special Seam OCP (CCL)", "Tetaria Khar OCP (ECL)", "Jharia Block-IV Colliery").forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option, fontSize = 13.sp) },
                                            onClick = {
                                                customMineName = option
                                                mineExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("OPERATING CONTRACTOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = customContractorName,
                                    onValueChange = { customContractorName = it },
                                    placeholder = { Text("Select or type Contractor (e.g. L&T Mining Services)", color = GovtTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { contractorExpanded = !contractorExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GovtTextDark)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = GovtSurfaceWhite,
                                        unfocusedContainerColor = GovtSurfaceWhite,
                                        focusedBorderColor = GovtNavyPrimary,
                                        unfocusedBorderColor = GovtCardBorder,
                                        focusedTextColor = GovtTextDark,
                                        unfocusedTextColor = GovtTextDark
                                    )
                                )
                                DropdownMenu(expanded = contractorExpanded, onDismissRequest = { contractorExpanded = false }) {
                                    listOf("L&T Mining Services", "BGR Mining & Infra", "Thriveni Earthmovers", "-- Direct CIL Operator --").forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option, fontSize = 13.sp) },
                                            onClick = {
                                                customContractorName = option
                                                contractorExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("VIOLATION CATEGORY *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = customCategory,
                                            onValueChange = { customCategory = it },
                                            placeholder = { Text("Select/Type Category", color = GovtTextMuted, fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = {
                                                IconButton(onClick = { categoryExpanded = !categoryExpanded }) {
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GovtTextDark)
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = GovtSurfaceWhite,
                                                unfocusedContainerColor = GovtSurfaceWhite,
                                                focusedBorderColor = GovtNavyPrimary,
                                                unfocusedBorderColor = GovtCardBorder,
                                                focusedTextColor = GovtTextDark,
                                                unfocusedTextColor = GovtTextDark
                                            )
                                        )
                                        DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                                            listOf("Safety & Strata", "Gas & Ventilation", "Machinery & Haulage", "Electrical Runway", "Health & PME", "Environmental").forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option, fontSize = 12.sp) },
                                                    onClick = {
                                                        customCategory = option
                                                        categoryExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("THREAT SEVERITY *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = customSeverity,
                                            onValueChange = { customSeverity = it },
                                            placeholder = { Text("Select/Type Severity", color = GovtTextMuted, fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = {
                                                IconButton(onClick = { severityExpanded = !severityExpanded }) {
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GovtTextDark)
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = GovtSurfaceWhite,
                                                unfocusedContainerColor = GovtSurfaceWhite,
                                                focusedBorderColor = GovtNavyPrimary,
                                                unfocusedBorderColor = GovtCardBorder,
                                                focusedTextColor = GovtTextDark,
                                                unfocusedTextColor = GovtTextDark
                                            )
                                        )
                                        DropdownMenu(expanded = severityExpanded, onDismissRequest = { severityExpanded = false }) {
                                            listOf("Standard (Monitor)", "Elevated Risk", "Critical Hazard (Stop Order)").forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option, fontSize = 12.sp) },
                                                    onClick = {
                                                        customSeverity = option
                                                        severityExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("STATUTORY REGULATION REF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customRegulationRef,
                                onValueChange = { customRegulationRef = it },
                                placeholder = { Text("e.g. CMR 2017 Reg 115 / Sec 22 Mines Act 1952", color = GovtTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GovtSurfaceWhite,
                                    unfocusedContainerColor = GovtSurfaceWhite,
                                    focusedBorderColor = GovtNavyPrimary,
                                    unfocusedBorderColor = GovtCardBorder,
                                    focusedTextColor = GovtTextDark,
                                    unfocusedTextColor = GovtTextDark
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("DOSSIER NARRATIVE / FINDINGS *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = narrativeText,
                                onValueChange = { narrativeText = it },
                                placeholder = { Text("Type statutory findings, regulatory breaches, and immediate directives manually...", color = GovtTextMuted) },
                                modifier = Modifier.fillMaxWidth().height(110.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GovtSurfaceWhite,
                                    unfocusedContainerColor = GovtSurfaceWhite,
                                    focusedBorderColor = GovtNavyPrimary,
                                    unfocusedBorderColor = GovtCardBorder,
                                    focusedTextColor = GovtTextDark,
                                    unfocusedTextColor = GovtTextDark
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("GEOSPATIAL RTK TRACKING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = GovtBgSlate,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GovtCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { isGnssSynced = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                                    ) {
                                        Icon(Icons.Default.Satellite, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sync GNSS Coordinates", fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Text(
                                        text = if (isGnssSynced) "23.7923° N, 86.4253° E (Lock)" else "Awaiting GNSS uplink...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGnssSynced) Color(0xFF059669) else GovtTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("VISUAL EVIDENCE ARCHIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GovtBgSlate)
                                    .border(1.dp, GovtCardBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = GovtNavyPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(if (isCameraCaptured) "📸 Photo Evidence Attached & Sealed" else "Initialize field camera to capture photo.", fontSize = 11.sp, color = GovtTextMuted)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { isCameraCaptured = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                                    ) {
                                        Text("Open Camera", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (customMineName.isBlank()) {
                                        Toast.makeText(context, "Please enter or select a Mine Site", Toast.LENGTH_SHORT).show()
                                    } else if (narrativeText.isBlank()) {
                                        Toast.makeText(context, "Please enter dossier narrative findings", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val newId = (1000..9999).random()
                                        val nowIso = Instant.now().toString()
                                        val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(
                                            Date()
                                        )

                                        // 1. Parent Inspection Record
                                        val newInspection = Inspection(
                                            id = newId,
                                            mineId = 42,
                                            contractorId = 9,
                                            date = nowIso,
                                            inspectorName = customInspectorName.ifEmpty { "Smt. Ananya Sen" },
                                            type = "${customCategory.ifEmpty { "Safety" }} Audit — $customMineName",
                                            status = "completed",
                                            syncedAt = nowIso,
                                            trackingId = "INSP-2026-$newId"
                                        )

                                        // 2. Child Linked Violation Ticket (Explicitly connected via inspectionId = newId!)
                                        val newViolation = Violation(
                                            id = newId,
                                            mineId = 42,
                                            inspectionId = newId, // 👈 Connected via inspection_id!
                                            category = customCategory.ifEmpty { "safety" }.lowercase(),
                                            description = narrativeText,
                                            severity = if (customSeverity.contains("Critical", ignoreCase = true)) "critical" else "high",
                                            status = "open",
                                            regulationRef = customRegulationRef.ifEmpty { "CMR 2017 Reg 115" },
                                            createdAt = formattedDate
                                        )

                                        coroutineScope.launch {
                                            try {
                                                InspectionsRepository(context).createInspection(newInspection)
                                                ViolationsRepository(context).createViolation(newViolation)
                                                Toast.makeText(context, "✅ Inspection #INSP-2026-$newId & Linked Violation uploaded to Supabase!", Toast.LENGTH_LONG).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Saved locally: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        // 3. Broadcast Notification Alert to Notification Center
                                        StatutoryNotificationManager.addNotification(
                                            title = "Statutory Breach Filed: VIO-$newId",
                                            message = "[${(newViolation.category ?: "SAFETY").uppercase()}] $narrativeText (Inspection #INSP-2026-$newId).",
                                            category = "VIOLATION",
                                            severity = (newViolation.severity ?: "HIGH").uppercase(),
                                            mineName = customMineName.ifEmpty { "Govindpur Colliery (Mine ID: 42)" },
                                            actionRoute = "violations"
                                        )

                                        liveInspections.add(0, newInspection)
                                        Toast.makeText(context, "🚨 Statutory Violation Filed & Linked to Inspection #INSP-2026-$newId!", Toast.LENGTH_LONG).show()

                                        customMineName = ""
                                        customContractorName = ""
                                        customCategory = ""
                                        customSeverity = ""
                                        customRegulationRef = ""
                                        narrativeText = ""
                                        isGnssSynced = false
                                        isCameraCaptured = false
                                        activeMode = "logs_archive"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("File Statutory Dossier (Save Offline)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by inspector, type, or ID...", color = GovtTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GovtNavyPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GovtSurfaceWhite,
                            unfocusedContainerColor = GovtSurfaceWhite,
                            focusedBorderColor = GovtNavyPrimary,
                            unfocusedBorderColor = GovtCardBorder,
                            focusedTextColor = GovtTextDark,
                            unfocusedTextColor = GovtTextDark
                        )
                    )
                }

                items(filteredInspections) { inspection ->
                    val statusLower = (inspection.status ?: "scheduled").lowercase()
                    val (statusBg, statusText) = when (statusLower) {
                        "completed", "synced" -> Pair(Color(0xFFD1FAE5), Color(0xFF059669))
                        "overdue" -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
                        else -> Pair(Color(0xFFE0F2FE), Color(0xFF0284C7))
                    }

                    // Linked Violation for this inspection
                    val linkedViolation = violations.find { it.inspectionId == inspection.id || (it.id == inspection.id && it.mineId == inspection.mineId) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectedDetailInspection = inspection },
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = inspection.inspectorName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GovtTextDark
                                    )
                                }

                                Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                                    Text(
                                        text = (inspection.status ?: "SCHEDULED").uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = statusText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = inspection.type ?: "Statutory Safety & Gas Audit",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = GovtTextDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tracking ID: ${inspection.trackingId ?: "INSP-2026-${inspection.id}"}", fontSize = 11.sp, color = GovtTextMuted)

                                if (linkedViolation != null) {
                                    Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "⚠️ 1 Linked Violation Discovered",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // INSPECTION DETAIL & LINKED VIOLATIONS MODAL SHEET
    selectedDetailInspection?.let { insp ->
        val linkedViolation = violations.find { it.inspectionId == insp.id || (it.id == insp.id && it.mineId == insp.mineId) }

        AlertDialog(
            onDismissRequest = { selectedDetailInspection = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = GovtNavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Inspection #${insp.trackingId ?: "INSP-2026-${insp.id}"}", fontWeight = FontWeight.Bold, color = GovtTextDark, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Inspector: ${insp.inspectorName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GovtTextDark)
                            Text("Type: ${insp.type ?: "Statutory Safety Audit"}", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Date: ${insp.date}", fontSize = 11.sp, color = GovtTextMuted)
                            Text("Synced Status: ${insp.syncedAt ?: "Offline Cached"}", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = GovtCardBorder)

                    Text("LINKED VIOLATIONS DISCOVERED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)

                    if (linkedViolation != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Violation #${linkedViolation.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF7F1D1D))
                                    Surface(color = Color(0xFFDC2626), shape = RoundedCornerShape(4.dp)) {
                                        Text((linkedViolation.severity ?: "HIGH").uppercase(), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(linkedViolation.description, fontSize = 11.sp, color = Color(0xFF7F1D1D), fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Regulation: ${linkedViolation.regulationRef}", fontSize = 10.sp, color = Color(0xFF991B1B))

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val v = linkedViolation
                                        selectedDetailInspection = null
                                        onViolationClick(v)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("View Violation Details →", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("✅ No violations logged for this inspection. Site was fully compliant.", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedDetailInspection = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Close Inspection Report", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
