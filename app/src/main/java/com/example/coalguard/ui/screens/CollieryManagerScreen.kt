package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.ui.components.RealMineMapView
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
fun CollieryManagerScreen(
    onNavigateBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNavigateToInspections: () -> Unit = {},
    onNavigateToRegisters: () -> Unit = {},
    onNavigateToCapture: () -> Unit = {},
    onNavigateToViolations: () -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(6.dp)) {
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
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search Anything", tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    IconButton(onClick = onNotificationClick) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFDC2626)) {
                                    Text("4", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Statutory Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF0284C7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .widthIn(max = 135.dp)
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SMT. ANANYA SEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
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
            // Header Title
            item {
                Column {
                    Text("MINISTRY OF COAL / OPERATIONS / COLLIERY MANAGEMENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Colliery Manager Console", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = GovtTextDark)
                        Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(6.dp)) {
                            Text("Synced", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("LOCAL TIME: 23:56:11 (UTC+05:30)", fontSize = 10.sp, color = GovtTextMuted)
                }
            }

            // 1. ACTION SHORTCUTS (Horizontal Scrollable Chips - NO TEXT TRUNCATION!)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionShortcutChip("Online Telemetry", Icons.Default.Wifi, Color(0xFF065F46)) {
                        Toast.makeText(context, "Network Telemetry Online", Toast.LENGTH_SHORT).show()
                    }

                    ActionShortcutChip("Inspections", Icons.AutoMirrored.Filled.Assignment, GovtNavyPrimary) {
                        onNavigateToInspections()
                    }

                    ActionShortcutChip("CMR Registers", Icons.Default.Description, GovtGoldAmber) {
                        onNavigateToRegisters()
                    }

                    ActionShortcutChip("Report Hazard", Icons.Default.Warning, Color(0xFFDC2626)) {
                        onNavigateToCapture()
                    }
                }
            }

            // 2. METRIC CARDS (2x2 GRID - NO CRAMPED OVERLAPPING!)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Workers on Site + Open Violations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CollieryKpiCard(
                            title = "WORKERS ON-SITE",
                            value = "${WorkforceAttendanceManager.onSiteCount()}",
                            sub = "3 Active Shifts",
                            accentColor = Color(0xFF0284C7),
                            icon = Icons.Default.Groups,
                            modifier = Modifier.weight(1f)
                        )

                        CollieryKpiCard(
                            title = "OPEN VIOLATIONS",
                            value = "0",
                            sub = "Site Fully Compliant",
                            accentColor = Color(0xFF059669),
                            icon = Icons.Default.VerifiedUser,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Compliance Items + Contractors Active
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CollieryKpiCard(
                            title = "COMPLIANCE ITEMS",
                            value = "12",
                            sub = "Tracked This Month",
                            accentColor = Color(0xFF0284C7),
                            icon = Icons.Default.TaskAlt,
                            modifier = Modifier.weight(1f)
                        )

                        CollieryKpiCard(
                            title = "ACTIVE CONTRACTORS",
                            value = "${WorkforceAttendanceManager.contractorCount()}",
                            sub = "of 4 registered",
                            accentColor = GovtGoldAmber,
                            icon = Icons.Default.Engineering,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. SHIFT SCHEDULE SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SHIFT SCHEDULE & MANIFEST", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ShiftItemCard("Morning (06:00 - 14:00)", "Rajesh Kumar", "SHIFT FOREMAN", "${WorkforceAttendanceManager.onSiteCount()} MINERS", true)
                        Spacer(modifier = Modifier.height(10.dp))
                        ShiftItemCard("Afternoon (14:00 - 22:00)", "Suresh Patel", "SHIFT FOREMAN", "118 MINERS", false)
                        Spacer(modifier = Modifier.height(10.dp))
                        ShiftItemCard("Night (22:00 - 06:00)", "Manoj Singh", "SHIFT FOREMAN", "97 MINERS", false)
                    }
                }
            }

            // 4. INSAR SATELLITE SUBSIDENCE CARD (FIXED SINGLE-LINE UNBROKEN BADGE!)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Radar, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "INSAR SATELLITE RADAR SUBSIDENCE",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GovtTextDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Clean, UNBROKEN horizontal badge on a single line!
                            Surface(
                                color = Color(0xFFD1FAE5),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF059669)))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "STABLE: 0.84 mm/yr",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, GovtCardBorder, RoundedCornerShape(10.dp))
                        ) {
                            RealMineMapView(
                                latitude = 23.7923,
                                longitude = 86.4253,
                                mineName = "Govindpur Colliery (BCCL)",
                                radiusMeters = 500,
                                isSatellite = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(color = GovtBgSlate, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).padding(8.dp)) {
                                Column {
                                    Text("DISPLACEMENT RATE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                                    Text("2.4 mm/yr", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GovtNavyPrimary)
                                    Text("Ground Deformation", fontSize = 8.sp, color = GovtTextMuted)
                                }
                            }

                            Surface(color = GovtBgSlate, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).padding(8.dp)) {
                                Column {
                                    Text("BENCH TILT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                                    Text("1.2° Slope", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GovtNavyPrimary)
                                    Text("Under Regulation 108", fontSize = 8.sp, color = GovtTextMuted)
                                }
                            }
                        }
                    }
                }
            }

            // 5. CONTRACTOR MANIFEST
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CONTRACTOR MANIFEST", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ContractorManifestItem("M/s RK Earthmovers Pvt Ltd", "34 WORKERS", "CHECKED-IN", "06:12 AM", Color(0xFFD1FAE5), Color(0xFF065F46))
                        Spacer(modifier = Modifier.height(10.dp))
                        ContractorManifestItem("M/s Suvidha Drilling Co.", "18 WORKERS", "CHECKED-IN", "06:45 AM", Color(0xFFD1FAE5), Color(0xFF065F46))
                        Spacer(modifier = Modifier.height(10.dp))
                        ContractorManifestItem("M/s Bharat Explosives", "8 WORKERS", "PENDING", "--", GovtGoldTint, GovtGoldAmber, "⚠️ SAFETY CERT EXPIRING SOON")
                        Spacer(modifier = Modifier.height(10.dp))
                        ContractorManifestItem("M/s Ganesh Haulage", "22 WORKERS", "ABSENT", "--", Color(0xFFFEE2E2), Color(0xFFDC2626))
                    }
                }
            }

            // 6. OPEN HAZARDS BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToViolations() },
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
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("OPEN HAZARDS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Violations & Sync", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1FAE5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("NO OPEN HAZARDS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ActionShortcutChip(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun CollieryKpiCard(title: String, value: String, sub: String, accentColor: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = GovtTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = GovtTextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sub, fontSize = 9.sp, color = GovtTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ShiftItemCard(timeRange: String, foreman: String, role: String, miners: String, isActive: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GovtBgSlate,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(timeRange, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (isActive) Color(0xFFD1FAE5) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isActive) "ACTIVE" else "UPCOMING",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color(0xFF065F46) else GovtTextMuted
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("$foreman • $role", fontSize = 11.sp, color = GovtTextMuted)
            }

            Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(8.dp)) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF0369A1), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(miners, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                }
            }
        }
    }
}

@Composable
fun ContractorManifestItem(name: String, workers: String, status: String, time: String, bg: Color, fg: Color, alert: String? = null) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GovtBgSlate,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = GovtTextMuted, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(workers, fontSize = 10.sp, color = GovtTextMuted)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
                        Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = fg)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(time, fontSize = 10.sp, color = GovtTextMuted)
                }
            }

            if (alert != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(alert, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
            }
        }
    }
}
