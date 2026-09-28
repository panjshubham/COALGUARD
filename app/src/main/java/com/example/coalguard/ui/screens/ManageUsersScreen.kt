package com.example.coalguard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.data.local.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    users: List<UserEntity> = emptyList(),
    onNavigateBack: () -> Unit = {},
    onAddUserClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("all") }

    val sampleUsers = remember {
        listOf(
            UserEntity("u1", "Er. Rajesh Kumar", "rajesh.kumar@coalindia.in", "mine_official", 1, "2023-01-15"),
            UserEntity("u2", "Dr. Arindam Sen", "arindam.sen@coalindia.in", "corporate", null, "2023-02-20"),
            UserEntity("u3", "Shri S. K. Verma", "sk.verma@dgms.gov.in", "regulator", null, "2023-03-10"),
            UserEntity("u4", "Inspector S. Roy", "s.roy@coalindia.in", "mine_official", 2, "2023-04-05")
        )
    }

    val displayUsers = if (users.isNotEmpty()) users else sampleUsers

    val filteredUsers = remember(displayUsers, searchQuery, selectedRoleFilter) {
        displayUsers.filter { u ->
            val matchesSearch = searchQuery.isBlank() ||
                    u.name.contains(searchQuery, ignoreCase = true) ||
                    u.email.contains(searchQuery, ignoreCase = true)

            val matchesRole = when (selectedRoleFilter) {
                "mine_official" -> u.role.equals("mine_official", ignoreCase = true)
                "corporate" -> u.role.equals("corporate", ignoreCase = true)
                "regulator" -> u.role.equals("regulator", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesRole
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mine Officials & Authorities", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Provisioning new CIL/DGMS Official...", Toast.LENGTH_SHORT).show()
                    onAddUserClick()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Provision Officer")
            }
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
                                .background(Color(0xFF38BDF8), shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AUTHORITY & JURISDICTION ROSTER", style = MaterialTheme.typography.labelSmall, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Provisioned CIL Officials & DGMS Regulators", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search officer name, email, or mine...", color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "all" to "All Roles",
                        "mine_official" to "Mine Officials",
                        "corporate" to "Corporate HQ",
                        "regulator" to "DGMS Regulators"
                    ).forEach { (roleKey, label) ->
                        FilterChip(
                            selected = selectedRoleFilter == roleKey,
                            onClick = { selectedRoleFilter = roleKey },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1E293B),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF0F172A),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }

            if (filteredUsers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Officials Found", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("No CIL mine officials or DGMS regulators match the selected search/role.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                        }
                    }
                }
            } else {
                items(filteredUsers) { user ->
                    val roleLower = user.role.lowercase()
                    val (roleBg, roleFg, roleTitle) = when {
                        roleLower.contains("corporate") -> Triple(Color(0xFF4C1D95), Color(0xFFC084FC), "Corporate HQ")
                        roleLower.contains("regulator") -> Triple(Color(0xFF7F1D1D), Color(0xFFFCA5A5), "DGMS Regulator")
                        else -> Triple(Color(0xFF0C4A6E), Color(0xFF38BDF8), "Mine Official")
                    }

                    val mineName = when (user.assignedMineId) {
                        1 -> "Govindpur Colliery (BCCL)"
                        2 -> "Dhori Khas (CCL)"
                        3 -> "Karo Special Seam (CCL)"
                        4 -> "Tetaria Khar (ECL)"
                        else -> "National Headquarters / All Mines"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    color = roleBg,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = roleTitle.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = roleFg
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(mineName, style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }

                                Surface(
                                    color = Color(0xFF064E3B),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("ACTIVE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
