package com.example.coalguard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Landing : Screen("landing", "Overview", Icons.Default.Home)
    
    // Command Center
    object Dashboard : Screen("dashboard", "Corporate HQ", Icons.Default.Business)
    object CollieryManager : Screen("colliery_manager", "Colliery Manager", Icons.Default.PrecisionManufacturing)
    object RegulatorPortal : Screen("regulator_portal", "Regulator Portal", Icons.Default.Security)
    
    // Operations
    object MinesMap : Screen("mines_map", "Mines Map", Icons.Default.Map)
    object Compliance : Screen("compliance", "Compliance", Icons.Default.Verified)
    object Inspections : Screen("inspections", "Inspections", Icons.AutoMirrored.Filled.Assignment)
    object Violations : Screen("violations", "Violations", Icons.Default.Warning)
    object Contractors : Screen("contractors", "Contractors", Icons.Default.Engineering)
    
    // Safety & Records
    object Statutory : Screen("registers", "CMR Statutory Books", Icons.Default.Description)
    object PpeMonitor : Screen("ppe_monitor", "PPE Safety Monitor", Icons.Default.CameraAlt)
    object WaterInrush : Screen("water_inrush", "Water Inrush AI", Icons.Default.WaterDrop)
    object AuditLog : Screen("audit_log", "Audit Log", Icons.Default.History)
    
    // Other Utilities
    object Financial : Screen("financial", "Financial & ROI", Icons.Default.AttachMoney)
    object AiWorkbench : Screen("ai_workbench", "AI Workbench", Icons.Default.Psychology)
    object ManageUsers : Screen("manage_users", "Manage Mine Officials", Icons.Default.People)
    object DataImport : Screen("data_import", "Bulk Data Import", Icons.Default.FileUpload)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Attendance : Screen("attendance", "Workforce Attendance", Icons.Default.Badge)
    object HelpSupport : Screen("help_support", "Help & Support", Icons.AutoMirrored.Filled.HelpOutline)
    object Capture : Screen("capture", "Hazard Log", Icons.Default.AddAPhoto)
}
