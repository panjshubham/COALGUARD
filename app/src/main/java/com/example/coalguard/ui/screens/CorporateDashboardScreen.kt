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
import com.example.coalguard.ui.components.CoalGuardLogo
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted

data class EcLimitCardItem(
    val mineName: String,
    val ecRef: String,
    val ceilingPercent: Double,
    val ceilingLabel: String,
    val extractedMt: Double,
    val statutoryCapMt: Double,
    val railDispatchMt: Double,
    val stockText: String,
    val alertText: String,
    val isCritical: Boolean
)

data class RiskRankedSubsidiary(
    val id: Int,
    val name: String,
    val riskIndex: Int,
    val shapFactors: List<Pair<String, Double>>,
    val recommendation: String
)

data class LiveViolationFeedItem(
    val id: Int,
    val mineName: String,
    val category: String,
    val severity: String,
    val escalationStatus: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorporateDashboardScreen(
    onNavigateBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavigateToViolations: () -> Unit = {},
    onNavigateToRegisters: () -> Unit = {},
    onNavigateToUserManagement: () -> Unit = {}
) {
    val context = LocalContext.current
    var isRecalculating by remember { mutableStateOf(false) }
    var selectedXaiSubsidiary by remember { mutableStateOf<RiskRankedSubsidiary?>(null) }
    var showProvisionDialog by remember { mutableStateOf(false) }

    // --- Profile State ---
    var showProfileDialog by remember { mutableStateOf(false) }
    var profileName by remember { mutableStateOf("SMT. ANANYA SEN") }
    var profileId by remember { mutableStateOf("ID: DGMS-EZ-2024-8841") }

    val ecCards = remember {
        listOf(
            EcLimitCardItem("Tetaria Khar (ECL)", "EC Ref: J-11015/84/2018-IA.II(M)", 87.1, "87.1% CEILING REACHED", 3.92, 4.50, 3.65, "Pithead Stock: +0.27 MT", "⚠️ MoEFCC Section 15 alert: Extraction pace will breach cap in 28 days", true),
            EcLimitCardItem("Dhori Khas (CCL)", "EC Ref: J-11015/22/2016-IA.II(M)", 68.3, "68.3% CEILING", 4.10, 6.00, 4.05, "Stockpile Variance: 1.2% (Nominal)", "Optimal extraction schedule. Dispatch capacity balanced.", false),
            EcLimitCardItem("Govindpur Colliery (BCCL)", "EC Ref: J-11015/39/2019-IA.II(M)", 67.2, "67.2% CEILING", 2.15, 3.20, 2.12, "Stockpile Variance: 1.4% (Nominal)", "Valid till Mar 2028. Full logistics harmony with railway rakes.", false)
        )
    }

    val riskRankedSubsidiaries = remember {
        listOf(
            RiskRankedSubsidiary(1, "Jayant OCP", 25, listOf("Overburden Slope Incline" to 12.5, "Methane Seam Variance" to 8.2), "Maintain continuous bench slope monitoring."),
            RiskRankedSubsidiary(2, "Tetaria Khar", 24, listOf("Methane Accumulation (Seam III)" to 28.5, "Haul Berm Defect" to 22.0), "Isolate power to Seam III machinery and reconstruct Bench 2 berm."),
            RiskRankedSubsidiary(3, "Bhubaneswari OCP", 24, listOf("Dust PM10 Concentration" to 14.1, "Haul Truck Speed Defect" to 9.9), "Increase water sprinkling frequency on haul road 4."),
            RiskRankedSubsidiary(4, "Rajhara", 24, listOf("Ventilation Fan Overhaul Overdue" to 18.2), "Perform fan bearing maintenance on Sunday shift."),
            RiskRankedSubsidiary(5, "Choritand Tiliaya", 22, listOf("Contractor VTC Expired" to 15.0), "Verify contractor passcards at gate interlock."),
            RiskRankedSubsidiary(6, "Moonidih Project", 21, listOf("Sump Pump Capacity" to 11.2), "Clear lower seam drainage channel."),
            RiskRankedSubsidiary(7, "North of Arkhapal Srirampur", 20, listOf("Environmental Audit Delay" to 8.0), "Upload Managerial Bi-Weekly Report."),
            RiskRankedSubsidiary(8, "Gevra OCP", 19, listOf("Rail Loading Siding Lag" to 6.5), "Coordinate with South East Central Railway."),
            RiskRankedSubsidiary(9, "Rohne", 18, listOf("All Sensors Within Normal Bounds" to 0.0), "100% Compliant baseline telemetry.")
        )
    }

    val liveViolationsFeed = remember {
        listOf(
            LiveViolationFeedItem(1, "Tetaria Khar", "production", "MEDIUM", "OPEN", "about 12 hours ago"),
            LiveViolationFeedItem(3, "Kathautia OCP", "production", "MEDIUM", "OPEN", "about 16 hours ago"),
            LiveViolationFeedItem(4, "North of Arkhapal Srirampur", "environment", "MEDIUM", "OPEN", "about 16 hours ago"),
            LiveViolationFeedItem(5, "Dhori Khas", "labour", "MEDIUM", "OPEN", "about 16 hours ago"),
            LiveViolationFeedItem(7, "Govindpur Colliery", "safety", "MEDIUM", "OPEN", "1 day ago"),
            LiveViolationFeedItem(9, "Govindpur Colliery", "safety", "CRITICAL", "OPEN", "1 day ago")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoalGuardLogo(size = 34.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("COAL INDIA LIMITED", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(6.dp)) {
                                    Text("HQ ACTIVE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, color = Color(0xFFA7F3D0), fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("सत्यमेव जयते | CORPORATE HQ COMMAND CENTER", fontSize = 9.sp, color = GovtGoldAmber)
                        }
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

                    Surface(
                        color = Color(0xFF0284C7), 
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { showProfileDialog = true }
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(profileName.take(1).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(profileName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(profileId, fontSize = 8.sp, color = Color.White.copy(alpha = 0.85f))
                            }
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
            // Header Action Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Surface(
                            color = GovtGoldTint,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, GovtGoldAmber)
                        ) {
                            Text(
                                text = "EXECUTIVE MISSION COMMAND • HQ ADMIN",
                                color = GovtGoldAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Corporate HQ Telemetry", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GovtTextDark)
                        Text("Real-time risk scoring, environmental production quotas, and live breach feeds.", fontSize = 11.sp, color = GovtTextMuted)
                    }

                    Button(
                        onClick = {
                            isRecalculating = true
                            Toast.makeText(context, "🔄 Recalculating Global Risk Scores via XGBoost RiskNet...", Toast.LENGTH_SHORT).show()
                            isRecalculating = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recalculate Risk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Stat Summary Cards Grid Row 1
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CorpKpiCard("TOTAL SUPERVISED SITES", "18", "Nodes Active", Color(0xFF0284C7), Modifier.weight(1f))
                    CorpKpiCard("ACTIVE VIOLATIONS", "57", "Action Required", GovtGoldAmber, Modifier.weight(1f))
                    CorpKpiCard("OVERDUE COMPLIANCE", "5", "Escalated", Color(0xFFDC2626), Modifier.weight(1f))
                }
            }

            // Stat Summary Cards Grid Row 2
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CorpKpiCard("GLOBAL RISK INDEX", "29 /100", "Weighted Mean", Color(0xFF0284C7), Modifier.weight(1f))
                    CorpKpiCard("DAILY EXTRACTION", "1.84 MT", "Pit Output", Color(0xFF059669), Modifier.weight(1f))
                    CorpKpiCard("RAILWAY DISPATCH", "1.79 MT", "Rake Movement", Color(0xFF0284C7), Modifier.weight(1f))
                }
            }

            // Environmental Quota Limits Card Header
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ENVIRONMENTAL & LOGISTICS LIMITS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            }

                            Surface(color = GovtGoldTint, shape = RoundedCornerShape(6.dp)) {
                                Text("FY 2025-26 QUOTA TRACKING", modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Continuous reconciliation of Pit Extraction vs Statutory EC Production Limits vs Railway Siding Dispatches.", fontSize = 11.sp, color = GovtTextMuted)
                    }
                }
            }

            // EC Limit Cards List
            items(ecCards) { card ->
                val barColor = if (card.isCritical) Color(0xFFDC2626) else Color(0xFF059669)
                val badgeBg = if (card.isCritical) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)
                val badgeFg = if (card.isCritical) Color(0xFFDC2626) else Color(0xFF059669)

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
                            Column {
                                Text(card.mineName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                Text(card.ecRef, fontSize = 10.sp, color = GovtTextMuted)
                            }

                            Surface(color = badgeBg, shape = RoundedCornerShape(8.dp)) {
                                Text(card.ceilingLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = badgeFg)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Extracted: ${card.extractedMt} MT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                            Text("Statutory Cap: ${card.statutoryCapMt} MTPA", fontSize = 11.sp, color = GovtTextMuted)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { (card.ceilingPercent / 100.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = barColor,
                            trackColor = GovtBgSlate
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rail Siding Dispatch: ${card.railDispatchMt} MT", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.Bold)
                            Text(card.stockText, fontSize = 11.sp, color = GovtTextMuted)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = card.alertText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (card.isCritical) GovtGoldAmber else Color(0xFF059669)
                        )
                    }
                }
            }

            // Risk Ranked Subsidiaries Section
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CONSOLIDATED RISK-RANKED SUBSIDIARIES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            }
                            Text("SHAP AI Model", fontSize = 9.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        riskRankedSubsidiaries.forEach { sub ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sub.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = GovtTextDark, modifier = Modifier.weight(1f))
                                    Text("Risk: ${sub.riskIndex}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Button(
                                        onClick = { selectedXaiSubsidiary = sub },
                                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Explain Risk (XAI)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { sub.riskIndex / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Color(0xFF059669),
                                    trackColor = GovtBgSlate
                                )
                            }
                        }
                    }
                }
            }

            // MINE OFFICIALS & STATUTORY ACCESS CONTROL CARD
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MINE OFFICIALS & STATUTORY ACCESS CONTROL", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            }

                            Surface(color = GovtGoldTint, shape = RoundedCornerShape(6.dp)) {
                                Text("DGMS GOVERNANCE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Govern Mine Managers, Safety Officers, Colliery Engineers & DGMS Inspectors across all CIL subsidiaries.", fontSize = 11.sp, color = GovtTextMuted)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = GovtBgSlate,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("TOTAL OFFICIALS", fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                                    Text("13 Officers", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFD1FAE5),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("CLEARANCE ACTIVE", fontSize = 8.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Bold)
                                    Text("12 Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = GovtGoldTint,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("PENDING JURISDICTION", fontSize = 8.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                                    Text("1 Pending", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sample Officials Preview List
                        listOf(
                            Triple("Er. Rajesh Kumar", "Mine Official • Govindpur Colliery (Mine ID: 42)", "Active Clearance"),
                            Triple("Dr. Arindam Sen", "Corporate HQ • Strategy & Compliance", "Active Clearance"),
                            Triple("Shri S. K. Verma", "DGMS Regulator • Dhanbad Region I", "Active Clearance")
                        ).forEach { (officerName, roleJurisdiction, status) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(GovtNavyPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(officerName.take(1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(officerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                        Text(roleJurisdiction, fontSize = 10.sp, color = GovtTextMuted)
                                    }
                                }

                                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(4.dp)) {
                                    Text("• $status", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                }
                            }
                            HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToUserManagement,
                                colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Manage Mine Officials", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showProvisionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Provision Officer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // LIVE VIOLATIONS FEED CARD
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
                            Text("ENTERPRISE LIVE VIOLATIONS FEED", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                                Text("• REALTIME INTERCONNECT ACTIVE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        liveViolationsFeed.forEach { item ->
                            val isCritical = item.severity == "CRITICAL"
                            val severityBg = if (isCritical) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                            val severityFg = if (isCritical) Color(0xFFDC2626) else GovtGoldAmber

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.mineName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                    Text("Category: ${item.category}", fontSize = 10.sp, color = GovtTextMuted)
                                }

                                Surface(color = severityBg, shape = RoundedCornerShape(4.dp)) {
                                    Text(item.severity, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Black, color = severityFg)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(color = GovtGoldTint, shape = RoundedCornerShape(4.dp)) {
                                    Text(item.escalationStatus, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(item.timestamp, fontSize = 9.sp, color = GovtTextMuted)
                            }
                            HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            // Quick Nav Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToViolations,
                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active Directives", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onNavigateToRegisters,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GovtNavyPrimary),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = GovtNavyPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CMR Registers", fontWeight = FontWeight.Bold, color = GovtNavyPrimary, fontSize = 12.sp)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // AI EXPLAINABILITY DIALOG
    selectedXaiSubsidiary?.let { sub ->
        AlertDialog(
            onDismissRequest = { selectedXaiSubsidiary = null },
            confirmButton = {
                Button(onClick = { selectedXaiSubsidiary = null }, colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)) {
                    Text("Close SHAP Analysis", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text("${sub.name} — XAI SHAP Model Breakdown", fontWeight = FontWeight.Bold, color = GovtTextDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Risk Index: ${sub.riskIndex} / 100", fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                    HorizontalDivider(color = GovtCardBorder)

                    Text("SHAP Feature Contributions:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                    sub.shapFactors.forEach { (feature, points) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(feature, fontSize = 11.sp, color = GovtTextDark)
                            Text("+$points pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Statutory Directive:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                    Text(sub.recommendation, fontSize = 11.sp, color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // PROVISION OFFICER DIALOG
    if (showProvisionDialog) {
        var newName by remember { mutableStateOf("") }
        var newEmail by remember { mutableStateOf("") }
        var newJurisdiction by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showProvisionDialog = false },
            title = { Text("Provision Statutory Officer Clearance", fontWeight = FontWeight.Bold, color = GovtTextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Officer Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Official Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newJurisdiction,
                        onValueChange = { newJurisdiction = it },
                        label = { Text("Assigned Jurisdiction / Mine ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newEmail.isNotBlank()) {
                            Toast.makeText(context, "✅ Statutory Clearance Provisioned for $newName", Toast.LENGTH_LONG).show()
                        }
                        showProvisionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Provision Clearance", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProvisionDialog = false }) {
                    Text("Cancel", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CorpKpiCard(title: String, value: String, sub: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = GovtTextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Surface(color = accentColor.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                Text(sub, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
        }
    }
}
