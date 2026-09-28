package com.example.coalguard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted

data class GlobalSearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // MINES, VIOLATIONS, INSPECTIONS, REGULATIONS, CONTRACTORS
    val targetRoute: String,
    val icon: ImageVector,
    val badgeColor: Color
)

val GLOBAL_SEARCH_DATABASE = listOf(
    // Mines
    GlobalSearchResultItem("s1", "Govindpur Colliery (BCCL)", "Underground Pit • Jharia Coalfield, Jharkhand", "MINES", "colliery_manager", Icons.Default.PrecisionManufacturing, Color(0xFF0284C7)),
    GlobalSearchResultItem("s2", "Dhori Khas Colliery (CCL)", "Underground Pit • Bokaro Coalfield, Jharkhand", "MINES", "mines_map", Icons.Default.Map, Color(0xFF0284C7)),
    GlobalSearchResultItem("s3", "Tetaria Khar OCP (ECL)", "Opencast Mega Pit • Raniganj Coalfield, WB", "MINES", "mines_map", Icons.Default.Map, Color(0xFF0284C7)),
    GlobalSearchResultItem("s4", "Jayant Mega OCP (NCL)", "Opencast Pit • Singrauli Coalfield, MP", "MINES", "mines_map", Icons.Default.Map, Color(0xFF0284C7)),

    // Violations
    GlobalSearchResultItem("s5", "PPE Safety Breach (Hard-Hat)", "Worker missing mandatory hard-hat near East Pit 3", "VIOLATIONS", "violations", Icons.Default.Warning, Color(0xFFDC2626)),
    GlobalSearchResultItem("s6", "Unsecured High Voltage Cable", "Exposed wiring near haulage winch gallery 3", "VIOLATIONS", "violations", Icons.Default.Warning, Color(0xFFDC2626)),
    GlobalSearchResultItem("s7", "Haul Road Berm Defect", "Bench 2 berm height below H >= D/2 statutory ratio", "VIOLATIONS", "violations", Icons.Default.Warning, Color(0xFFDC2626)),

    // Inspections
    GlobalSearchResultItem("s8", "Inspection #INSP-2026-1042", "Safety Audit by Smt. Ananya Sen at Govindpur", "INSPECTIONS", "inspections", Icons.AutoMirrored.Filled.Assignment, Color(0xFF059669)),
    GlobalSearchResultItem("s9", "Inspection #INSP-2026-9912", "Gas & Ventilation Audit by Inspector S. Roy", "INSPECTIONS", "inspections", Icons.AutoMirrored.Filled.Assignment, Color(0xFF059669)),

    // Regulations
    GlobalSearchResultItem("s10", "CMR 2017 Regulation 115", "Personal Protective Equipment (Hard-Hat & High-Vis Vest)", "REGULATIONS", "registers", Icons.Default.Description, GovtGoldAmber),
    GlobalSearchResultItem("s11", "CMR 2017 Regulation 153", "Underground Gas Testing & Methane Isolation Thresholds", "REGULATIONS", "registers", Icons.Default.Description, GovtGoldAmber),
    GlobalSearchResultItem("s12", "CMR 2017 Regulation 83", "Opencast Haul Road Berm Height (H >= D/2 Ratio)", "REGULATIONS", "registers", Icons.Default.Description, GovtGoldAmber),
    GlobalSearchResultItem("s13", "Mines Act 1952 Section 22", "DGMS Statutory Improvement & Prohibition Directives", "REGULATIONS", "compliance", Icons.Default.Verified, GovtGoldAmber),

    // Contractors
    GlobalSearchResultItem("s14", "L&T Mining Services", "Active Contractor • 34 Workers Checked-In", "CONTRACTORS", "contractors", Icons.Default.Engineering, Color(0xFF7C3AED)),
    GlobalSearchResultItem("s15", "M/s Bharat Explosives", "Active Contractor • Safety Certificate Expiring Soon", "CONTRACTORS", "contractors", Icons.Default.Engineering, Color(0xFF7C3AED))
)

@Composable
fun GlobalSearchDialog(
    onDismiss: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val filteredResults = remember(query, selectedCategoryFilter) {
        GLOBAL_SEARCH_DATABASE.filter { item ->
            val matchesCategory = when (selectedCategoryFilter) {
                "MINES" -> item.category == "MINES"
                "VIOLATIONS" -> item.category == "VIOLATIONS"
                "INSPECTIONS" -> item.category == "INSPECTIONS"
                "REGULATIONS" -> item.category == "REGULATIONS"
                "CONTRACTORS" -> item.category == "CONTRACTORS"
                else -> true
            }

            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.subtitle.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CoalGuard Global Search", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GovtTextDark)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Search mines, violations, inspections, regulations & contractors", fontSize = 11.sp, color = GovtTextMuted)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search anything (e.g. Govindpur, CMR 115, Hard-Hat)...", fontSize = 11.sp, color = GovtTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GovtNavyPrimary) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GovtTextMuted)
                            }
                        }
                    },
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

                // Filter Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "MINES", "VIOLATIONS", "REGULATIONS").forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 9.sp, fontWeight = if (selectedCategoryFilter == cat) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GovtNavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = GovtSurfaceWhite,
                                labelColor = GovtTextDark
                            )
                        )
                    }
                }

                HorizontalDivider(color = GovtCardBorder)

                // Results List
                if (filteredResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = GovtTextMuted, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No matching records found", fontSize = 12.sp, color = GovtTextMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredResults) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onDismiss()
                                        onNavigateToRoute(item.targetRoute)
                                    },
                                colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GovtCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(item.badgeColor.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(item.icon, contentDescription = null, tint = item.badgeColor, modifier = Modifier.size(16.dp))
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(item.subtitle, fontSize = 10.sp, color = GovtTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }

                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
            ) {
                Text("Close Search", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = GovtSurfaceWhite,
        shape = RoundedCornerShape(16.dp)
    )
}
