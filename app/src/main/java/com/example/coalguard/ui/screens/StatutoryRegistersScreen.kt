package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.data.model.AuditLedgerEntry
import com.example.coalguard.ui.components.CoalGuardLogo
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted

// Data Model for the 4 CMR Statutory Books
data class StatutoryRecord(
    val id: Long,
    val registerType: String, // "CMR_153_GAS_TESTING", "CMR_129_OVERMAN_DAILY", "CMR_83_HAUL_ROAD", "DGMS_CIRCULAR_02_HEMM"
    val shift: String,
    val seamOrPit: String,
    val inspectorName: String,
    val inspectorRole: String,
    val keyParams: String,
    val complianceStatus: String, // "COMPLIANT", "WARNING", "STATUTORY_BREACH"
    val regulation: String,
    val remarks: String,
    val createdAt: String,
    val sha256Seal: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
)

// Pre-populated Statutory Records matching DGMS Rules
val SAMPLE_STATUTORY_RECORDS = listOf(
    StatutoryRecord(
        id = 101,
        registerType = "CMR_153_GAS_TESTING",
        shift = "Morning (06:00 - 14:00)",
        seamOrPit = "Seam III - District East Face 4B",
        inspectorName = "Er. Rajesh Kumar",
        inspectorRole = "Statutory Gas Testing Officer (First Class)",
        keyParams = "CH4: 0.45% · CO: 4.0 ppm · O2: 20.4% · Velocity: 1.7 m/s",
        complianceStatus = "COMPLIANT",
        regulation = "CMR 2017 Reg 153 & 155",
        remarks = "Ventilation parameters normal. Flame safety lamp test passed.",
        createdAt = "Today, 08:30 IST"
    ),
    StatutoryRecord(
        id = 102,
        registerType = "CMR_129_OVERMAN_DAILY",
        shift = "Morning (06:00 - 14:00)",
        seamOrPit = "Incline No. 2 - Main Travelling Roadway",
        inspectorName = "Suresh Patel",
        inspectorRole = "Overman (First Class)",
        keyParams = "Strata: Stable · Roof Supports: Intact · Water Inrush: None",
        complianceStatus = "COMPLIANT",
        regulation = "CMR 2017 Reg 129",
        remarks = "Travelling roadway examined. Supports intact and whitewashing clear.",
        createdAt = "Today, 09:15 IST"
    ),
    StatutoryRecord(
        id = 103,
        registerType = "CMR_83_HAUL_ROAD",
        shift = "Morning (06:00 - 14:00)",
        seamOrPit = "Opencast Bench 3 - Haul Road North",
        inspectorName = "Manoj Singh",
        inspectorRole = "Mining Sirdar",
        keyParams = "Berm Height H: 2.2m · Dumper Tyre D: 2.0m · H >= D/2: PASS",
        complianceStatus = "COMPLIANT",
        regulation = "CMR 2017 Reg 83",
        remarks = "Berm height exceeds 1/2 dumper tyre diameter requirement (H >= D/2). Compliant.",
        createdAt = "Today, 10:00 IST"
    ),
    StatutoryRecord(
        id = 104,
        registerType = "DGMS_CIRCULAR_02_HEMM",
        shift = "Morning (06:00 - 14:00)",
        seamOrPit = "Pit 2 Heavy Equipment Bay (CAT 777D)",
        inspectorName = "Inspector S. Roy",
        inspectorRole = "Colliery Mechanical Engineer",
        keyParams = "Fail-Safe Brakes: PASS · AVA Alarm: PASS · Fatigue Sensor: NORMAL",
        complianceStatus = "COMPLIANT",
        regulation = "DGMS S&T Circular 02/2021",
        remarks = "Pre-shift machine inspection passed. All fail-safe audio-visual alarms operational.",
        createdAt = "Today, 07:45 IST"
    ),
    StatutoryRecord(
        id = 105,
        registerType = "CMR_153_GAS_TESTING",
        shift = "Night (22:00 - 06:00)",
        seamOrPit = "Seam III - Blind Heading Gallery B",
        inspectorName = "Er. Rajesh Kumar",
        inspectorRole = "Statutory Gas Testing Officer",
        keyParams = "CH4: 0.95% (BREACH > 0.75%) · CO: 8.5 ppm · Air: 0.35 m/s",
        complianceStatus = "STATUTORY_BREACH",
        regulation = "CMR 2017 Reg 153",
        remarks = "Methane accumulation detected near face! Auxiliary fan duct detached. Power isolated.",
        createdAt = "Yesterday, 23:45 IST"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatutoryRegistersScreen(
    auditEntries: List<AuditLedgerEntry> = emptyList(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedRecordForDetail by remember { mutableStateOf<StatutoryRecord?>(null) }

    val records = remember { SAMPLE_STATUTORY_RECORDS }

    val filteredRecords = records.filter { record ->
        when (selectedFilter) {
            "GAS" -> record.registerType == "CMR_153_GAS_TESTING"
            "OVERMAN" -> record.registerType == "CMR_129_OVERMAN_DAILY"
            "BERM" -> record.registerType == "CMR_83_HAUL_ROAD"
            "HEMM" -> record.registerType == "DGMS_CIRCULAR_02_HEMM"
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoalGuardLogo(size = 32.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("COAL INDIA LIMITED", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text("सत्यमेव जयते | CMR STATUTORY REGISTERS & LOGBOOKS", fontSize = 8.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Initializing Document OCR Scanner for Paper Logbook...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan Logbook", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            // Header Info Banner
            item {
                Column {
                    Surface(
                        color = GovtGoldTint,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, GovtGoldAmber)
                    ) {
                        Text(
                            text = "SMART DOCUMENT DIGITIZATION • 4 CMR STATUTORY BOOKS",
                            color = GovtGoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "CMR Statutory Registers & Books",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Digitized shift logbooks for Gas Testing (Reg 153), Overman Daily Logs (Reg 129), Haul Road Berms (Reg 83), and HEMM Machinery Checklists.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                }
            }

            // Top OCR Scan Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GovtNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Digitize Physical Paper Logbook", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                            Text("Upload or snap photo of paper register to extract parameters using TrOCR / Donut VDU.", fontSize = 11.sp, color = GovtTextMuted)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Launching OCR Camera for Paper Logbook...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Scan OCR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Filter Tabs (4 Statutory Books)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALL" to "All 4 Statutory Books",
                        "GAS" to "CMR 153 — Gas Testing",
                        "OVERMAN" to "CMR 129 — Overman Log",
                        "BERM" to "CMR 83 — Haul Road & Berms",
                        "HEMM" to "DGMS Circular 02 — HEMM"
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

            // List of Statutory Log Cards
            items(filteredRecords) { record ->
                val isCompliant = record.complianceStatus == "COMPLIANT"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRecordForDetail = record },
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (record.registerType) {
                                        "CMR_153_GAS_TESTING" -> Icons.Default.Whatshot
                                        "CMR_129_OVERMAN_DAILY" -> Icons.AutoMirrored.Filled.Assignment
                                        "CMR_83_HAUL_ROAD" -> Icons.Default.PrecisionManufacturing
                                        else -> Icons.Default.Engineering
                                    },
                                    contentDescription = null,
                                    tint = GovtNavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = record.regulation,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GovtTextDark
                                )
                            }

                            // Status Badge
                            Surface(
                                color = if (isCompliant) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isCompliant) Color(0xFF059669) else Color(0xFFDC2626))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isCompliant) "COMPLIANT" else "BREACH",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCompliant) Color(0xFF065F46) else Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = record.seamOrPit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GovtTextDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Extracted Parameters Pill
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = GovtBgSlate,
                            border = BorderStroke(1.dp, GovtCardBorder)
                        ) {
                            Text(
                                text = record.keyParams,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GovtNavyPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${record.inspectorName} • ${record.shift}",
                                fontSize = 10.sp,
                                color = GovtTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "View Details →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GovtGoldAmber
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // STATUTORY RECORD DETAILS MODAL SHEET
    selectedRecordForDetail?.let { r ->
        val isCompliant = r.complianceStatus == "COMPLIANT"

        AlertDialog(
            onDismissRequest = { selectedRecordForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCompliant) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isCompliant) Color(0xFF059669) else Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(r.regulation, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GovtTextDark)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Location: ${r.seamOrPit}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GovtTextDark)
                            Text("Shift: ${r.shift}", fontSize = 11.sp, color = GovtTextMuted)
                            Text("Officer: ${r.inspectorName} (${r.inspectorRole})", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Timestamp: ${r.createdAt}", fontSize = 10.sp, color = GovtTextMuted)
                        }
                    }

                    HorizontalDivider(color = GovtCardBorder)

                    Text("EXTRACTED PARAMETERS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, GovtCardBorder), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = r.keyParams,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GovtNavyPrimary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text("Officer Remarks: ${r.remarks}", fontSize = 11.sp, color = GovtTextDark)

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "SHA-256 Seal: ${r.sha256Seal.take(32)}...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = GovtTextMuted,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedRecordForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Close Logbook Entry", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
