package com.example.coalguard.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.data.model.Contractor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorsScreen(
    contractors: List<Contractor> = emptyList(),
    onNavigateBack: () -> Unit = {},
    onContractorClick: (Contractor) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }

    var workerName by remember { mutableStateOf("Rajesh Mahato") }
    var workerRole by remember { mutableStateOf("CAT-777D Heavy Dumper Driver") }
    var vtcStatus by remember { mutableStateOf("VALID") }
    var pmeStatus by remember { mutableStateOf("FIT") }
    var gateVerified by remember { mutableStateOf(false) }

    val sampleContractors = remember {
        listOf(
            Contractor(1, "L&T Mining Services Ltd.", "CIL-CON-9012", "2026-08-31", "https://coalguard.gov.in/docs/lt_license.pdf"),
            Contractor(2, "BGR Mining & Infra Pvt. Ltd.", "CIL-CON-4412", "2025-11-15", "https://coalguard.gov.in/docs/bgr_license.pdf"),
            Contractor(3, "Thriveni Earthmovers Pvt. Ltd.", "CIL-CON-3391", "2024-02-10", "https://coalguard.gov.in/docs/thriveni.pdf"),
            Contractor(4, "Sainik Mining & Allied Services", "CIL-CON-8821", "2026-03-31", "https://coalguard.gov.in/docs/sainik.pdf")
        )
    }

    val displayContractors = if (contractors.isNotEmpty()) contractors else sampleContractors

    val filteredContractors = remember(displayContractors, searchQuery, selectedFilter) {
        displayContractors.filter { c ->
            val matchesSearch = searchQuery.isBlank() ||
                    c.name.contains(searchQuery, ignoreCase = true) ||
                    c.licenseNo.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "active" -> c.licenseExpiry > "2025-01-01"
                "expired" -> c.licenseExpiry <= "2025-01-01"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contractor Fleet & VTC Governance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu")
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
                                .background(Color(0xFFF59E0B), shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CONTRACTOR SAFETY GOVERNANCE", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Mines VTC Rules 1966 & Gate Interlock Verification", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("VTC & PME Gate Pass Interlock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(8.dp)) {
                                Text("Rule 6 & 9", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Worker: $workerName ($workerRole)", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = vtcStatus == "VALID" && pmeStatus == "FIT",
                                onClick = {
                                    workerName = "Rajesh Mahato"
                                    workerRole = "CAT-777D Heavy Dumper Operator"
                                    vtcStatus = "VALID"
                                    pmeStatus = "FIT"
                                    gateVerified = false
                                },
                                label = { Text("Valid Worker", style = MaterialTheme.typography.labelSmall) }
                            )

                            FilterChip(
                                selected = vtcStatus == "EXPIRED",
                                onClick = {
                                    workerName = "Sunil Bauri"
                                    workerRole = "Dumper Co-Driver / Spotter"
                                    vtcStatus = "EXPIRED"
                                    pmeStatus = "FIT"
                                    gateVerified = false
                                },
                                label = { Text("Lapsed VTC", style = MaterialTheme.typography.labelSmall) }
                            )

                            FilterChip(
                                selected = pmeStatus == "EXPIRED_UNFIT",
                                onClick = {
                                    workerName = "Anil Murmu"
                                    workerRole = "Rotary Blast-hole Drill Operator"
                                    vtcStatus = "VALID"
                                    pmeStatus = "EXPIRED_UNFIT"
                                    gateVerified = false
                                },
                                label = { Text("Lapsed PME Medical", style = MaterialTheme.typography.labelSmall) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { gateVerified = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("RUN GATE PASSCARD CHECK")
                        }

                        if (gateVerified) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val isAllowed = vtcStatus == "VALID" && pmeStatus == "FIT"
                            val barrierText = if (isAllowed) "✅ ACCESS GRANTED: BARRIER OPEN" else "⛔ ACCESS DENIED: BARRIER LOCKED"
                            val barrierBg = if (isAllowed) Color(0xFF064E3B) else Color(0xFF7F1D1D)
                            val barrierFg = if (isAllowed) Color(0xFF34D399) else Color(0xFFFCA5A5)

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = barrierBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(barrierText, fontWeight = FontWeight.Bold, color = barrierFg, style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAllowed) "All VTC training and medical PME certifications verified." else "Worker failed gate interlock under Mines VTC Rules 1966.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by contractor name or license no...", color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("all" to "All Fleets", "active" to "Valid License", "expired" to "Expired/Lapsed").forEach { (filterKey, label) ->
                        FilterChip(
                            selected = selectedFilter == filterKey,
                            onClick = { selectedFilter = filterKey },
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

            items(filteredContractors) { contractor ->
                val isExpired = contractor.licenseExpiry <= "2025-01-01"
                val statusText = if (isExpired) "EXPIRED" else "ACTIVE"
                val statusBg = if (isExpired) Color(0xFF7F1D1D) else Color(0xFF064E3B)
                val statusFg = if (isExpired) Color(0xFFFCA5A5) else Color(0xFF34D399)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 88.dp),
                    onClick = { onContractorClick(contractor) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = contractor.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                color = statusBg,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = statusFg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Engineering, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("License: ${contractor.licenseNo}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            }

                            Text("Expiry: ${contractor.licenseExpiry}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }
    }
}
