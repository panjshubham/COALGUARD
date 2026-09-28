package com.example.coalguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.data.model.Violation
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.domain.SystemAlert
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    riskScore: Float,
    alerts: List<SystemAlert>,
    violations: List<Violation>,
    pendingSyncCount: Int,
    onNavigateToViolations: () -> Unit,
    onNavigateToRegisters: () -> Unit,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HQ Command Center", fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("ONLINE", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.RadioButtonChecked, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF064E3B),
                                labelColor = Color(0xFF34D399)
                            )
                        )
                    }
                },
                actions = {
                    FilterChip(
                        selected = pendingSyncCount > 0,
                        onClick = {},
                        label = {
                            if (pendingSyncCount > 0) {
                                Text("$pendingSyncCount Pending", color = MaterialTheme.colorScheme.onErrorContainer)
                            } else {
                                Text("All Synced", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        },
                        leadingIcon = {
                            if (pendingSyncCount > 0) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                SupabaseClientInstance.client.auth.signOut()
                            } catch (e: Exception) {
                            } finally {
                                onLogout()
                            }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log out")
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Analytics, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Mine Safety Risk Index", style = MaterialTheme.typography.titleMedium, color = Color(0xFF94A3B8))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (riskScore > 70f) "CRITICAL STATUTORY RISK" else "MODERATE OPERATIONAL RISK",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (riskScore > 70f) Color(0xFFEF4444) else Color(0xFFF59E0B)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(if (riskScore > 70f) Color(0xFF7F1D1D) else Color(0xFF78350F))
                                    .border(2.dp, if (riskScore > 70f) Color(0xFFEF4444) else Color(0xFFF59E0B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = String.format("%.0f", riskScore),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (riskScore > 70f) Color(0xFFFCA5A5) else Color(0xFFFDE68A)
                                    )
                                    Text("/100", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Mandatory Action (Sec 22 Mines Act 1952): Isolate Seam III power, reconstruct Bench 2 haul road berm to 1.65m, and re-certify contractual drivers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToViolations() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Active Breaches", style = MaterialTheme.typography.labelMedium, color = Color(0xFF94A3B8))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${violations.size}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToRegisters() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Statutory Books", style = MaterialTheme.typography.labelMedium, color = Color(0xFF94A3B8))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Form IV / V", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Automated System Alerts (${alerts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("SLA Escalations", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8))
                }
            }

            items(alerts) { alert ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (alert.severity == "critical") Color(0xFF450A0A) else Color(0xFF451A03)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(alert.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            SuggestionChip(
                                onClick = {},
                                label = { Text(alert.severity.uppercase(), style = MaterialTheme.typography.labelSmall) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (alert.severity == "critical") Color(0xFF7F1D1D) else Color(0xFF78350F),
                                    labelColor = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(alert.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                    }
                }
            }

            item {
                Text("Recent Violations Archive", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }

            items(violations) { v ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp),
                    onClick = onNavigateToViolations,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(v.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(v.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8), maxLines = 1)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        AssistChip(
                            onClick = {},
                            label = { Text(v.severity.uppercase(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (v.severity.lowercase() == "critical" || v.severity.lowercase() == "high") Color(0xFF7F1D1D) else Color(0xFF1E293B),
                                labelColor = if (v.severity.lowercase() == "critical" || v.severity.lowercase() == "high") Color(0xFFFCA5A5) else Color(0xFF38BDF8)
                            )
                        )
                    }
                }
            }
        }
    }
}
