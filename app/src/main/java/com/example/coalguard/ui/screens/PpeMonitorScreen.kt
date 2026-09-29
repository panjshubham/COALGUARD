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
import androidx.compose.ui.graphics.vector.ImageVector
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

// PPE Violation Event Model
data class PpeEventItem(
    val id: String,
    val location: String,
    val timestamp: String,
    val missingPpe: List<String>,
    val detectedPpe: List<String>,
    val severity: String,
    var isResolved: Boolean = false
)

data class SiteCamera(
    val id: String,
    val name: String,
    val fps: Int = 30,
    val isOnline: Boolean = true,
    val activeViolations: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PpeMonitorScreen(
    onNavigateBack: () -> Unit = {},
    onLaunchCameraScan: () -> Unit = {}
) {
    val context = LocalContext.current

    val cameras = remember {
        listOf(
            SiteCamera("1", "Incline Shaft No. 2 Entrance", fps = 28, activeViolations = 1),
            SiteCamera("2", "Haulage Road Junction", fps = 30, activeViolations = 2),
            SiteCamera("3", "Pithead Gantry & Lamp Room", fps = 25, activeViolations = 0)
        )
    }

    val ppeEvents = remember {
        mutableStateListOf(
            PpeEventItem("1", "Govindpur East Pit 3", "2 mins ago", listOf("hard-hat"), listOf("safety-vest"), "CRITICAL"),
            PpeEventItem("2", "Haulage Road Junction", "12 mins ago", listOf("hard-hat", "safety-vest"), emptyList(), "CRITICAL"),
            PpeEventItem("3", "Pithead Gantry", "35 mins ago", listOf("safety-vest"), listOf("hard-hat"), "HIGH"),
            PpeEventItem("4", "Seam III Conveyor", "1 hour ago", emptyList(), listOf("hard-hat", "safety-vest"), "NONE", isResolved = true)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("सत्यमेव जयते | REALTIME YOLOv8 PPE VISION MONITOR", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = onLaunchCameraScan,
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Camera Scan", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            // Header Info Box
            item {
                Column {
                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF059669))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI VISION INTELLIGENCE • YOLOv8 PPE Engine",
                                color = Color(0xFF065F46),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "PPE Live Safety Monitor",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Continuous site monitoring for statutory hard-hats and hi-vis safety vests.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onLaunchCameraScan,
                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Run Instant Field Camera Scan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Stat Summary Cards Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PpeKpiCard("COMPLIANCE RATE", "94.2%", Color(0xFF059669), Icons.Default.VerifiedUser, Modifier.weight(1f))
                        PpeKpiCard("TOTAL SCANS TODAY", "1,284", GovtNavyPrimary, Icons.Default.Visibility, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PpeKpiCard("ACTIVE VIOLATIONS", "${ppeEvents.count { !it.isResolved && it.missingPpe.isNotEmpty() }}", Color(0xFFDC2626), Icons.Default.Warning, Modifier.weight(1f))
                        PpeKpiCard("RESOLVED TODAY", "28", Color(0xFF059669), Icons.Default.CheckCircle, Modifier.weight(1f))
                    }
                }
            }

            // Site CCTV Feeds Section
            item {
                Column {
                    Text("SITE CAMERA FEEDS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cameras.forEach { cam ->
                            CameraFeedCard(cam)
                        }
                    }
                }
            }

            // Recent Violation Stream Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("REALTIME VIOLATION FEED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                    Surface(color = GovtGoldTint, shape = RoundedCornerShape(4.dp)) {
                        Text("CMR 2017 REG 115", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                    }
                }
            }

            items(ppeEvents) { event ->
                PpeEventCard(
                    event = event,
                    onResolve = {
                        val index = ppeEvents.indexOfFirst { it.id == event.id }
                        if (index != -1) {
                            ppeEvents[index] = ppeEvents[index].copy(isResolved = true)
                            Toast.makeText(context, "✅ Safety Officer Dispatched & Breach Resolved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun PpeKpiCard(label: String, value: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GovtTextMuted)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            }
        }
    }
}

@Composable
fun CameraFeedCard(cam: SiteCamera) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF059669))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(cam.name, color = GovtTextDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("CCTV FEED • ${cam.fps} FPS • Local YOLOv8", color = GovtTextMuted, fontSize = 10.sp)
                }
            }

            if (cam.activeViolations > 0) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${cam.activeViolations} VIOLATION",
                        color = Color(0xFFDC2626),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "COMPLIANT",
                        color = Color(0xFF059669),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PpeEventCard(event: PpeEventItem, onResolve: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (event.missingPpe.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (event.missingPpe.isNotEmpty()) Color(0xFFDC2626) else Color(0xFF059669),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(event.location, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                }

                Text(event.timestamp, fontSize = 10.sp, color = GovtTextMuted)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (event.missingPpe.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("MISSING GEAR: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    event.missingPpe.forEach { item ->
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = item.uppercase(),
                                color = Color(0xFFDC2626),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            } else {
                Text("✅ All Required Statutory PPE Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
            }

            if (!event.isResolved && event.missingPpe.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onResolve,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Acknowledge & Dispatch Safety Officer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
