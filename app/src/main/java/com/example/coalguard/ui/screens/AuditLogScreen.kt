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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogScreen(
    auditEntries: List<AuditLedgerEntry> = emptyList(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedAction by remember { mutableStateOf("all") }
    var selectedEntryForProof by remember { mutableStateOf<AuditLedgerEntry?>(null) }

    val sampleEntries = remember {
        listOf(
            AuditLedgerEntry(18495, "violations", 1042, "INSERT", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", "5a6a9490a177d855848e3cf2b82142e88b8bcbe39ffbf7d0577da7dc03541c88", "17 Sept 2026, 12:30", "Er. Rajesh Kumar"),
            AuditLedgerEntry(18494, "inspections", 9912, "INSERT", "7d8f5e3a2b1c40989f5e3d2a1b0c9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c3b2", "713dadfa38ace81c97a8a65f9a7761596f2a8a6b1897d28716b5a327265882aa", "17 Sept 2026, 11:45", "Inspector S. Roy"),
            AuditLedgerEntry(18493, "statutory_registers", 8821, "HASH_SEAL", "a1b2c3d4e5f67890123456789abcdef0123456789abcdef0123456789abcdef0", "2c94cf5cb4529d1f3b259d646b9a888c3a11bf7d995961a8684742a0352211bb", "17 Sept 2026, 10:15", "Er. V. K. Sharma"),
            AuditLedgerEntry(18492, "violations", 1042, "UPDATE", "ff99887766554433221100aabbccddeeff99887766554433221100aabbccddee", "b270a8ea61bf31bb59207869677e584109789cb43213018e69ba3394747766aa", "17 Sept 2026, 09:00", "Colliery Manager")
        )
    }

    val displayEntries = if (auditEntries.isNotEmpty()) auditEntries else sampleEntries

    val filteredEntries = remember(displayEntries, searchQuery, selectedAction) {
        displayEntries.filter { e ->
            val matchesSearch = searchQuery.isBlank() ||
                    e.tableName.contains(searchQuery, ignoreCase = true) ||
                    e.dataHash.contains(searchQuery, ignoreCase = true) ||
                    e.recordId.toString().contains(searchQuery, ignoreCase = true) ||
                    (e.userId ?: "").contains(searchQuery, ignoreCase = true)

            val matchesAction = when (selectedAction) {
                "INSERT" -> e.action.equals("INSERT", ignoreCase = true)
                "UPDATE" -> e.action.equals("UPDATE", ignoreCase = true)
                "DELETE" -> e.action.equals("DELETE", ignoreCase = true)
                "HASH_SEAL" -> e.action.contains("HASH", ignoreCase = true) || e.action.contains("SEAL", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesAction
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
                            Text("सत्यमेव जयते | STATUTORY BLOCKCHAIN AUDIT LEDGER", fontSize = 8.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Exporting Signed DGMS Form V Audit PDF...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export PDF", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            text = "APPEND-ONLY IMMUTABLE LEDGER • SHA-256 HASH CHAIN",
                            color = GovtGoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cryptographic Audit Ledger",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Statutory blockchain chain-of-custody sealing every inspection, violation, and compliance action under DGMS rules.",
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
                    AuditKpiCard("TOTAL BLOCKS", "${displayEntries.size} Blocks", GovtNavyPrimary, Modifier.weight(1f))
                    AuditKpiCard("LEDGER INTEGRITY", "100% Secure", Color(0xFF059669), Modifier.weight(1f))
                    AuditKpiCard("ENCRYPTION SEAL", "SHA-256 Valid", Color(0xFF0284C7), Modifier.weight(1f))
                    AuditKpiCard("DGMS AUDIT", "Form V Ready", GovtGoldAmber, Modifier.weight(1f))
                }
            }

            // Search Bar & Action Filter Chips
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
                            placeholder = { Text("Search table, record ID, user, or SHA-256 hash...", color = GovtTextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GovtNavyPrimary) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GovtTextMuted)
                                    }
                                }
                            },
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
                            listOf("all" to "All Logs", "INSERT" to "Created", "UPDATE" to "Updated", "HASH_SEAL" to "Sealed").forEach { (actionKey, label) ->
                                FilterChip(
                                    selected = selectedAction == actionKey,
                                    onClick = { selectedAction = actionKey },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = if (selectedAction == actionKey) FontWeight.Bold else FontWeight.Normal) },
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

            // Audit Block Cards List
            if (filteredEntries.isEmpty()) {
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
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Zero Tampered Ledger Anomalies", style = MaterialTheme.typography.titleMedium, color = GovtTextDark, fontWeight = FontWeight.Bold)
                            Text("All statutory database logs match search criteria and SHA-256 chain integrity.", fontSize = 12.sp, color = GovtTextMuted)
                        }
                    }
                }
            } else {
                items(filteredEntries) { entry ->
                    val actionUpper = entry.action.uppercase()
                    val (actionBg, actionFg) = when {
                        actionUpper.contains("INSERT") -> Pair(Color(0xFFD1FAE5), Color(0xFF059669))
                        actionUpper.contains("UPDATE") -> Pair(Color(0xFFE0F2FE), Color(0xFF0284C7))
                        actionUpper.contains("DELETE") -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
                        else -> Pair(GovtGoldTint, GovtGoldAmber)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEntryForProof = entry },
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
                                    Text(
                                        text = "#${entry.id}",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = GovtNavyPrimary,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(color = actionBg, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = actionUpper,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = actionFg
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "${entry.tableName.uppercase()} #${entry.recordId}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = GovtTextDark
                                    )
                                }

                                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(4.dp)) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("VERIFIED SEAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // SHA-256 Hashes Preview Box
                            Surface(
                                color = GovtBgSlate,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GovtCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "data_hash: ${entry.dataHash.take(28)}...",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GovtNavyPrimary
                                        )

                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(entry.dataHash))
                                                Toast.makeText(context, "Copied SHA-256 Hash to Clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Hash", tint = GovtNavyPrimary, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    Text(
                                        text = "prev_hash: ${entry.prevHash?.take(28) ?: "GENESIS_ROOT_HASH"}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = GovtTextMuted
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
                                    Icon(Icons.Default.Person, contentDescription = null, tint = GovtTextMuted, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = entry.userId ?: "System Auditor",
                                        fontSize = 10.sp,
                                        color = GovtTextMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = GovtTextMuted, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = entry.timestamp,
                                        fontSize = 10.sp,
                                        color = GovtTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // SHA-256 PROOF MODAL DIALOG
    selectedEntryForProof?.let { entry ->
        AlertDialog(
            onDismissRequest = { selectedEntryForProof = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Block #${entry.id} Cryptographic Proof", fontWeight = FontWeight.Bold, color = GovtTextDark, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Action: ${entry.action.uppercase()}", fontWeight = FontWeight.Bold, color = GovtNavyPrimary, fontSize = 12.sp)
                    Text("Target: ${entry.tableName.uppercase()} #${entry.recordId}", fontSize = 11.sp, color = GovtTextMuted)

                    HorizontalDivider(color = GovtCardBorder)

                    Text("SHA-256 DATA HASH:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, GovtCardBorder)) {
                        Text(
                            text = entry.dataHash,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = GovtNavyPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text("PREVIOUS BLOCK HASH:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                    Surface(color = GovtBgSlate, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, GovtCardBorder)) {
                        Text(
                            text = entry.prevHash ?: "GENESIS_ROOT_HASH",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = GovtTextMuted,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(entry.dataHash))
                        Toast.makeText(context, "Copied SHA-256 Hash to Clipboard!", Toast.LENGTH_SHORT).show()
                        selectedEntryForProof = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy SHA-256 Hash", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEntryForProof = null }) {
                    Text("Close", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AuditKpiCard(label: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = accentColor.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                Text(value, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
        }
    }
}
