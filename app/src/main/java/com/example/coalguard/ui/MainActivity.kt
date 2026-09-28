package com.example.coalguard.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coalguard.data.local.SessionManager
import com.example.coalguard.data.model.AuditLedgerEntry
import com.example.coalguard.data.model.ComplianceItem
import com.example.coalguard.data.model.Inspection
import com.example.coalguard.data.model.SystemAlertEntity
import com.example.coalguard.data.model.UserRole
import com.example.coalguard.data.model.Violation
import com.example.coalguard.data.remote.SupabaseClientInstance
import com.example.coalguard.data.sync.NetworkSyncCallback
import com.example.coalguard.domain.AlertsEngine
import com.example.coalguard.domain.SystemAlert
import com.example.coalguard.ui.components.CoalGuardLogo
import com.example.coalguard.ui.components.GlobalSearchDialog
import com.example.coalguard.ui.components.NotificationCenterSheet
import com.example.coalguard.ui.navigation.Screen
import com.example.coalguard.ui.screens.AIScanScreen
import com.example.coalguard.ui.screens.AiWorkbenchScreen
import com.example.coalguard.ui.screens.AttendanceScreen
import com.example.coalguard.ui.screens.AuditLogScreen
import com.example.coalguard.ui.screens.CollieryManagerScreen
import com.example.coalguard.ui.screens.ComplianceScreen
import com.example.coalguard.ui.screens.ContractorsScreen
import com.example.coalguard.ui.screens.CorporateDashboardScreen
import com.example.coalguard.ui.screens.DataImportScreen
import com.example.coalguard.ui.screens.FinancialScreen
import com.example.coalguard.ui.screens.HelpSupportScreen
import com.example.coalguard.ui.screens.InspectionsScreen
import com.example.coalguard.ui.screens.LandingScreen
import com.example.coalguard.ui.screens.ManageUsersScreen
import com.example.coalguard.ui.screens.MinesMapScreen
import com.example.coalguard.ui.screens.PpeMonitorScreen
import com.example.coalguard.ui.screens.ProfileScreen
import com.example.coalguard.ui.screens.RegulatorPortalScreen
import com.example.coalguard.ui.screens.StatutoryRegistersScreen
import com.example.coalguard.ui.screens.UserManagementScreen
import com.example.coalguard.ui.screens.ViolationDetailScreen
import com.example.coalguard.ui.screens.ViolationsScreen
import com.example.coalguard.ui.screens.auth.LoginScreen
import com.example.coalguard.ui.screens.capture.CaptureScreen
import com.example.coalguard.ui.screens.water.WaterInrushScreen
import com.example.coalguard.ui.theme.CoalGuardTheme
import com.example.coalguard.ui.viewmodel.DashboardViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var networkSyncCallback: NetworkSyncCallback
    private val dashboardViewModel: DashboardViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        networkSyncCallback = NetworkSyncCallback(applicationContext)
        networkSyncCallback.register()

        setContent {
            CoalGuardTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()

                val startDestination = Screen.Landing.route
                val dashboardState by dashboardViewModel.uiState.collectAsState()
                var showNotificationSheet by remember { mutableStateOf(false) }
                var showGlobalSearchDialog by remember { mutableStateOf(false) }

                val sampleCompliance = listOf(
                    ComplianceItem("1", mineId = 1, category = "Safety", title = "Ventilation Shaft Check", dueDate = "2023-10-01", status = "overdue"),
                    ComplianceItem("2", mineId = 1, category = "Environment", title = "Gas Monitoring Calibration", dueDate = "2023-11-15", status = "pending")
                )
                val sampleViolations = remember {
                    mutableStateListOf(
                        Violation(1, mineId = 1, category = "Unsecured Cable Runway", description = "Exposed wiring near East Pit 3", severity = "high", status = "open")
                    )
                }
                val sampleInspections = remember {
                    mutableStateListOf(
                        Inspection(1, mineId = 1, contractorId = 1, date = "2023-10-15", inspectorName = "Er. Rajesh Kumar", syncedAt = "2023-10-15", trackingId = "INSP-2023-9912")
                    )
                }
                val sampleAuditEntries = remember {
                    mutableStateListOf(
                        AuditLedgerEntry(1, tableName = "VIOLATION", recordId = 1, action = "INSERT", dataHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", prevHash = null, createdAt = "1695000000000")
                    )
                }
                var selectedViolation by remember { mutableStateOf<Violation?>(sampleViolations.first()) }

                val alertsEngine = remember { AlertsEngine(applicationContext) }
                var persistedAlerts by remember { mutableStateOf<List<SystemAlertEntity>>(emptyList()) }

                LaunchedEffect(Unit) {
                    launch {
                        persistedAlerts = alertsEngine.evaluateAndPersistAlerts(sampleCompliance, dashboardState.violations.ifEmpty { sampleViolations })
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentRoute != "login",
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = Color.White,
                            windowInsets = WindowInsets.statusBars
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Top Header & Dynamic Active Role Badge
                                val sessionManager = remember(currentRoute) { SessionManager(context) }
                                val activeRole = sessionManager.getRole()

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CoalGuardLogo(size = 36.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("CIL · DGMS", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF0F172A))
                                    }

                                    Surface(
                                        color = when (activeRole) {
                                            UserRole.CORPORATE -> Color(0xFFFEF3C7)
                                            UserRole.REGULATOR -> Color(0xFFE0F2FE)
                                            else -> Color(0xFFD1FAE5)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "• ${activeRole.title.uppercase()}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (activeRole) {
                                                UserRole.CORPORATE -> Color(0xFFD97706)
                                                UserRole.REGULATOR -> Color(0xFF0284C7)
                                                else -> Color(0xFF059669)
                                            }
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE2E8F0))

                                // GROUP 1: COMMAND CENTER (Role-Filtered)
                                Text("COMMAND CENTER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 4.dp))
                                buildList {
                                    if (activeRole == UserRole.CORPORATE) {
                                        add(Screen.Dashboard to "Corporate HQ")
                                    }
                                    add(Screen.CollieryManager to "Colliery Manager")
                                    if (activeRole == UserRole.REGULATOR || activeRole == UserRole.CORPORATE) {
                                        add(Screen.RegulatorPortal to "Regulator Portal")
                                    }
                                }.forEach { (screen, label) ->
                                    val selected = currentRoute == screen.route
                                    DrawerItemCustom(
                                        icon = screen.icon,
                                        label = label,
                                        selected = selected,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // GROUP 2: OPERATIONS
                                Text("OPERATIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 4.dp))
                                listOf(
                                    Screen.MinesMap to "Mines Map",
                                    Screen.Attendance to "Workforce Attendance",
                                    Screen.Compliance to "Compliance",
                                    Screen.Inspections to "Inspections",
                                    Screen.Violations to "Violations",
                                    Screen.Contractors to "Contractors"
                                ).forEach { (screen, label) ->
                                    val selected = currentRoute == screen.route
                                    DrawerItemCustom(
                                        icon = screen.icon,
                                        label = label,
                                        selected = selected,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // GROUP 3: SAFETY & RECORDS
                                Text("SAFETY & RECORDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 4.dp))
                                buildList {
                                    add(Screen.Statutory to "CMR Statutory Books")
                                    add(Screen.PpeMonitor to "PPE Safety Monitor")
                                    add(Screen.WaterInrush to "Water Inrush & Aquifer AI")
                                    add(Screen.HelpSupport to "Help & Support Center")
                                    if (activeRole == UserRole.CORPORATE || activeRole == UserRole.REGULATOR) {
                                        add(Screen.AuditLog to "Audit Log")
                                    }
                                }.forEach { (screen, label) ->
                                    val selected = currentRoute == screen.route
                                    DrawerItemCustom(
                                        icon = screen.icon,
                                        label = label,
                                        selected = selected,
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }

                                // GROUP 4: ADMINISTRATION (Corporate HQ Admin Only)
                                if (activeRole == UserRole.CORPORATE) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("ADMINISTRATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(vertical = 4.dp))
                                    listOf(
                                        Screen.ManageUsers to "Manage Mine Officials",
                                        Screen.DataImport to "Bulk Data Import",
                                        Screen.Financial to "Financial & ROI Overview"
                                    ).forEach { (screen, label) ->
                                        val selected = currentRoute == screen.route
                                        DrawerItemCustom(
                                            icon = screen.icon,
                                            label = label,
                                            selected = selected,
                                            onClick = {
                                                coroutineScope.launch { drawerState.close() }
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // BOTTOM STATUS CARD
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF16A34A)))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("System Secure · DGMS Valid", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("12:41 IST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // FOOTER ACTIONS
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { coroutineScope.launch { drawerState.close() } }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Collapse", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            coroutineScope.launch { drawerState.close() }
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign out", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            if (currentRoute != "login") {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    NavigationBar(
                                        containerColor = Color.White,
                                        tonalElevation = 12.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val items = listOf(
                                            Screen.Landing,
                                            Screen.CollieryManager,
                                            Screen.AiWorkbench, // Middle AI Scan Button
                                            Screen.Violations,
                                            Screen.Profile
                                        )
                                        items.forEachIndexed { index, screen ->
                                            val isSelected = currentRoute == screen.route
                                            if (index == 2) {
                                                NavigationBarItem(
                                                    selected = isSelected,
                                                    onClick = {
                                                        navController.navigate(screen.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                    icon = { Spacer(modifier = Modifier.size(26.dp)) },
                                                    label = {
                                                        Text(
                                                            text = "AI Scan",
                                                            color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF64748B),
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 11.sp
                                                        )
                                                    },
                                                    colors = NavigationBarItemDefaults.colors(
                                                        indicatorColor = Color.Transparent
                                                    )
                                                )
                                            } else {
                                                NavigationBarItem(
                                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                                    label = {
                                                        Text(
                                                            text = when(screen) {
                                                                Screen.Landing -> "Home"
                                                                Screen.CollieryManager -> "Colliery"
                                                                Screen.Violations -> "Violations"
                                                                Screen.Profile -> "Profile"
                                                                else -> screen.title
                                                            },
                                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                            fontSize = 11.sp
                                                        )
                                                    },
                                                    selected = isSelected,
                                                    colors = NavigationBarItemDefaults.colors(
                                                        selectedIconColor = Color(0xFF1D4ED8),
                                                        selectedTextColor = Color(0xFF1D4ED8),
                                                        indicatorColor = Color(0xFFEFF6FF),
                                                        unselectedIconColor = Color(0xFF64748B),
                                                        unselectedTextColor = Color(0xFF64748B)
                                                    ),
                                                    onClick = {
                                                        navController.navigate(screen.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Floating Center Scanner FAB with White Border Ring & Elevation Cutout
                                    val isAiActive = currentRoute == Screen.AiWorkbench.route
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .offset(y = (-22).dp)
                                            .size(60.dp)
                                            .clickable {
                                                navController.navigate(Screen.AiWorkbench.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                        shape = CircleShape,
                                        color = Color.White,
                                        shadowElevation = 8.dp
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(3.dp)
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(
                                                    if (isAiActive) Color(0xFF7C3AED) else Color(0xFF8B5CF6)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DocumentScanner,
                                                contentDescription = "AI Scanner (PPE & OCR)",
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier
                                .padding(bottom = innerPadding.calculateBottomPadding())
                                .fillMaxSize()
                        ) {
                            composable(Screen.Landing.route) {
                                val currentUser = SupabaseClientInstance.client.auth.currentUserOrNull()
                                val isUserLoggedIn = currentUser != null || (currentRoute != "login" && currentRoute != null && currentRoute != "landing")

                                LandingScreen(
                                    isUserLoggedIn = isUserLoggedIn,
                                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                                    onNotificationClick = { showNotificationSheet = true },
                                    onSearchClick = { showGlobalSearchDialog = true },
                                    onNavigateToRoute = { route ->
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    onNavigateToLogin = { navController.navigate("login") }
                                )
                            }
                            composable(Screen.CollieryManager.route) {
                                CollieryManagerScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onNotificationClick = { showNotificationSheet = true },
                                    onSearchClick = { showGlobalSearchDialog = true },
                                    onNavigateToInspections = { navController.navigate(Screen.Inspections.route) },
                                    onNavigateToRegisters = { navController.navigate(Screen.Statutory.route) },
                                    onNavigateToCapture = { navController.navigate(Screen.Capture.route) },
                                    onNavigateToViolations = { navController.navigate(Screen.Violations.route) }
                                )
                            }
                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = { role ->
                                        val targetRoute = when (role.lowercase()) {
                                            "official", "mine_official" -> Screen.CollieryManager.route
                                            "corporate" -> Screen.Dashboard.route
                                            "regulator" -> Screen.RegulatorPortal.route
                                            else -> Screen.Dashboard.route
                                        }
                                        navController.navigate(targetRoute) {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(Screen.Dashboard.route) {
                                CorporateDashboardScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onNotificationClick = { showNotificationSheet = true },
                                    onNavigateToViolations = { navController.navigate(Screen.Violations.route) },
                                    onNavigateToRegisters = { navController.navigate(Screen.Statutory.route) },
                                    onNavigateToUserManagement = { navController.navigate(Screen.ManageUsers.route) }
                                )
                            }
                            composable(Screen.RegulatorPortal.route) {
                                RegulatorPortalScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onNotificationClick = { showNotificationSheet = true },
                                    onNavigateToViolations = { navController.navigate(Screen.Violations.route) },
                                    onNavigateToRegisters = { navController.navigate(Screen.Statutory.route) }
                                )
                            }
                            composable(Screen.PpeMonitor.route) {
                                PpeMonitorScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onLaunchCameraScan = { navController.navigate(Screen.Capture.route) }
                                )
                            }
                            composable(Screen.WaterInrush.route) {
                                WaterInrushScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.MinesMap.route) {
                                MinesMapScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.Financial.route) {
                                FinancialScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.Compliance.route) {
                                ComplianceScreen(
                                    complianceItems = dashboardState.complianceItems.ifEmpty { sampleCompliance },
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.Attendance.route) {
                                AttendanceScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.Inspections.route) {
                                InspectionsScreen(
                                    inspections = dashboardState.inspections.ifEmpty { sampleInspections },
                                    violations = dashboardState.violations.ifEmpty { sampleViolations },
                                    pendingSyncCount = dashboardState.pendingSyncCount,
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onInspectionClick = {},
                                    onViolationClick = { viol ->
                                        selectedViolation = viol
                                    },
                                    onNewInspectionClick = {
                                        navController.navigate(Screen.Capture.route)
                                    }
                                )
                            }
                            composable(Screen.Contractors.route) {
                                ContractorsScreen(
                                    contractors = dashboardState.contractors,
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onContractorClick = {}
                                )
                            }
                            composable(Screen.Violations.route) {
                                ViolationsScreen(
                                    violations = dashboardState.violations.ifEmpty { sampleViolations },
                                    pendingSyncCount = dashboardState.pendingSyncCount,
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } },
                                    onViolationClick = { violation ->
                                        selectedViolation = violation
                                        navController.navigate("violationDetail")
                                    }
                                )
                            }
                            composable("violationDetail") {
                                selectedViolation?.let { viol ->
                                    ViolationDetailScreen(
                                        violation = viol,
                                        onBack = { navController.popBackStack() },
                                        onComputeHash = {
                                            val updated = viol.copy(dataHash = "computed_hash_${System.currentTimeMillis()}")
                                            sampleViolations[0] = updated
                                            selectedViolation = updated
                                            sampleAuditEntries.add(
                                                AuditLedgerEntry(
                                                    id = (100000..999999).random(),
                                                    tableName = "VIOLATION",
                                                    recordId = viol.id,
                                                    action = "UPDATE",
                                                    dataHash = updated.dataHash!!,
                                                    prevHash = sampleAuditEntries.lastOrNull()?.dataHash,
                                                    createdAt = System.currentTimeMillis().toString()
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                            composable(Screen.Statutory.route) {
                                StatutoryRegistersScreen(
                                    auditEntries = dashboardState.auditEntries.ifEmpty { sampleAuditEntries },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(Screen.AiWorkbench.route) {
                                AIScanScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.ManageUsers.route) {
                                val sessionManager = SessionManager(context)
                                if (sessionManager.isCorporateAdmin()) {
                                    UserManagementScreen(
                                        onNavigateBack = { coroutineScope.launch { drawerState.open() } }
                                    )
                                } else {
                                    Toast.makeText(context, "Access Restricted: Requires Corporate HQ Admin privileges.", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                            composable(Screen.DataImport.route) {
                                val sessionManager = SessionManager(context)
                                if (sessionManager.isCorporateAdmin()) {
                                    DataImportScreen(
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } else {
                                    Toast.makeText(context, "Access Restricted: Requires Corporate HQ Admin privileges.", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                            composable(Screen.AuditLog.route) {
                                val sessionManager = SessionManager(context)
                                if (!sessionManager.isMineOfficial()) {
                                    AuditLogScreen(
                                        auditEntries = dashboardState.auditEntries.ifEmpty { sampleAuditEntries },
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } else {
                                    Toast.makeText(context, "Access Restricted: Requires DGMS Regulator or Corporate HQ Admin privileges.", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                            composable(Screen.Profile.route) {
                                ProfileScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onLogout = {
                                        coroutineScope.launch {
                                            try {
                                                SupabaseClientInstance.client.auth.signOut()
                                            } catch (e: Exception) {
                                            } finally {
                                                navController.navigate("login") {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                            composable(Screen.HelpSupport.route) {
                                HelpSupportScreen(
                                    onNavigateBack = { coroutineScope.launch { drawerState.open() } }
                                )
                            }
                            composable(Screen.Capture.route) {
                                CaptureScreen(
                                    onCaptured = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        if (showNotificationSheet) {
                            NotificationCenterSheet(
                                onDismiss = { showNotificationSheet = false },
                                onNavigateToRoute = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        if (showGlobalSearchDialog) {
                            GlobalSearchDialog(
                                onDismiss = { showGlobalSearchDialog = false },
                                onNavigateToRoute = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::networkSyncCallback.isInitialized) {
            networkSyncCallback.unregister()
        }
    }
}

@Composable
fun DrawerItemCustom(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (selected) Color(0xFFFEF3C7) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = if (selected) BorderStroke(1.dp, Color(0xFFFDE68A)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFFD97706) else Color(0xFF475569),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selected) Color(0xFF92400E) else Color(0xFF334155)
            )
        }
    }
}
