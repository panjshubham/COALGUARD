package com.example.coalguard.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("HOW_IT_WORKS") }
    var showTicketDialog by remember { mutableStateOf(false) }
    var ticketSubmitted by remember { mutableStateOf(false) }

    val faqs = remember {
        listOf(
            FaqItem(
                "What happens if I lose internet while submitting an inspection or violation?",
                "No data is lost. The app automatically saves your submission locally in the Room database on your phone. The moment internet returns, it syncs all pending inspection reports to Supabase with SHA-256 hash sealing."
            ),
            FaqItem(
                "How is the Mine Safety Risk Score calculated?",
                "The risk index (0-100) is calculated by an XGBoost machine learning model trained on historical DGMS inspection data, considering open violations, inspection intervals, methane gas telemetry, and InSAR ground displacement."
            ),
            FaqItem(
                "How does the YOLOv8 PPE Camera Verification work?",
                "Point your phone camera at a field worker. The local YOLOv8 neural network instantly detects if the worker is wearing mandatory statutory hard-hats and hi-vis safety vests under CMR 2017 Regulation 115."
            ),
            FaqItem(
                "What is the difference between Mine Official, Corporate, and Regulator roles?",
                "Mine Officers manage their assigned colliery's daily inspections and pit violations. Corporate HQ users have executive risk overview across all mines. DGMS Regulators have independent auditing access and Section 22 order authority."
            ),
            FaqItem(
                "How do I reset my portal password?",
                "On the Sign In screen, enter your official email address and click 'Forgot Password'. You will receive an official reset link."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("सत्यमेव जयते | HELP & SUPPORT CENTER · CIL · DGMS", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GovtGoldTint,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, GovtGoldAmber)
                        ) {
                            Text(
                                text = "CIL · DGMS • HQ Admin Console",
                                color = GovtGoldAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "System Secure · DGMS Valid",
                                color = Color(0xFF059669),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Help & Support Center",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )

                    Text(
                        text = "A complete guide to every feature of CoalGuard — the DGMS-integrated safety & compliance platform for Coal India Limited.",
                        fontSize = 12.sp,
                        color = GovtTextMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "You are logged in as: Corporate HQ Admin (Smt. Ananya Sen)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GovtNavyPrimary
                    )
                }
            }

            // Section Tabs Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "HOW_IT_WORKS" to "📋 How It Works",
                        "WEATHER" to "🌤️ Weather",
                        "FAQ" to "❓ FAQ",
                        "LEGAL" to "⚖️ Legal & Privacy",
                        "CONTACT" to "📞 Helplines"
                    ).forEach { (tabKey, label) ->
                        FilterChip(
                            selected = selectedTab == tabKey,
                            onClick = { selectedTab = tabKey },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (selectedTab == tabKey) FontWeight.Bold else FontWeight.Normal) },
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

            // SECTION 1: HOW IT WORKS GUIDE
            if (selectedTab == "HOW_IT_WORKS") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text("📋 COALGUARD MODULE NAVIGATION GUIDE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)

                            GuideModuleBlock("Command Center", "Overview, Corporate HQ Executive Dashboard, Colliery Manager, Regulator Portal")
                            GuideModuleBlock("Operations", "Mines Map, Statutory Compliance, Field Inspections, Active Violations, Contractors VTC")
                            GuideModuleBlock("Safety & Records", "CMR Statutory Books (Form IV/V), PPE Safety Monitor, Water Inrush AI, Audit Log")
                            GuideModuleBlock("Risk & Analytics", "Multi-Modal AI Workbench, Water Leakage & Inrush AI, Financial & ROI Engine")
                            GuideModuleBlock("Administration", "Enterprise Manage Mine Officials, Bulk Data Import & System Sync")
                        }
                    }
                }
            }

            // SECTION 2: LIVE MINE SITE WEATHER PANEL
            if (selectedTab == "WEATHER") {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = GovtGoldAmber, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mine Site Weather Safety", fontWeight = FontWeight.Bold, color = GovtTextDark, fontSize = 14.sp)
                                }
                                Surface(color = GovtGoldTint, shape = RoundedCornerShape(4.dp)) {
                                    Text("Dhanbad Seam II", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Temp: 32°C", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                Text("Wind: 18 km/h", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                Text("Visibility: 8 km", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(8.dp)) {
                                Text("✅ Weather conditions are currently safe for all pit operations.", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                            }
                        }
                    }
                }
            }

            // SECTION 3: FAQS
            if (selectedTab == "FAQ") {
                items(faqs) { faq ->
                    FaqAccordionCard(faq)
                }
            }

            // SECTION 4: TRANSPARENCY, LEGAL & PRIVACY HUB
            if (selectedTab == "LEGAL") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("TRANSPARENCY & LEGAL HUB", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                            }

                            Text(
                                text = "CoalGuard operates under the strict guidelines of the Directorate General of Mines Safety (DGMS) and the Ministry of Coal, Government of India. Below are the governing policies for platform usage, data privacy, and statutory compliance.",
                                fontSize = 11.sp,
                                color = GovtTextMuted,
                                lineHeight = 16.sp
                            )

                            HorizontalDivider(color = GovtCardBorder)

                            // Terms & Conditions
                            Text("Terms and Conditions", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                            BulletPointText("Users must ensure that all compliance data uploaded is truthful and accurate to the best of their knowledge under the Mines Act, 1952.")
                            BulletPointText("Sharing account credentials (especially for Manager or Inspector roles) is strictly prohibited and constitutes a security breach.")
                            BulletPointText("AI-generated risk scores are advisory. Final statutory responsibility remains with the designated Mine Manager.")

                            HorizontalDivider(color = GovtCardBorder)

                            // Privacy Policy
                            Text("Privacy Policy & Data Security", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                            BulletPointText("All telemetry and worker data is encrypted at rest (AES-256) and in transit (TLS 1.3).")
                            BulletPointText("Worker PII (Personally Identifiable Information) is anonymized in AI training sets.")
                            BulletPointText("Government regulators have audited access. Data is hosted strictly within India (MeitY empaneled data centers).")

                            HorizontalDivider(color = GovtCardBorder)

                            // Contractor Disclaimer
                            Text("Contractor Disclaimer", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                            Text(
                                text = "Third-party contractors using CoalGuard for tender qualification acknowledge that compliance verification fees are non-refundable. The platform reserves the right to suspend contractor profiles if systemic safety violations are detected by the AI workbench.",
                                fontSize = 11.sp,
                                color = GovtTextMuted,
                                lineHeight = 16.sp
                            )

                            HorizontalDivider(color = GovtCardBorder)

                            // AI Automation Disclaimer
                            Text("AI Automation Disclaimer", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                            Text(
                                text = "The CoalGuard AI Workbench (Computer Vision for PPE, NLP for logs) operates with a 94.2% confidence threshold. Automated fines levied by the system undergo a 24-hour review period where they can be contested by the mine manager before final execution.",
                                fontSize = 11.sp,
                                color = GovtTextMuted,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(color = GovtBgSlate, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "Document Version: 2.4.1 (Last Updated: September 2024)",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GovtTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 5: CONTACT & EMERGENCY HELPLINES
            if (selectedTab == "CONTACT") {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("24/7 DGMS & Mine Rescue Helplines", fontWeight = FontWeight.Bold, color = Color(0xFF7F1D1D), fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18003456789"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Call Emergency Helpline: 1800-345-6789", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("• Central Mine Rescue Station (Dhanbad): 06544-222333", fontSize = 11.sp, color = Color(0xFF7F1D1D), fontWeight = FontWeight.Medium)
                                Text("• Tech Support Desk: support@coalguard.in", fontSize = 11.sp, color = Color(0xFF7F1D1D), fontWeight = FontWeight.Medium)
                            }
                        }

                        Button(
                            onClick = { showTicketDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("+ Submit Technical Support Ticket", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        AnimatedVisibility(visible = ticketSubmitted) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                                border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Support ticket submitted! Ticket ID: #TK-2026-8812", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // SUBMIT SUPPORT TICKET DIALOG
    if (showTicketDialog) {
        var querySubject by remember { mutableStateOf("") }
        var queryDetails by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTicketDialog = false },
            title = { Text("Submit Technical Support Ticket", fontWeight = FontWeight.Bold, color = GovtTextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = querySubject,
                        onValueChange = { querySubject = it },
                        label = { Text("Issue Subject") },
                        placeholder = { Text("e.g. Offline sync error in Seam II") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = queryDetails,
                        onValueChange = { queryDetails = it },
                        label = { Text("Description & Pit Details") },
                        placeholder = { Text("Describe error message or technical query...") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (querySubject.isNotBlank()) {
                            ticketSubmitted = true
                            Toast.makeText(context, "✅ Support Ticket #TK-2026-8812 Dispatched!", Toast.LENGTH_SHORT).show()
                        }
                        showTicketDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Submit Ticket", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTicketDialog = false }) {
                    Text("Cancel", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun GuideModuleBlock(title: String, desc: String) {
    Column {
        Text("• $title", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GovtTextDark)
        Text(desc, fontSize = 11.sp, color = GovtTextMuted)
    }
}

@Composable
fun BulletPointText(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text("• ", fontWeight = FontWeight.Bold, color = GovtNavyPrimary, fontSize = 12.sp)
        Text(text, fontSize = 11.sp, color = GovtTextDark, lineHeight = 16.sp)
    }
}

@Composable
fun FaqAccordionCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GovtTextDark,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = GovtNavyPrimary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = GovtCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 12.sp,
                        color = GovtTextDark,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
