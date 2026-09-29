package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.KeyboardType
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

// Inspector Profile Data Model matching CoalGuard Web Schema
data class InspectorProfileData(
    var fullName: String = "Shri R. K. Mahapatra",
    var designation: String = "Chief Inspector of Mines (DGMS)",
    var badgeId: String = "DGMS-EZ-2024-8841",
    var subsidiary: String = "BCCL - Bharat Coking Coal Ltd (Dhanbad)",
    var primaryPhone: String = "+91 98312 45678",
    var email: String = "rk.mahapatra@dgms.gov.in",
    var secondaryPhone: String = "+91 94311 87654",
    var familyContactName: String = "Smt. Sunita Mahapatra",
    var familyRelationship: String = "Spouse",
    var familyAddress: String = "Quarter B-14, CIL Officers Colony, Koyla Nagar, Dhanbad - 826005",
    var bloodGroup: String = "O+",
    var medicalAlert: String = "High-particulate respiratory sensitivity (carry personal inhaler)",
    var notifyFamilyOnAlert: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf(InspectorProfileData()) }
    var isSaved by remember { mutableStateOf(false) }
    var testAlertSent by remember { mutableStateOf(false) }

    var isGeofenceEnabled by remember { mutableStateOf(true) }
    var isHashSealingEnabled by remember { mutableStateOf(true) }
    var isPushNotificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("सत्यमेव जयते | OFFICER PROFILE & EMERGENCY PROTOCOL", fontSize = 9.sp, color = GovtGoldAmber)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            // Header Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GovtNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = profile.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                                Text(
                                    text = if (initials.isEmpty()) "RM" else initials,
                                    color = GovtGoldAmber,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = profile.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GovtTextDark)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = GovtGoldTint,
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, GovtGoldAmber)
                                    ) {
                                        Text(
                                            text = profile.badgeId,
                                            color = GovtGoldAmber,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(text = profile.designation, fontSize = 12.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                                Text(text = profile.subsidiary, fontSize = 11.sp, color = GovtTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                testAlertSent = true
                                Toast.makeText(context, "🚨 Simulated Emergency SMS Broadcast to ${profile.secondaryPhone} (${profile.familyContactName})", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Family Emergency SMS Dispatch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Test Alert Feedback Banner
            item {
                AnimatedVisibility(visible = testAlertSent) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Emergency Family Alert Simulated!", fontWeight = FontWeight.Bold, color = Color(0xFF065F46), fontSize = 13.sp)
                                Text(
                                    "[DGMS-CIL ALERT] Officer ${profile.fullName} safety status check verified. Secondary line (${profile.secondaryPhone}) notified.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: DGMS CMR 2017 Emergency Protocol & Family Details
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    border = BorderStroke(1.dp, GovtGoldAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = GovtGoldAmber)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("FAMILY EMERGENCY PROTOCOL", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtNavyPrimary)
                                    Text("Mandatory DGMS CMR 2017 Rescue Dispatch Contact", fontSize = 10.sp, color = GovtTextMuted)
                                }
                            }

                            Surface(color = GovtGoldTint, shape = RoundedCornerShape(4.dp)) {
                                Text("MANDATORY CMR Reg 129", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                            }
                        }

                        OutlinedTextField(
                            value = profile.secondaryPhone,
                            onValueChange = { profile = profile.copy(secondaryPhone = it); isSaved = false },
                            label = { Text("Secondary Mobile No. (Family Emergency Line)", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GovtNavyPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = profile.familyContactName,
                            onValueChange = { profile = profile.copy(familyContactName = it); isSaved = false },
                            label = { Text("Next of Kin / Family Contact Name", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GovtNavyPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = profile.familyRelationship,
                                onValueChange = { profile = profile.copy(familyRelationship = it); isSaved = false },
                                label = { Text("Relationship", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GovtSurfaceWhite,
                                    unfocusedContainerColor = GovtSurfaceWhite,
                                    focusedBorderColor = GovtNavyPrimary,
                                    unfocusedBorderColor = GovtCardBorder,
                                    focusedTextColor = GovtTextDark,
                                    unfocusedTextColor = GovtTextDark
                                ),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = profile.bloodGroup,
                                onValueChange = { profile = profile.copy(bloodGroup = it); isSaved = false },
                                label = { Text("Blood Group", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = GovtSurfaceWhite,
                                    unfocusedContainerColor = GovtSurfaceWhite,
                                    focusedBorderColor = GovtNavyPrimary,
                                    unfocusedBorderColor = GovtCardBorder,
                                    focusedTextColor = GovtTextDark,
                                    unfocusedTextColor = GovtTextDark
                                ),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = profile.familyAddress,
                            onValueChange = { profile = profile.copy(familyAddress = it); isSaved = false },
                            label = { Text("Family Residential Address (Colony / Quarter)", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = GovtNavyPrimary) },
                            modifier = Modifier.fillMaxWidth(),
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

                        OutlinedTextField(
                            value = profile.medicalAlert,
                            onValueChange = { profile = profile.copy(medicalAlert = it); isSaved = false },
                            label = { Text("Medical Precautions / Underground Pit Allergies", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = GovtNavyPrimary) },
                            modifier = Modifier.fillMaxWidth(),
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

                        HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Automated SOS Incident Broadcast", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GovtTextDark)
                                Text("Auto-SMS to family line if gas levels exceed 1.25% or pit evacuation is flagged", fontSize = 10.sp, color = GovtTextMuted)
                            }
                            Switch(
                                checked = profile.notifyFamilyOnAlert,
                                onCheckedChange = { profile = profile.copy(notifyFamilyOnAlert = it); isSaved = false },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GovtNavyPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Section 2: Officer Statutory Credentials
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    border = BorderStroke(1.dp, GovtCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("OFFICER STATUTORY IDENTIFICATION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GovtNavyPrimary)

                        OutlinedTextField(
                            value = profile.fullName,
                            onValueChange = { profile = profile.copy(fullName = it); isSaved = false },
                            label = { Text("Full Name", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = profile.designation,
                            onValueChange = { profile = profile.copy(designation = it); isSaved = false },
                            label = { Text("Statutory Designation / Role", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = profile.badgeId,
                            onValueChange = { profile = profile.copy(badgeId = it); isSaved = false },
                            label = { Text("DGMS Officer Badge ID", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = profile.subsidiary,
                            onValueChange = { profile = profile.copy(subsidiary = it); isSaved = false },
                            label = { Text("CIL Subsidiary & Circle", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = profile.primaryPhone,
                            onValueChange = { profile = profile.copy(primaryPhone = it); isSaved = false },
                            label = { Text("Primary Mobile No.", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = profile.email,
                            onValueChange = { profile = profile.copy(email = it); isSaved = false },
                            label = { Text("Official Email (NIC / DGMS)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = GovtSurfaceWhite,
                                unfocusedContainerColor = GovtSurfaceWhite,
                                focusedBorderColor = GovtNavyPrimary,
                                unfocusedBorderColor = GovtCardBorder,
                                focusedTextColor = GovtTextDark,
                                unfocusedTextColor = GovtTextDark
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Section 3: Governance & Security Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("GOVERNANCE & SECURITY PREFERENCES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingToggleRow("Offline Anti-Proxy Geofence", "Validate GPS satellite boundaries against mine perimeter", isGeofenceEnabled) { isGeofenceEnabled = it }
                        HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 8.dp))
                        SettingToggleRow("SHA-256 Ledger Auto-Sealing", "Automatically compute cryptographic hashes for statutory forms", isHashSealingEnabled) { isHashSealingEnabled = it }
                        HorizontalDivider(color = GovtCardBorder, modifier = Modifier.padding(vertical = 8.dp))
                        SettingToggleRow("SLA Alert Push Notifications", "Receive shift escalation alerts for critical safety directives", isPushNotificationsEnabled) { isPushNotificationsEnabled = it }
                    }
                }
            }

            // Save & Logout Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            isSaved = true
                            Toast.makeText(context, "✅ Profile & Family Emergency Protocol Saved!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Profile & Emergency Protocol", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LOG OUT OF COALGUARD", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), fontSize = 13.sp)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun SettingToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = GovtTextDark)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = GovtTextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GovtNavyPrimary
            )
        )
    }
}
