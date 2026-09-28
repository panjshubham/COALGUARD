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
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted

data class Section22Directive(
    val id: String,
    val mineName: String,
    val clause: String,
    val summary: String,
    val status: String,
    val issuedDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegulatorPortalScreen(
    onNavigateBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavigateToViolations: () -> Unit = {},
    onNavigateToRegisters: () -> Unit = {}
) {
    val context = LocalContext.current
    var showIssueOrderDialog by remember { mutableStateOf(false) }

    val directivesList = remember {
        mutableStateListOf(
            Section22Directive("DIR-2026-901", "Tetaria Khar Colliery", "Mines Act 1952 Sec 22", "Immediate power isolation to Seam III due to CH4 gas threshold breach", "PROHIBITION ACTIVE", "14 Sept 2026"),
            Section22Directive("DIR-2026-882", "Govindpur Colliery", "CMR 2017 Reg 83", "Reconstruct haul road bench 2 berm to 1.8m height within 48 hours", "IMPROVEMENT ORDER", "12 Sept 2026"),
            Section22Directive("DIR-2026-854", "Dhori Khas Mine", "CMR 2017 Reg 115", "Mandatory recertification of all hydraulic roof supports before next shift", "COMPLIANCE NOTICE", "10 Sept 2026")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("DIRECTORATE GENERAL OF MINES SAFETY", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(6.dp)) {
                                Text("DGMS REGULATOR", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Text("सत्यमेव जयते | STATUTORY OVERSIGHT & ENFORCEMENT PORTAL", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFDC2626)) {
                                    Text("4", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White, modifier = Modifier.size(22.dp))
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
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GovtGoldTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = GovtGoldAmber, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Shri S. K. Verma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                Text("DGMS Chief Inspector of Mines • Dhanbad Region I", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                            }
                            Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                                Text("AUDITOR VERIFIED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            }
                        }
                    }
                }
            }

            // Stat Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("PROHIBITION ORDERS", fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("3 Active", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFDC2626))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("FORM V AUDITS", fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("42 Dossiers", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF059669))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("MONITORED BREACHES", fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("57 Unsealed", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GovtGoldAmber)
                        }
                    }
                }
            }

            // Issue Statutory Directive Action Bar
            item {
                Button(
                    onClick = { showIssueOrderDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("+ Issue Statutory Section 22 Directive Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Active Statutory Directives List
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ACTIVE DGMS SECTION 22 DIRECTIVES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            Text("${directivesList.size} Directives", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        directivesList.forEach { dir ->
                            val statusBg = if (dir.status.contains("PROHIBITION")) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                            val statusFg = if (dir.status.contains("PROHIBITION")) Color(0xFFDC2626) else GovtGoldAmber

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(dir.mineName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                                        Text(dir.clause, fontSize = 10.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                                    }

                                    Surface(color = statusBg, shape = RoundedCornerShape(4.dp)) {
                                        Text(dir.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusFg)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(dir.summary, fontSize = 11.sp, color = GovtTextDark)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Issued: ${dir.issuedDate}", fontSize = 10.sp, color = GovtTextMuted)
                            }
                            HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }

            // Quick Nav Shortcuts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToViolations,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mine Violations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onNavigateToRegisters,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GovtNavyPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CMR Logbooks", color = GovtNavyPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // ISSUE SECTION 22 ORDER DIALOG
    if (showIssueOrderDialog) {
        var mineNameInput by remember { mutableStateOf("Tetaria Khar Colliery") }
        var clauseInput by remember { mutableStateOf("Mines Act 1952 Section 22") }
        var orderTextInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showIssueOrderDialog = false },
            title = { Text("Issue Statutory Section 22 Order", fontWeight = FontWeight.Bold, color = GovtTextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mineNameInput,
                        onValueChange = { mineNameInput = it },
                        label = { Text("Mine / Colliery Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = clauseInput,
                        onValueChange = { clauseInput = it },
                        label = { Text("Statutory Act / Regulation Clause") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = orderTextInput,
                        onValueChange = { orderTextInput = it },
                        label = { Text("Order Directives & Immediate Requirements") },
                        placeholder = { Text("e.g. Stop coal extraction in Seam II until roof supports are recertified.") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (orderTextInput.isNotBlank()) {
                            directivesList.add(
                                0,
                                Section22Directive(
                                    id = "DIR-2026-${(100..999).random()}",
                                    mineName = mineNameInput,
                                    clause = clauseInput,
                                    summary = orderTextInput,
                                    status = "PROHIBITION ACTIVE",
                                    issuedDate = "Just Now"
                                )
                            )
                            Toast.makeText(context, "✅ Statutory Section 22 Prohibition Order Issued!", Toast.LENGTH_LONG).show()
                        }
                        showIssueOrderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Issue Statutory Order", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIssueOrderDialog = false }) {
                    Text("Cancel", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
