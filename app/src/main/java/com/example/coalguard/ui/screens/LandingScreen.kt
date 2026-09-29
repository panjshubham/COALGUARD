package com.example.coalguard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.R
import com.example.coalguard.ui.components.CoalGuardLogo
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted
import kotlinx.coroutines.delay

data class TelemetryMatrixItem(
    val concessionName: String,
    val operator: String,
    val stabilityIndex: String,
    val pm10Particulate: String,
    val ch4Reading: String,
    val dgmsState: String,
    val isWarning: Boolean = false
)

data class ProblemSolutionItem(
    val problemTitle: String,
    val problemDesc: String,
    val solutionTitle: String,
    val solutionDesc: String,
    val targetRoute: String,
    val icon: ImageVector,
    val category: String
)

data class GridWorkspaceItem(
    val title: String,
    val subtitle: String,
    val route: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconColor: Color,
    val isNew: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LandingScreen(
    isUserLoggedIn: Boolean = false,
    onOpenDrawer: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var pendingRoute by remember { mutableStateOf<String?>(null) }
    var pendingFeatureTitle by remember { mutableStateOf("") }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val matrixItems = remember {
        listOf(
            TelemetryMatrixItem("Jharia Block-IV Colliery", "BCCL / Coal India", "99.2% (Stable)", "84 µg/m³", "0.12% vol", "CERTIFIED"),
            TelemetryMatrixItem("Korba West Open Cast Mine", "SECL Central Pit", "98.7% (Stable)", "112 µg/m³", "0.08% vol", "CERTIFIED"),
            TelemetryMatrixItem("Singrauli Northern Ridge", "NCL Governance Unit", "91.4% (Review Bench 4)", "168 µg/m³", "0.24% vol", "ADVISORY ACTIVE", isWarning = true),
            TelemetryMatrixItem("Talcher Deep Seam Complex", "MCL Mahanadi Range", "99.8% (Stable)", "72 µg/m³", "0.05% vol", "CERTIFIED")
        )
    }

    val sliderImages = remember {
        listOf(
            R.drawable.coal_mining_operation,
            R.drawable.coal_mining_2,
            R.drawable.coal_mining_3
        )
    }

    val pagerState = rememberPagerState(pageCount = { sliderImages.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % sliderImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // Interlock handler: requires login before opening operational workspace
    fun openFeatureWithAuthCheck(route: String, featureTitle: String) {
        if (isUserLoggedIn) {
            onNavigateToRoute(route)
        } else {
            pendingRoute = route
            pendingFeatureTitle = featureTitle
            showLoginRequiredDialog = true
        }
    }

    // Streamlined 6 Essential Operational Workspaces (2-Column Grid)
    val essentialWorkspaces = remember {
        listOf(
            GridWorkspaceItem("Risk & Gas Telemetry", "CH4 & InSAR Subsidence", "colliery_manager", Icons.Default.Sensors, Color(0xFFE0F2FE), Color(0xFF0284C7)),
            GridWorkspaceItem("AI Hazard Cam", "YOLOv8 PPE Vision Scanner", "capture", Icons.Default.AddAPhoto, Color(0xFFFEE2E2), Color(0xFFDC2626), isNew = true),
            GridWorkspaceItem("Statutory Audits", "Field Inspection Dossiers", "inspections", Icons.AutoMirrored.Filled.Assignment, Color(0xFFD1FAE5), Color(0xFF059669)),
            GridWorkspaceItem("Violations Feed", "SLA Escalation Directives", "violations", Icons.Default.Warning, Color(0xFFFEF3C7), GovtGoldAmber, isNew = true),
            GridWorkspaceItem("CMR Statutory Books", "SHA-256 Hash Chain Ledger", "registers", Icons.Default.Description, Color(0xFFF3E8FF), Color(0xFF7C3AED)),
            GridWorkspaceItem("Water Inrush AI", "Hydrochemical Aquifer Model", "water_inrush", Icons.Default.WaterDrop, Color(0xFFE0F2FE), Color(0xFF0284C7), isNew = true)
        )
    }

    // High-Impact Problem Statements vs AI Solutions
    val problemSolutions = remember {
        listOf(
            ProblemSolutionItem(
                problemTitle = "Underground Water Inrush & Gas Explosions",
                problemDesc = "Unpredicted aquifer breaches cause sudden underground flooding & mine disasters.",
                solutionTitle = "XGBoost Hydrochemical Ion AI & Sub-surface Telemetry",
                solutionDesc = "Classifies water samples into G1/G2/G3 aquifers & isolates power when CH4 > 0.75%.",
                targetRoute = "water_inrush",
                icon = Icons.Default.WaterDrop,
                category = "SAFETY HAZARD AI"
            ),
            ProblemSolutionItem(
                problemTitle = "Paper Register Tampering & Post-dated Inspection Fraud",
                problemDesc = "Physical logbooks are prone to retro-active modifications and lack auditability.",
                solutionTitle = "Cryptographic SHA-256 Blockchain Hash Chains",
                solutionDesc = "Seals every statutory inspection entry into an immutable cryptographic ledger.",
                targetRoute = "registers",
                icon = Icons.Default.Description,
                category = "AUDIT INTEGRITY"
            ),
            ProblemSolutionItem(
                problemTitle = "Unsafe Worker PPE Gear & High-Risk Site Entry",
                problemDesc = "Workers entering hazardous pits without hard-hats or high-visibility vests.",
                solutionTitle = "Realtime YOLOv8 Camera Vision & Gate Pass Interlock",
                solutionDesc = "Scans personnel photos instantly and blocks gate barriers for unverified workers.",
                targetRoute = "capture",
                icon = Icons.Default.CameraAlt,
                category = "FIELD SAFETY VISION"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoalGuardLogo(size = 36.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("कोलगाड प्लेटफॉर्म • COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Text("सत्यमेव जयते | MINISTRY OF COAL • GOVT. OF INDIA", fontSize = 8.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Anything",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
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

                    if (!isUserLoggedIn) {
                        Button(
                            onClick = onNavigateToLogin,
                            colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp).padding(end = 4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign In", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = Color(0xFF065F46),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFA7F3D0)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CLEARANCE ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7F3D0))
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
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TOP EXPRESS PORTAL BANNER (Matching Reference Image)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GovtNavyPrimary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = GovtGoldAmber, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CIL STATUTORY EXPRESS PORTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("DGMS V4.2 COMPLIANT", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                BannerQuickActionItem("Mission Control", Icons.Default.PrecisionManufacturing, GovtGoldAmber) {
                                    openFeatureWithAuthCheck("colliery_manager", "Mission Control")
                                }

                                BannerQuickActionItem("Hazard Cam", Icons.Default.AddAPhoto, Color(0xFFEF4444)) {
                                    openFeatureWithAuthCheck("capture", "AI Hazard Camera")
                                }

                                BannerQuickActionItem("CMR Registers", Icons.Default.Description, Color(0xFF38BDF8)) {
                                    openFeatureWithAuthCheck("registers", "CMR Registers")
                                }

                                BannerQuickActionItem("Inrush AI", Icons.Default.WaterDrop, Color(0xFF34D399)) {
                                    openFeatureWithAuthCheck("water_inrush", "Water Inrush AI")
                                }
                            }
                        }
                    }
                }
            }

            // 2. ESSENTIAL OPERATIONAL WORKSPACES (Clean 2-Column Grid)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CORE MINE WORKSPACES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Essential Operational Gateways", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GovtTextDark)
                        }
                        Surface(color = GovtGoldTint, shape = RoundedCornerShape(6.dp)) {
                            Text("6 CORE MODULES", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        essentialWorkspaces.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowItems.forEach { gridItem ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(120.dp)
                                            .clickable { openFeatureWithAuthCheck(gridItem.route, gridItem.title) },
                                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, GovtCardBorder)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(gridItem.iconBg),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        gridItem.icon,
                                                        contentDescription = gridItem.title,
                                                        tint = gridItem.iconColor,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                if (gridItem.isNew) {
                                                    Surface(
                                                        color = Color(0xFFDC2626),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text("NEW", modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp), fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                    }
                                                }
                                            }

                                            Column {
                                                Text(gridItem.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(gridItem.subtitle, fontSize = 10.sp, color = GovtTextMuted, maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. LANDSCAPE HERO CAROUSEL BANNER
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = sliderImages[page]),
                                    contentDescription = "Mining Operation ${page + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.58f))
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = GovtGoldAmber,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "NATIONAL SAFETY DIRECTIVES",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Coal India Autonomous Safety Network",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Predictive Water Inrush AI, InSAR Bench Radar & Cryptographic SHA-256 Hash Chains.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE2E8F0),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // 4. GOVT PROBLEM STATEMENT VS COALGUARD AI SOLUTION SHOWCASE
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("GOVT PROBLEM STATEMENT & SOLUTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("National Mine Safety Directives", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GovtTextDark)
                        }
                        Surface(color = GovtGoldTint, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, GovtGoldAmber)) {
                            Text("DIRECTIVES", modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        problemSolutions.forEach { ps ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { openFeatureWithAuthCheck(ps.targetRoute, ps.problemTitle) },
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
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFFEE2E2)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(ps.icon, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("GOVT PROBLEM STATEMENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                                Text(ps.problemTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                            }
                                        }

                                        Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(6.dp)) {
                                            Text(ps.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(ps.problemDesc, fontSize = 11.sp, color = GovtTextMuted)

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GovtCardBorder)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFD1FAE5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("COALGUARD AI SOLUTION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                            Text(ps.solutionTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                                            Text(ps.solutionDesc, fontSize = 11.sp, color = GovtTextMuted)
                                        }
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. LIVE CONCESSION STATUS MATRIX
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("LIVE CONCESSION STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("National Coal Basin Telemetry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = GovtTextDark)
                }
            }

            items(matrixItems) { item ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openFeatureWithAuthCheck("registers", "CMR Registers & Books") },
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
                                Column {
                                    Text(item.concessionName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                    Text(item.operator, fontSize = 11.sp, color = GovtTextMuted)
                                }

                                Surface(
                                    color = if (item.isWarning) GovtGoldTint else Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.dgmsState,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isWarning) GovtGoldAmber else Color(0xFF059669)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Stability: ${item.stabilityIndex}", fontSize = 11.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                                Text("CH4: ${item.ch4Reading}", fontSize = 11.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Login Required Interlock Dialog
    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showLoginRequiredDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GovtGoldTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GovtGoldAmber, modifier = Modifier.size(28.dp))
                }
            },
            title = { Text("🔒 Government Clearance Required", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GovtTextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Accessing '$pendingFeatureTitle' requires authorized CIL / DGMS official clearance.",
                        fontSize = 13.sp,
                        color = GovtTextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Please sign in with your enterprise credentials to unlock operational features, field hazard logs, and statutory books.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLoginRequiredDialog = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign In to Access Workspace", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginRequiredDialog = false }) {
                    Text("Explore Guest Mode", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BannerQuickActionItem(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
    }
}
