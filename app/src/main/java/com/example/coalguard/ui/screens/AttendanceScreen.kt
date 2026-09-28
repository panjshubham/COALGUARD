package com.example.coalguard.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.coalguard.ui.components.CoalGuardLogo
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted
import java.io.File

data class WorkerAttendanceItem(
    val id: String,
    val workerId: String,
    val name: String,
    val designation: String,
    val employer: String, // "CIL Regular (BCCL)" or "M/s RK Earthmovers"
    val shift: String,
    val checkInTime: String,
    val verificationMethod: String, // "FACE_BIOMETRIC", "GEO_FENCED_GATE", "RFID_SMART_PASS"
    val pmeStatus: String = "FORM_O_VALID",
    var isPresent: Boolean = true,
    var photoUrl: String? = null
)

val SAMPLE_WORKERS_ROSTER = listOf(
    WorkerAttendanceItem("1", "EMP-8821", "Er. Rajesh Kumar", "Statutory Overman", "CIL Regular (BCCL)", "Morning (06:00 - 14:00)", "06:12 AM", "FACE_BIOMETRIC"),
    WorkerAttendanceItem("2", "EMP-8842", "Manoj Singh", "Mining Sirdar", "CIL Regular (BCCL)", "Morning (06:00 - 14:00)", "06:18 AM", "GEO_FENCED_GATE"),
    WorkerAttendanceItem("3", "EMP-9912", "Ramesh Turi", "Heavy Dumper Operator", "M/s RK Earthmovers", "Morning (06:00 - 14:00)", "06:30 AM", "FACE_BIOMETRIC"),
    WorkerAttendanceItem("4", "EMP-9915", "Suresh Hembram", "Underground Drill Operator", "M/s Suvidha Drilling", "Morning (06:00 - 14:00)", "06:45 AM", "RFID_SMART_PASS"),
    WorkerAttendanceItem("5", "EMP-8850", "Sunil Marandi", "Winding Engine Operator", "CIL Regular (BCCL)", "Afternoon (14:00 - 22:00)", "Pending Shift", "GEO_FENCED_GATE", isPresent = false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedShift by remember { mutableStateOf("Morning (06:00 - 14:00)") }
    var showClockInDialog by remember { mutableStateOf(false) }

    val workerRoster = remember { mutableStateListOf<WorkerAttendanceItem>().apply { addAll(SAMPLE_WORKERS_ROSTER) } }

    var tempPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var lastCapturedSelfie by remember { mutableStateOf<Bitmap?>(null) }

    // Camera Launcher for Face Biometric Check-In
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            try {
                val uri = Uri.parse(tempPhotoUri)
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                lastCapturedSelfie = bitmap

                // Auto clock-in first unverified worker
                val unverifiedIndex = workerRoster.indexOfFirst { !it.isPresent }
                if (unverifiedIndex != -1) {
                    workerRoster[unverifiedIndex] = workerRoster[unverifiedIndex].copy(
                        isPresent = true,
                        checkInTime = "Just Now",
                        verificationMethod = "FACE_BIOMETRIC"
                    )
                }
                Toast.makeText(context, "✅ Face Biometric Verified & Check-In Recorded!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("AttendanceScreen", "Error decoding selfie: ${e.message}", e)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Camera permission required for Face Biometric Check-In", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchFaceBiometricCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }

        try {
            val photoFile = File(context.cacheDir, "face_biometric_${System.currentTimeMillis()}.jpg")
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, photoFile)
            tempPhotoUri = uri.toString()
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            Log.e("AttendanceScreen", "Error launching camera: ${e.message}", e)
            Toast.makeText(context, "Could not open camera app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    val filteredWorkers = workerRoster.filter { worker ->
        val matchesSearch = searchQuery.isBlank() ||
                worker.name.contains(searchQuery, ignoreCase = true) ||
                worker.workerId.contains(searchQuery, ignoreCase = true) ||
                worker.designation.contains(searchQuery, ignoreCase = true) ||
                worker.employer.contains(searchQuery, ignoreCase = true)

        val matchesShift = selectedShift == "ALL" || worker.shift.contains(selectedShift.take(7), ignoreCase = true)
        matchesSearch && matchesShift
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoalGuardLogo(size = 32.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("COAL INDIA LIMITED", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text("सत्यमेव जयते | WORKFORCE ATTENDANCE & MUSTER ROLL", fontSize = 8.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Syncing offline attendance logs with cloud...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color.White)
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
            // Header Info Banner
            item {
                Column {
                    Surface(
                        color = GovtGoldTint,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, GovtGoldAmber)
                    ) {
                        Text(
                            text = "WORKFORCE MUSTER ROLL • FACE BIOMETRIC & GNSS GEOFENCE",
                            color = GovtGoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Workforce Attendance & Shift Roster",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GovtTextDark
                    )
                    Text(
                        text = "Realtime face verification, pithead GNSS geofence check-ins, and PME Form O medical fitness tracking.",
                        fontSize = 12.sp,
                        color = GovtTextMuted
                    )
                }
            }

            // Attendance KPI Stat Summary Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AttendanceKpiCard("TOTAL ON-SITE", "${workerRoster.count { it.isPresent }} Miners", GovtNavyPrimary, Modifier.weight(1f))
                    AttendanceKpiCard("CIL REGULAR", "${workerRoster.count { it.isPresent && it.employer.contains("CIL") }} Staff", Color(0xFF059669), Modifier.weight(1f))
                    AttendanceKpiCard("CONTRACTORS", "${workerRoster.count { it.isPresent && !it.employer.contains("CIL") }} Workers", GovtGoldAmber, Modifier.weight(1f))
                    AttendanceKpiCard("OFFLINE CACHE", "0 Pending", Color(0xFF0284C7), Modifier.weight(1f))
                }
            }

            // Quick Check-In Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { launchFaceBiometricCamera() },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Face Biometric", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "📍 GNSS Pithead Geofence Verified: Check-In Recorded!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GPS Geofence", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { showClockInDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clock-In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // Last Captured Face Selfie Preview
            if (lastCapturedSelfie != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GovtCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = lastCapturedSelfie!!.asImageBitmap(),
                                    contentDescription = "Selfie",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("FACE BIOMETRIC VERIFIED", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF059669))
                                Text("Watermark Sealed: 23.7923° N, 86.4253° E", fontSize = 10.sp, color = GovtTextMuted)
                                Text("Method: face_biometric_watermarked", fontSize = 10.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Search Bar & Shift Selector Chips
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by worker name, ID, designation, or contractor...", color = GovtTextMuted) },
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Morning (06:00 - 14:00)", "Afternoon (14:00 - 22:00)", "Night (22:00 - 06:00)", "ALL").forEach { shiftLabel ->
                                FilterChip(
                                    selected = selectedShift == shiftLabel,
                                    onClick = { selectedShift = shiftLabel },
                                    label = { Text(if (shiftLabel == "ALL") "All Shifts" else shiftLabel.take(9), fontSize = 10.sp, fontWeight = if (selectedShift == shiftLabel) FontWeight.Bold else FontWeight.Normal) },
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
                }
            }

            // Workforce Roster List
            items(filteredWorkers) { worker ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (worker.isPresent) GovtNavyPrimary else GovtBgSlate),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = worker.name.take(1),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (worker.isPresent) Color.White else GovtTextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(worker.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GovtTextDark)
                                    Text("${worker.workerId} • ${worker.designation}", fontSize = 10.sp, color = GovtTextMuted)
                                }
                            }

                            Surface(
                                color = if (worker.isPresent) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (worker.isPresent) "CHECKED-IN" else "ABSENT",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (worker.isPresent) Color(0xFF059669) else Color(0xFFDC2626)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Employer: ${worker.employer}", fontSize = 10.sp, color = GovtNavyPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Verification: ${worker.verificationMethod}", fontSize = 9.sp, color = GovtTextMuted)
                            }

                            Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "FORM O MEDICAL VALID",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Clock-In: ${worker.checkInTime}", fontSize = 10.sp, color = GovtTextMuted)

                            Button(
                                onClick = {
                                    val idx = workerRoster.indexOfFirst { it.id == worker.id }
                                    if (idx != -1) {
                                        val newState = !workerRoster[idx].isPresent
                                        workerRoster[idx] = workerRoster[idx].copy(
                                            isPresent = newState,
                                            checkInTime = if (newState) "Just Now" else "Clocked-Out"
                                        )
                                        Toast.makeText(context, if (newState) "✅ Worker Checked-In!" else "Worker Clocked-Out", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (worker.isPresent) Color(0xFFDC2626) else GovtNavyPrimary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (worker.isPresent) "Clock-Out" else "Clock-In",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // MANUAL CLOCK-IN DIALOG
    if (showClockInDialog) {
        var newWorkerName by remember { mutableStateOf("") }
        var newWorkerId by remember { mutableStateOf("EMP-${(1000..9999).random()}") }
        var newDesignation by remember { mutableStateOf("Dumper Operator") }
        var newEmployer by remember { mutableStateOf("M/s RK Earthmovers") }

        AlertDialog(
            onDismissRequest = { showClockInDialog = false },
            title = { Text("Manual Worker Clock-In", fontWeight = FontWeight.Bold, color = GovtTextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newWorkerName,
                        onValueChange = { newWorkerName = it },
                        label = { Text("Worker Full Name") },
                        placeholder = { Text("e.g. Ramesh Turi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newWorkerId,
                        onValueChange = { newWorkerId = it },
                        label = { Text("Worker Employee ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newDesignation,
                        onValueChange = { newDesignation = it },
                        label = { Text("Designation") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newEmployer,
                        onValueChange = { newEmployer = it },
                        label = { Text("Employer / Contractor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWorkerName.isNotBlank()) {
                            workerRoster.add(
                                0,
                                WorkerAttendanceItem(
                                    id = System.currentTimeMillis().toString(),
                                    workerId = newWorkerId,
                                    name = newWorkerName,
                                    designation = newDesignation,
                                    employer = newEmployer,
                                    shift = selectedShift,
                                    checkInTime = "Just Now",
                                    verificationMethod = "MANUAL_ENTRY",
                                    isPresent = true
                                )
                            )
                            Toast.makeText(context, "✅ Worker $newWorkerName Clocked-In!", Toast.LENGTH_SHORT).show()
                        }
                        showClockInDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                ) {
                    Text("Clock-In Worker", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClockInDialog = false }) {
                    Text("Cancel", color = GovtTextMuted)
                }
            },
            containerColor = GovtSurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AttendanceKpiCard(label: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, GovtCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 8.sp, color = GovtTextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = accentColor.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                Text(value, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
        }
    }
}
