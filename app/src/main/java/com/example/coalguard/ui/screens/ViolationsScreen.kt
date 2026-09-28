package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.coalguard.data.model.Violation
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
fun ViolationsScreen(
    violations: List<Violation>,
    pendingSyncCount: Int = 0,
    onNavigateBack: () -> Unit = {},
    onViolationClick: (Violation) -> Unit = {},
    onSyncClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }

    val sampleViolations = remember {
        listOf(
            Violation(id = 215, mineId = 42, category = "PPE Safety Breach", description = "Worker missing mandatory hard-hat & safety vest in East Pit 3", severity = "critical", status = "open", regulationRef = "CMR 2017 Reg 115", createdAt = "14 Sept 2026, 16:43"),
            Violation(id = 214, mineId = 42, category = "Unsecured Cable Runway", description = "Exposed high-voltage electrical cable near haulage winch", severity = "high", status = "open", regulationRef = "CMR 2017 Reg 83", createdAt = "14 Sept 2026, 15:20"),
            Violation(id = 207, mineId = 43, category = "Berm Erosion Hazard", description = "Haul road bench berm height below 1.5m statutory requirement", severity = "high", status = "open", regulationRef = "CMR 2017 Reg 83", createdAt = "14 Sept 2026, 12:56"),
            Violation(id = 206, mineId = 44, category = "Dust PM10 Concentration", description = "Dust suppression water sprinkler line pressure drop in Seam II", severity = "medium", status = "remediating", regulationRef = "CMR 2017 Reg 129", createdAt = "14 Sept 2026, 12:49"),
            Violation(id = 205, mineId = 45, category = "Contractor VTC Lapsed", description = "M/s Bharat Explosives worker entry without active VTC gate pass", severity = "high", status = "closed", regulationRef = "MINES VTC 1966", createdAt = "14 Sept 2026, 12:48")
        )
    }

    val displayViolations = if (violations.isNotEmpty()) violations else sampleViolations

    val filteredViolations = remember(displayViolations, searchQuery, selectedFilter) {
        displayViolations.filter { v ->
            val matchesQuery = searchQuery.isBlank() ||
                    (v.category ?: "").contains(searchQuery, ignoreCase = true) ||
                    v.description.contains(searchQuery, ignoreCase = true) ||
                    v.id.toString().contains(searchQuery, ignoreCase = true) ||
                    v.regulationRef.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "open" -> v.status.equals("open", ignoreCase = true)
                "remediating" -> v.status.equals("in_progress", ignoreCase = true) || v.status.equals("remediating", ignoreCase = true)
                "closed" -> v.status.equals("resolved", ignoreCase = true) || v.status.equals("closed", ignoreCase = true)
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
                        Text("COAL INDIA LIMITED", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("सत्यमेव जयते | STATUTORY DIRECTIVES & HAZARD FEEDS", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "🔄 Synchronizing live DGMS breach feed...", Toast.LENGTH_SHORT).show()
                        onSyncClick()
                    }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color.White)
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
            // Header Info Section
            item {
                Column {
                    Surface(
                        color = GovtGoldTint,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, GovtGoldAmber)
                    ) {
                        Text(
                            text = "STATUTORY ENFORCEMENT & DIRECTIVES",
                            color = GovtGoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Active Violations & Directives Archive",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Continuous tracking of field breaches, SLA shift escalations, and statutory directives under CMR 2017.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                }
            }

            // Key Metrics Summary Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiSummaryCard(
                        title = "ACTIVE BREACHES",
                        value = "${displayViolations.count { it.status.equals("open", ignoreCase = true) }} Open",
                        badgeBg = Color(0xFFFEF3C7),
                        badgeFg = GovtGoldAmber,
                        modifier = Modifier.weight(1f)
                    )
                    KpiSummaryCard(
                        title = "CRITICAL DIRECTIVES",
                        value = "${displayViolations.count { it.severity.equals("critical", ignoreCase = true) }} Escalated",
                        badgeBg = Color(0xFFFEE2E2),
                        badgeFg = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f)
                    )
                    KpiSummaryCard(
                        title = "RESOLVED TICKETS",
                        value = "${displayViolations.count { it.status.equals("closed", ignoreCase = true) || it.status.equals("resolved", ignoreCase = true) }} Closed",
                        badgeBg = Color(0xFFD1FAE5),
                        badgeFg = Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search & Filter Controls
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
                            placeholder = { Text("Search by breach ID, description, or regulation clause...", color = GovtTextMuted) },
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

                        // Filter Chips Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "all" to "All Breaches (${displayViolations.size})",
                                "open" to "Open Action Required",
                                "remediating" to "In Remediation",
                                "closed" to "Closed"
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

            // Violations List
            if (filteredViolations.isEmpty()) {
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
                            Text("Zero Active Breach Directives", style = MaterialTheme.typography.titleMedium, color = GovtTextDark, fontWeight = FontWeight.Bold)
                            Text("All statutory breach records match search criteria and DGMS safety protocols.", fontSize = 12.sp, color = GovtTextMuted)
                        }
                    }
                }
            } else {
                items(filteredViolations) { violation ->
                    ViolationCardItem(
                        violation = violation,
                        onClick = { onViolationClick(violation) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun KpiSummaryCard(
    title: String,
    value: String,
    badgeBg: Color,
    badgeFg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = badgeBg, shape = RoundedCornerShape(4.dp)) {
                Text(value, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeFg)
            }
        }
    }
}

@Composable
fun ViolationCardItem(
    violation: Violation,
    onClick: () -> Unit
) {
    val mineName = when (violation.mineId) {
        42 -> "Govindpur Colliery (Mine ID: 42)"
        43 -> "Dhori Khas Mine (Mine ID: 43)"
        44 -> "Karo Special Seam (Mine ID: 44)"
        45 -> "Tetaria Khar Colliery (Mine ID: 45)"
        else -> "Govindpur Colliery (Mine ID: 42)"
    }

    val isCritical = violation.severity.equals("critical", ignoreCase = true) || violation.severity.equals("high", ignoreCase = true)
    val severityBg = if (isCritical) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
    val severityFg = if (isCritical) Color(0xFFDC2626) else GovtGoldAmber
    val statusText = when (violation.status.lowercase()) {
        "open" -> "Action Required"
        "remediating", "in_progress" -> "In Remediation"
        else -> "Closed / Sealed"
    }
    val statusBg = when (violation.status.lowercase()) {
        "open" -> Color(0xFFFEE2E2)
        "remediating", "in_progress" -> Color(0xFFFEF3C7)
        else -> Color(0xFFD1FAE5)
    }
    val statusFg = when (violation.status.lowercase()) {
        "open" -> Color(0xFFDC2626)
        "remediating", "in_progress" -> GovtGoldAmber
        else -> Color(0xFF059669)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(color = severityBg, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = (violation.severity ?: "HIGH").uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = severityFg
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = violation.category ?: "Statutory Safety Breach",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GovtTextDark
                    )
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

            Text(
                text = violation.description,
                fontSize = 12.sp,
                color = GovtTextDark,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(mineName, fontSize = 10.sp, color = GovtTextMuted, fontWeight = FontWeight.Medium)
                }

                Surface(color = GovtGoldTint, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = violation.regulationRef ?: "CMR 2017 Reg 115",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GovtGoldAmber
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GovtCardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Logged: ${violation.createdAt ?: "Recent"}",
                    fontSize = 10.sp,
                    color = GovtTextMuted
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Take Action / Inspect", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
