package com.example.coalguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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

data class PenaltyRecord(
    val id: String,
    val mine: String,
    val reason: String,
    val amount: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    onNavigateBack: () -> Unit = {}
) {
    val penalties = remember {
        listOf(
            PenaltyRecord("PEN-091", "Tetaria Khar (ECL)", "SLA Breach: Missing PPE Gear", "₹25,000", "COLLECTED"),
            PenaltyRecord("PEN-092", "Dhori Khas (CCL)", "Overdue Ventilation Maintenance", "₹50,000", "PENDING"),
            PenaltyRecord("PEN-093", "Govindpur (BCCL)", "Unregistered Contractor Staff", "₹15,000", "COLLECTED"),
            PenaltyRecord("PEN-094", "Karo Special (CCL)", "SLA Breach: Haul Road Defect", "₹75,000", "PENDING")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial & Cost Savings (ROI)", fontWeight = FontWeight.Bold) },
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
                                .background(Color(0xFF10B981), shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("MONETIZATION & PENALTY MITIGATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Platform Monetization, Auto-Enforcement Fines, and Cost Avoidance", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                Surface(color = Color(0xFF064E3B), shape = RoundedCornerShape(8.dp)) {
                                    Text("+12% YoY", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("₹4.2 Cr", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                            Text("YTD Platform Revenue", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                Surface(color = Color(0xFF7F1D1D), shape = RoundedCornerShape(8.dp)) {
                                    Text("ENFORCED", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("₹85.5 L", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Penalties Levied", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                Surface(color = Color(0xFF0C4A6E), shape = RoundedCornerShape(8.dp)) {
                                    Text("+8%", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("₹1.1 Cr", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Contractor Audit Fees", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Surface(color = Color(0xFF78350F), shape = RoundedCornerShape(8.dp)) {
                                    Text("+22% ROI", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("₹12.4 Cr", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Est. Cost Avoidance", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Financial Chart Visualization Placeholder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Interactive Revenue vs Cost Savings Trend Analysis", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("COST AVOIDANCE & ACCIDENT PREVENTION (ROI)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.height(12.dp))

                        RoiProgressItem("Critical Accidents Avoided", "12 Incidents", "₹8.5 Cr Saved (Compensations)", 0.75f, Color(0xFF10B981))
                        Spacer(modifier = Modifier.height(12.dp))
                        RoiProgressItem("Audit Man-Hours Saved", "14,200 Hours", "₹2.1 Cr Saved (Labor Efficiency)", 0.90f, Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.height(12.dp))
                        RoiProgressItem("Unscheduled Production Halts Avoided", "5 Shifts", "₹1.8 Cr Saved (Output Retention)", 0.60f, Color(0xFFC084FC))
                    }
                }
            }

            item {
                Text("Automated Penalty Ledger", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }

            items(penalties) { penalty ->
                val isCollected = penalty.status == "COLLECTED"
                val statusBg = if (isCollected) Color(0xFF064E3B) else Color(0xFF78350F)
                val statusFg = if (isCollected) Color(0xFF34D399) else Color(0xFFFDE68A)

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
                            Text(penalty.mine, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(penalty.amount, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(penalty.reason, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))

                            Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                                Text(penalty.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusFg)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoiProgressItem(title: String, statText: String, savingsText: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
            Text(statText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(savingsText, fontSize = 10.sp, color = Color(0xFF64748B), modifier = Modifier.align(Alignment.End))
    }
}
