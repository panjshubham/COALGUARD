package com.example.coalguard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

// Enterprise User Data Model
data class EnterpriseUser(
    val id: String,
    val name: String,
    val email: String,
    val role: EnterpriseRole,
    val jurisdiction: String,
    val clearanceStatus: String = "Active Clearance",
    val provisionedDate: String = "12 Sept 2026"
)

enum class EnterpriseRole(val displayName: String, val badgeBg: Color, val badgeText: Color) {
    REGULATOR("DGMS Regulator", Color(0xFFE0F2FE), Color(0xFF0284C7)),
    MINE_OFFICIAL("Mine Official", Color(0xFFDCFCE7), Color(0xFF16A34A)),
    CORPORATE_HQ("Corporate HQ", Color(0xFFFFEDD5), Color(0xFFEA580C))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All") }
    var showProvisionDialog by remember { mutableStateOf(false) }

    // Sample User List matching Corporate HQ Access Control
    val usersList = remember {
        mutableStateListOf(
            EnterpriseUser("1", "Deb", "hub@gmail.com", EnterpriseRole.REGULATOR, "DGMS Statutory Regulatory Directorate"),
            EnterpriseUser("2", "AR", "debnatharpan56@gmail.com", EnterpriseRole.MINE_OFFICIAL, "Govindpur Colliery (Mine ID: 42)"),
            EnterpriseUser("3", "Suraj Shaw", "surajshaw12004@gmail.com", EnterpriseRole.MINE_OFFICIAL, "Dhori Khas Mine (Mine ID: 43)"),
            EnterpriseUser("4", "Hub", "as6@gmail.com", EnterpriseRole.MINE_OFFICIAL, "Karo Special Seam (Mine ID: 44)"),
            EnterpriseUser("5", "Rajesh Kumar", "rajesh.k@coalindia.in", EnterpriseRole.CORPORATE_HQ, "HQ Strategy & Compliance"),
            EnterpriseUser("6", "Vikram Singh", "vikram.s@dgms.gov.in", EnterpriseRole.REGULATOR, "Dhanbad Region I Inspection")
        )
    }

    val filteredUsers = usersList.filter { user ->
        (searchQuery.isEmpty() || user.name.contains(searchQuery, ignoreCase = true) || user.email.contains(searchQuery, ignoreCase = true) || user.jurisdiction.contains(searchQuery, ignoreCase = true)) &&
        (selectedRoleFilter == "All" || user.role.displayName.equals(selectedRoleFilter, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("COAL INDIA LIMITED", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("सत्यमेव जयते | ENTERPRISE ACCESS CONTROL & USER GOVERNANCE", fontSize = 9.sp, color = GovtGoldAmber)
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Surface(
                        color = GovtGoldTint,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, GovtGoldAmber)
                    ) {
                        Text(
                            text = "CORPORATE HQ ADMINISTRATION • DGMS Access Control",
                            color = GovtGoldAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enterprise User Management",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Provision statutory clearance, assign mine jurisdictions, and govern system roles.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { showProvisionDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Provision New User", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Stat Cards Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiStatCard(
                            title = "TOTAL USERS",
                            value = "${usersList.size}",
                            icon = Icons.Default.Group,
                            iconBg = GovtGoldTint,
                            iconColor = GovtGoldAmber,
                            modifier = Modifier.weight(1f)
                        )
                        KpiStatCard(
                            title = "MINE OFFICIALS",
                            value = "${usersList.count { it.role == EnterpriseRole.MINE_OFFICIAL }}",
                            icon = Icons.Default.Engineering,
                            iconBg = Color(0xFFDCFCE7),
                            iconColor = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiStatCard(
                            title = "CORPORATE HQ",
                            value = "${usersList.count { it.role == EnterpriseRole.CORPORATE_HQ }}",
                            icon = Icons.Default.Business,
                            iconBg = Color(0xFFFFEDD5),
                            iconColor = Color(0xFFEA580C),
                            modifier = Modifier.weight(1f)
                        )
                        KpiStatCard(
                            title = "REGULATORS",
                            value = "${usersList.count { it.role == EnterpriseRole.REGULATOR }}",
                            icon = Icons.Default.Gavel,
                            iconBg = Color(0xFFE0F2FE),
                            iconColor = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Search & Filter Section
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by name, email, or mine...", color = GovtTextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GovtNavyPrimary) },
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
                        
                        // Filter Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("All", "DGMS Regulator", "Mine Official", "Corporate HQ").forEach { roleName ->
                                FilterChip(
                                    selected = selectedRoleFilter == roleName,
                                    onClick = { selectedRoleFilter = roleName },
                                    label = { Text(roleName, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Users List
            items(filteredUsers) { user ->
                UserCardItem(user = user)
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Provision New User Dialog
    if (showProvisionDialog) {
        var newName by remember { mutableStateOf("") }
        var newEmail by remember { mutableStateOf("") }
        var newJurisdiction by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showProvisionDialog = false },
            title = { Text("Provision Statutory Clearance", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newJurisdiction,
                        onValueChange = { newJurisdiction = it },
                        label = { Text("Assigned Jurisdiction / Mine ID") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newEmail.isNotBlank()) {
                            usersList.add(
                                EnterpriseUser(
                                    id = System.currentTimeMillis().toString(),
                                    name = newName,
                                    email = newEmail,
                                    role = EnterpriseRole.MINE_OFFICIAL,
                                    jurisdiction = if (newJurisdiction.isBlank()) "Govindpur Colliery (Mine ID: 42)" else newJurisdiction
                                )
                            )
                        }
                        showProvisionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Provision Clearance", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProvisionDialog = false }) {
                    Text("Cancel", color = GovtTextMuted)
                }
            }
        )
    }
}

@Composable
fun KpiStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 9.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = GovtTextDark)
            }
        }
    }
}

@Composable
fun UserCardItem(user: EnterpriseUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // User Avatar Circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(GovtGoldTint),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                    Text(
                        text = if (initials.isEmpty()) "U" else initials,
                        fontWeight = FontWeight.Bold,
                        color = GovtGoldAmber,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GovtTextDark)
                    Text(text = user.email, fontSize = 12.sp, color = GovtTextMuted)
                }
                
                // Role Badge
                Surface(
                    color = user.role.badgeBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = user.role.displayName,
                        color = user.role.badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GovtCardBorder)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("JURISDICTION / MINE", fontSize = 9.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = user.jurisdiction,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (user.jurisdiction.contains("Pending")) GovtGoldAmber else GovtTextDark
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("CLEARANCE", fontSize = 9.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "• ${user.clearanceStatus}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }
            }
        }
    }
}
