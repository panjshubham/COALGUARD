package com.example.coalguard.ui.components

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

data class StatutoryNotification(
    val id: String,
    val title: String,
    val message: String,
    val category: String, // "RISK", "VIOLATION", "NOTICE", "SYSTEM"
    val severity: String, // "CRITICAL", "HIGH", "MEDIUM", "INFO"
    val timestamp: String,
    val mineName: String,
    var isRead: Boolean = false,
    val actionRoute: String? = null
)

object StatutoryNotificationManager {
    val notificationsList = mutableStateListOf(
        StatutoryNotification(
            id = "n1",
            title = "Critical Methane Gas Threshold Exceeded",
            message = "Real-time telemetry detected CH4 concentration > 0.85% in Pit 3 Seam III. Automatic ventilation power isolation triggered.",
            category = "RISK",
            severity = "CRITICAL",
            timestamp = "Just Now",
            mineName = "Govindpur Colliery (Mine ID: 42)",
            isRead = false,
            actionRoute = "colliery_manager"
        ),
        StatutoryNotification(
            id = "n2",
            title = "InSAR Satellite Bench Deformation Alert",
            message = "Radar displacement rate > 25.4mm/yr detected at Bench 2 slope. Immediate machine evacuation advised under CMR Reg 108.",
            category = "RISK",
            severity = "CRITICAL",
            timestamp = "12 mins ago",
            mineName = "Tetaria Khar Colliery (Mine ID: 45)",
            isRead = false,
            actionRoute = "colliery_manager"
        ),
        StatutoryNotification(
            id = "n3",
            title = "Statutory Safety Breach: Unsecured Cable",
            message = "Exposed high-voltage cable runway near East Pit 3. Breach logged with SHA-256 hash under CMR 2017 Reg 83.",
            category = "VIOLATION",
            severity = "HIGH",
            timestamp = "42 mins ago",
            mineName = "Govindpur Colliery (Mine ID: 42)",
            isRead = false,
            actionRoute = "violations"
        ),
        StatutoryNotification(
            id = "n4",
            title = "Water Inrush Karst Aquifer Alert",
            message = "Hydrochemical testing identified G1 Ordovician Limestone Karst aquifer breach in Gallery B. Engage main sumps.",
            category = "RISK",
            severity = "CRITICAL",
            timestamp = "1 hour ago",
            mineName = "Dhori Khas Mine (Mine ID: 43)",
            isRead = false,
            actionRoute = "water_inrush"
        ),
        StatutoryNotification(
            id = "n5",
            title = "DGMS Circular No. 4/2022 Issued",
            message = "Mandatory recertification required for hydraulic roof supports and strata barricades before 30th October 2026.",
            category = "NOTICE",
            severity = "MEDIUM",
            timestamp = "3 hours ago",
            mineName = "Directorate General of Mines Safety",
            isRead = true,
            actionRoute = "compliance"
        ),
        StatutoryNotification(
            id = "n6",
            title = "Contractor License Expiry Warning",
            message = "M/s Bharat Explosives safety operating license expiring in 15 days. VTC card gate interlock pending.",
            category = "NOTICE",
            severity = "HIGH",
            timestamp = "5 hours ago",
            mineName = "Karo Special Seam (Mine ID: 44)",
            isRead = true,
            actionRoute = "contractors"
        ),
        StatutoryNotification(
            id = "n7",
            title = "Cryptographic Ledger Sync Verified",
            message = "All offline field violations and inspection hash chains successfully synchronized with Supabase cloud.",
            category = "SYSTEM",
            severity = "INFO",
            timestamp = "Yesterday",
            mineName = "CoalGuard Core Ledger",
            isRead = true,
            actionRoute = "registers"
        )
    )

    fun addNotification(
        title: String,
        message: String,
        category: String = "VIOLATION",
        severity: String = "HIGH",
        mineName: String = "Govindpur Colliery (Mine ID: 42)",
        actionRoute: String = "violations"
    ) {
        val newNotif = StatutoryNotification(
            id = "n_${System.currentTimeMillis()}",
            title = title,
            message = message,
            category = category,
            severity = severity,
            timestamp = "Just Now",
            mineName = mineName,
            isRead = false,
            actionRoute = actionRoute
        )
        notificationsList.add(0, newNotif)
    }

    fun unreadCount(): Int = notificationsList.count { !it.isRead }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterSheet(
    onDismiss: () -> Unit,
    onNavigateToRoute: (String) -> Unit = {}
) {
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val notificationsList = StatutoryNotificationManager.notificationsList
    val unreadCount = StatutoryNotificationManager.unreadCount()

    val filteredList = notificationsList.filter { n ->
        when (selectedCategoryFilter) {
            "RISK" -> n.category == "RISK"
            "VIOLATION" -> n.category == "VIOLATION"
            "NOTICE" -> n.category == "NOTICE"
            "SYSTEM" -> n.category == "SYSTEM"
            else -> true
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = GovtBgSlate,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .padding(horizontal = 16.dp)
        ) {
            // Header Bar
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
                            text = "DGMS & CIL STATUTORY NETWORK",
                            color = GovtGoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notification Center",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = GovtTextDark
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFDC2626),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$unreadCount New",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        notificationsList.forEach { it.isRead = true }
                    }) {
                        Text("Mark all read", fontSize = 11.sp, color = GovtNavyPrimary, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GovtTextDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All (${notificationsList.size})",
                    "RISK" to "Risks & Gas",
                    "VIOLATION" to "Violations",
                    "NOTICE" to "Notices",
                    "SYSTEM" to "System Sync"
                ).forEach { (catKey, label) ->
                    FilterChip(
                        selected = selectedCategoryFilter == catKey,
                        onClick = { selectedCategoryFilter = catKey },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (selectedCategoryFilter == catKey) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovtNavyPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GovtSurfaceWhite,
                            labelColor = GovtTextDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notifications Scrollable List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { notif ->
                    NotificationCardItem(
                        notification = notif,
                        onClick = {
                            notif.isRead = true
                            if (!notif.actionRoute.isNullOrBlank()) {
                                onDismiss()
                                onNavigateToRoute(notif.actionRoute)
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun NotificationCardItem(
    notification: StatutoryNotification,
    onClick: () -> Unit
) {
    val (icon, iconBg, iconColor, severityBorder) = when (notification.severity) {
        "CRITICAL" -> Tuple4(Icons.Default.Warning, Color(0xFFFEE2E2), Color(0xFFDC2626), Color(0xFFFCA5A5))
        "HIGH" -> Tuple4(Icons.Default.ReportProblem, Color(0xFFFEF3C7), Color(0xFFD97706), Color(0xFFFDE68A))
        "MEDIUM" -> Tuple4(Icons.Default.Description, Color(0xFFE0F2FE), Color(0xFF0284C7), Color(0xFFBAE6FD))
        else -> Tuple4(Icons.Default.CheckCircle, Color(0xFFD1FAE5), Color(0xFF059669), Color(0xFFA7F3D0))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) Color(0xFFFFFFFF) else Color(0xFFF1F5F9)
        ),
        border = BorderStroke(if (!notification.isRead) 1.5.dp else 1.dp, if (!notification.isRead) severityBorder else GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = notification.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GovtTextDark
                            )
                            if (!notification.isRead) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDC2626))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${notification.mineName} • ${notification.timestamp}",
                            fontSize = 10.sp,
                            color = GovtTextMuted
                        )
                    }
                }

                Surface(
                    color = iconBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = notification.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = iconColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.message,
                fontSize = 12.sp,
                color = GovtTextDark,
                lineHeight = 16.sp
            )

            if (!notification.actionRoute.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Action / View Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
