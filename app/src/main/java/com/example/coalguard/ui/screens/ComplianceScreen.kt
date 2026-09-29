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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.coalguard.data.model.ComplianceItem
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplianceScreen(
    complianceItems: List<ComplianceItem>,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val sampleItems = remember {
        mutableStateListOf(
            ComplianceItem(id = "DIR-2025-1042", mineId = 42, category = "SAFETY", title = "Installation of Realtime CH4 Gas Monitoring Telemetry", dueDate = "14 Sept 2026", status = "overdue", assignedTo = "Shri R. K. Mahapatra", trackingId = "CMR 2017 Sec 104 - Underground Ventilation"),
            ComplianceItem(id = "DIR-2025-1043", mineId = 43, category = "ENVIRONMENT", title = "InSAR Satellite Subsidence Bench Survey Validation", dueDate = "22 Sept 2026", status = "pending", assignedTo = "Dr. Arindam Sen", trackingId = "DGMS Circular No. 4/2022 - Highwall Stability"),
            ComplianceItem(id = "DIR-2025-1044", mineId = 44, category = "SAFETY", title = "Hydraulic Roof Support & Strata Barricade Recertification", dueDate = "19 Sept 2026", status = "in_progress", assignedTo = "Er. V. K. Sharma", trackingId = "CMR 2017 Reg 124 - Systematic Support Rules"),
            ComplianceItem(id = "DIR-2025-1045", mineId = 45, category = "PRODUCTION", title = "Overhead Heavy Machinery Emergency Cut-off Inspection", dueDate = "16 Sept 2026", status = "overdue", assignedTo = "Inspector S. Roy", trackingId = "DGMS Tech S&T Circular 08 - Heavy Equipment"),
            ComplianceItem(id = "DIR-2025-1046", mineId = 42, category = "LABOUR", title = "Underground Miners Atmospheric PPE & Self-Rescuer Audit", dueDate = "28 Sept 2026", status = "completed", assignedTo = "Shri R. K. Mahapatra", trackingId = "Mines Act 1952 Sec 22A - Personal Protective Equipment")
        )
    }

    val liveItems = remember { mutableStateListOf<ComplianceItem>().apply { addAll(complianceItems.ifEmpty { sampleItems }) } }

    val filteredItems = remember(liveItems, searchQuery, selectedFilter) {
        liveItems.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.id.contains(searchQuery, ignoreCase = true) ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    (item.assignedTo ?: "").contains(searchQuery, ignoreCase = true) ||
                    (item.trackingId ?: "").contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "OVERDUE" -> item.status.lowercase() == "overdue"
                "PENDING" -> item.status.lowercase() == "pending"
                "IN_PROGRESS" -> item.status.lowercase() == "in_progress"
                "COMPLETED" -> item.status.lowercase() == "completed" || item.status.lowercase() == "closed"
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("सत्यमेव जयते | STATUTORY COMPLIANCE & DGMS DIRECTIVES", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Directive", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            // Header Info Card
            item {
                Column {
                    Surface(
                        color = GovtGoldTint,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, GovtGoldAmber)
                    ) {
                        Text(
                            text = "DGMS APEX REGULATORY ENGINE • CMR 2017 DIRECTIVES",
                            color = GovtGoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Statutory Compliance & DGMS Directives",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Track, assign, and audit CMR 2017 mandates, DGMS circulars, and pit safety items.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                }
            }

            // Stat Summary Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ComplianceStatCard("TOTAL DIRECTIVES", "${liveItems.size}", Color(0xFFE0F2FE), GovtNavyPrimary, Modifier.weight(1f))
                    ComplianceStatCard("OVERDUE MANDATES", "${liveItems.count { it.status.lowercase() == "overdue" }}", Color(0xFFFEE2E2), Color(0xFFDC2626), Modifier.weight(1f))
                    ComplianceStatCard("IN PROGRESS", "${liveItems.count { it.status.lowercase() == "in_progress" }}", Color(0xFFFEF3C7), GovtGoldAmber, Modifier.weight(1f))
                    ComplianceStatCard("COMPLIANT", "${liveItems.count { it.status.lowercase() == "completed" }}", Color(0xFFD1FAE5), Color(0xFF059669), Modifier.weight(1f))
                }
            }

            // Search & Filter Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by DIR ID, title, officer, or CMR regulation...", color = GovtTextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GovtNavyPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "ALL" to "All (${liveItems.size})",
                                "OVERDUE" to "Overdue",
                                "PENDING" to "Pending",
                                "IN_PROGRESS" to "In Progress",
                                "COMPLETED" to "Compliant"
                            ).forEach { (filterKey, label) ->
                                FilterChip(
                                    selected = selectedFilter == filterKey,
                                    onClick = { selectedFilter = filterKey },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = if (selectedFilter == filterKey) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GovtNavyPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = GovtSurfaceWhite,
                                        labelColor = GovtTextDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Compliance Items Cards List
            if (filteredItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("All Directives Fully Compliant", style = MaterialTheme.typography.titleMedium, color = GovtTextDark, fontWeight = FontWeight.Bold)
                            Text("No pending or overdue items match search criteria.", fontSize = 12.sp, color = GovtTextMuted)
                        }
                    }
                }
            } else {
                items(filteredItems) { item ->
                    ComplianceDirectiveCard(
                        item = item,
                        onMarkComplied = {
                            val idx = liveItems.indexOfFirst { it.id == item.id }
                            if (idx != -1) {
                                liveItems[idx] = liveItems[idx].copy(status = "completed")
                                Toast.makeText(context, "✅ Directive ${item.id} Marked Complied & Closed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // CREATE NEW STATUTORY DIRECTIVE DIALOG
    if (showCreateDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newRef by remember { mutableStateOf("") }
        var newOfficer by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Issue New Statutory Directive", fontWeight = FontWeight.Bold, color = GovtTextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Directive Title") },
                        placeholder = { Text("e.g. Installation of CH4 Gas Monitoring Telemetry") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newRef,
                        onValueChange = { newRef = it },
                        label = { Text("Statutory Clause (CMR / DGMS Circular)") },
                        placeholder = { Text("e.g. CMR 2017 Sec 104 - Ventilation Rules") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newOfficer,
                        onValueChange = { newOfficer = it },
                        label = { Text("Assigned Official") },
                        placeholder = { Text("e.g. Shri R. K. Mahapatra") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            liveItems.add(
                                0,
                                ComplianceItem(
                                    id = "DIR-2025-${(1050..1999).random()}",
                                    mineId = 42,
                                    category = "SAFETY",
                                    title = newTitle,
                                    dueDate = "30 Sept 2026",
                                    status = "pending",
                                    assignedTo = if (newOfficer.isBlank()) "Shri R. K. Mahapatra" else newOfficer,
                                    trackingId = if (newRef.isBlank()) "CMR 2017 Sec 104 Mandate" else newRef
                                )
                            )
                            Toast.makeText(context, "✅ Statutory Directive Issued Successfully!", Toast.LENGTH_SHORT).show()
                        }
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Issue Mandate", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ComplianceStatCard(label: String, value: String, bgCol: Color, fgCol: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = bgCol, shape = RoundedCornerShape(4.dp)) {
                Text(value, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fgCol)
            }
        }
    }
}

@Composable
fun ComplianceDirectiveCard(item: ComplianceItem, onMarkComplied: () -> Unit) {
    val (statusText, statusBg, statusFg) = when (item.status.lowercase()) {
        "overdue" -> Triple("• OVERDUE", Color(0xFFFEE2E2), Color(0xFFDC2626))
        "in_progress" -> Triple("• IN PROGRESS", Color(0xFFFEF3C7), GovtGoldAmber)
        "pending" -> Triple("• PENDING", Color(0xFFE0F2FE), Color(0xFF0284C7))
        else -> Triple("• COMPLIANT", Color(0xFFD1FAE5), Color(0xFF059669))
    }

    val catColor = when (item.category.uppercase()) {
        "SAFETY" -> Color(0xFFDC2626)
        "ENVIRONMENT" -> Color(0xFF059669)
        "PRODUCTION" -> Color(0xFF7C3AED)
        else -> Color(0xFF0284C7)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, GovtCardBorder)) {
                        Text(
                            text = item.id,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GovtNavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = catColor.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = item.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = catColor
                        )
                    }
                }

                Surface(color = statusBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GovtTextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = item.trackingId ?: "CMR 2017 Regulation Mandate", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Official: ${item.assignedTo ?: "Shri R. K. Mahapatra"}", fontSize = 10.sp, color = GovtTextMuted)
                    Text("Mine: ${item.category} Zone (Mine ID: ${item.mineId})", fontSize = 10.sp, color = GovtTextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("STATUTORY DUE", fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                    Text(item.dueDate, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (item.status.lowercase() == "overdue") Color(0xFFDC2626) else GovtTextDark)
                }
            }

            if (item.status.lowercase() != "completed" && item.status.lowercase() != "closed") {
                HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 10.dp))
                Button(
                    onClick = onMarkComplied,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark Directive Complied & Close", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
