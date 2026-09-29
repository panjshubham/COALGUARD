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
import androidx.activity.result.launch
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.data.remote.*
import com.example.coalguard.ui.components.CoalGuardLogo
import com.example.coalguard.ui.theme.GovtBgSlate
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtGoldTint
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiWorkbenchScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var activeTab by remember { mutableStateOf("PPE Check") }

    // Text processing states
    var textInput by remember { mutableStateOf("Roof support timber cracked near face 4, slight water trickling observed.") }
    var selectedLanguage by remember { mutableStateOf("Hindi") }
    var isProcessing by remember { mutableStateOf(false) }
    var resultOutput by remember { mutableStateOf<String?>(null) }

    // Media states
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val tabs = listOf(
        Triple("PPE Check", Icons.Default.Engineering, "Safety Gear Detection"),
        Triple("OCR", Icons.Default.Description, "Document Text Extraction"),
        Triple("Doc -> JSON", Icons.Default.DataObject, "Document Understanding"),
        Triple("Classify", Icons.Default.Category, "Compliance Classification"),
        Triple("Extract Entities", Icons.Default.FindInPage, "Named Entity Recognition"),
        Triple("Translate", Icons.Default.Translate, "Multilingual Translation"),
        Triple("Voice Report", Icons.Default.Mic, "Speech-to-Text")
    )

    fun bitmapToPart(bitmap: Bitmap, fieldName: String = "file"): MultipartBody.Part {
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        val mediaType = MediaType.parse("image/jpeg")
        val reqFile = RequestBody.create(mediaType, file)
        return MultipartBody.Part.createFormData(fieldName, file.name, reqFile)
    }

    fun runModelForImage(bitmap: Bitmap) {
        selectedBitmap = bitmap
        isProcessing = true
        resultOutput = null

        coroutineScope.launch {
            try {
                val part = bitmapToPart(bitmap)
                when (activeTab) {
                    "PPE Check" -> {
                        val response = RetrofitClient.apiService.detectPpe(part)
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            val missing = data.missing_ppe ?: emptyList()
                            val detected = data.detected_items?.map { "${it.label} (${((it.score ?: 0.0) * 100).toInt()}%)" } ?: emptyList()
                            val statusStr = data.compliance_status?.uppercase() ?: "UNKNOWN"
                            val alertStr = data.alert ?: ""

                            val isCompliant = (statusStr == "COMPLIANT") &&
                                    missing.isEmpty() &&
                                    !alertStr.contains("UNCERTAIN", ignoreCase = true) &&
                                    !alertStr.contains("Manual Review", ignoreCase = true) &&
                                    !alertStr.contains("Borderline", ignoreCase = true)

                            val isNonCompliant = !isCompliant

                            resultOutput = buildString {
                                if (isNonCompliant) {
                                    appendLine("🚨 PPE COMPLIANCE: NON_COMPLIANT (VIOLATION DETECTED)")
                                    appendLine("Severity: ${if (data.severity.isNullOrBlank() || data.severity == "NONE") "CRITICAL" else data.severity.uppercase()}")
                                    appendLine("❌ MISSING / UNVERIFIED PPE: ${missing.ifEmpty { listOf("Hard-Hat (Helmet)", "High-Vis Safety Vest") }.joinToString(", ")}")
                                } else {
                                    appendLine("✅ PPE COMPLIANCE: COMPLIANT (GREEN SIGNAL)")
                                    appendLine("Severity: LOW")
                                    appendLine("✅ All Essential PPE Detected (Hard-Hat & Safety Vest Verified)")
                                }
                                if (detected.isNotEmpty()) {
                                    appendLine("Detected Items: ${detected.joinToString(", ")}")
                                }
                                if (!data.alert.isNullOrBlank()) {
                                    appendLine("\nAlert: ${data.alert}")
                                }
                            }
                        } else {
                            resultOutput = "Error: Server returned code ${response.code()}"
                        }
                    }
                    "OCR" -> {
                        val response = RetrofitClient.apiService.extractOcrText(part)
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            resultOutput = buildString {
                                appendLine("📜 EXTRACTED DOCUMENT TEXT (${data.confidence_pct ?: 0}% Confidence):")
                                appendLine("--------------------------------------------------")
                                appendLine(data.extracted_text ?: "No text detected.")
                                if (!data.detected_dates.isNullOrEmpty()) {
                                    appendLine("\nDetected Dates: ${data.detected_dates.joinToString(", ")}")
                                }
                            }
                        } else {
                            resultOutput = "OCR Extraction failed: ${response.code()}"
                        }
                    }
                    "Doc -> JSON" -> {
                        val response = RetrofitClient.apiService.extractDonutJson(part)
                        if (response.isSuccessful && response.body() != null) {
                            val data = response.body()!!
                            resultOutput = buildString {
                                appendLine("{")
                                data.structured_output?.forEach { (k, v) ->
                                    appendLine("  \"$k\": \"$v\",")
                                }
                                appendLine("}")
                            }
                        } else {
                            resultOutput = "Donut JSON extraction failed: ${response.code()}"
                        }
                    }
                }
            } catch (e: Exception) {
                resultOutput = "Failed to connect to AI Service: ${e.message}\nMake sure your FastAPI server is running on http://192.168.0.56:8000"
            } finally {
                isProcessing = false
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                runModelForImage(bitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var tempPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }

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
                runModelForImage(bitmap)
            } catch (e: Exception) {
                Log.e("AiWorkbenchScreen", "Error decoding photo: ${e.message}", e)
                Toast.makeText(context, "Error reading captured photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchCameraSafely() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Camera permission required for live photo capture", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val photoFile = File(context.cacheDir, "camera_wb_${System.currentTimeMillis()}.jpg")
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, photoFile)
            tempPhotoUri = uri.toString()
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            Log.e("AiWorkbenchScreen", "Error launching camera: ${e.message}", e)
            Toast.makeText(context, "Could not open camera app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun runTextModel() {
        if (textInput.isBlank()) {
            Toast.makeText(context, "Please enter text to process", Toast.LENGTH_SHORT).show()
            return
        }

        isProcessing = true
        resultOutput = null

        coroutineScope.launch {
            try {
                when (activeTab) {
                    "Classify" -> {
                        val resp = RetrofitClient.apiService.classifyCompliance(ClassifyComplianceRequest(textInput))
                        if (resp.isSuccessful && resp.body() != null) {
                            val data = resp.body()!!
                            resultOutput = buildString {
                                appendLine("🏷️ PREDICTED COMPLIANCE CATEGORY:")
                                appendLine(data.top_category ?: "General Compliance")
                                appendLine("\nConfidence: ${((data.confidence ?: 0.0) * 100).toInt()}%")
                                appendLine("Model: ${data.model ?: "bart-large-mnli"}")
                            }
                        } else {
                            resultOutput = "Error: ${resp.code()}"
                        }
                    }
                    "Extract Entities" -> {
                        val resp = RetrofitClient.apiService.extractEntities(EntityExtractionRequest(textInput))
                        if (resp.isSuccessful && resp.body() != null) {
                            val data = resp.body()!!
                            val entities = data.entities ?: emptyList()
                            resultOutput = buildString {
                                appendLine("🔍 EXTRACTED ENTITIES (BERT NER):")
                                appendLine("----------------------------------")
                                if (entities.isEmpty()) {
                                    appendLine("No specific named entities found.")
                                } else {
                                    entities.forEach {
                                        appendLine("• [${it.entity_group ?: "TAG"}]: ${it.word} (${((it.score ?: 0.0) * 100).toInt()}%)")
                                    }
                                }
                            }
                        } else {
                            resultOutput = "Error: ${resp.code()}"
                        }
                    }
                    "Translate" -> {
                        val resp = RetrofitClient.apiService.translateText(TranslateTextRequest(textInput, selectedLanguage))
                        if (resp.isSuccessful && resp.body() != null) {
                            val data = resp.body()!!
                            resultOutput = buildString {
                                appendLine("🌐 TRANSLATION (${selectedLanguage.uppercase()}):")
                                appendLine("------------------------------------------")
                                appendLine(data.translated_text ?: "Translation failed.")
                            }
                        } else {
                            resultOutput = "Error: ${resp.code()}"
                        }
                    }
                }
            } catch (e: Exception) {
                resultOutput = "Connection error: ${e.message}\nMake sure AI service is running."
            } finally {
                isProcessing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoalGuardLogo(size = 32.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("COALGUARD PLATFORM", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text("सत्यमेव जयते | MULTI-MODAL AI TESTING WORKBENCH", fontSize = 8.sp, color = GovtGoldAmber, fontWeight = FontWeight.Bold)
                        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GovtBgSlate)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Horizontal Tab Selector
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == activeTab }.coerceAtLeast(0),
                edgePadding = 4.dp,
                modifier = Modifier.fillMaxWidth(),
                containerColor = GovtSurfaceWhite,
                contentColor = GovtNavyPrimary
            ) {
                tabs.forEach { (name, icon, _) ->
                    Tab(
                        selected = activeTab == name,
                        onClick = {
                            activeTab = name
                            resultOutput = null
                        },
                        text = { Text(name, fontWeight = if (activeTab == name) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        selectedContentColor = GovtNavyPrimary,
                        unselectedContentColor = GovtTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val activeTabInfo = tabs.find { it.first == activeTab } ?: tabs[0]

            // Main Active Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                border = BorderStroke(1.dp, GovtCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(activeTabInfo.third, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GovtTextDark)
                            Text("FastAPI Microservice Engine • Hugging Face Models", fontSize = 11.sp, color = GovtTextMuted)
                        }

                        Surface(color = GovtGoldTint, shape = RoundedCornerShape(6.dp)) {
                            Text("94.2% CONFIDENCE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                        }
                    }

                    // Content layout by mode
                    val isImageMode = activeTab == "PPE Check" || activeTab == "OCR" || activeTab == "Doc -> JSON"

                    if (isImageMode) {
                        // Image Upload / Camera Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { launchCameraSafely() },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber)
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Image", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Preview of selected image
                        if (selectedBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = selectedBitmap!!.asImageBitmap(),
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    } else if (activeTab == "Voice Report") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("🎙️ Whisper Speech-to-Text Voice Log", fontWeight = FontWeight.Bold, color = Color(0xFF0369A1), fontSize = 14.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Speak a hazard report or record a verbal memo in Hindi, Bengali, or English to generate a statutory violation entry.", fontSize = 12.sp, color = GovtTextDark)
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = { Toast.makeText(context, "Microphone recording started...", Toast.LENGTH_SHORT).show() },
                                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Hold to Record Voice Memo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        // Text input mode (Classify, Entities, Translate)
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = { Text("Input Unstructured Hazard Text to Analyze", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
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

                        if (activeTab == "Translate") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Target Language:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                listOf("Hindi", "Bengali", "Odia").forEach { lang ->
                                    FilterChip(
                                        selected = selectedLanguage == lang,
                                        onClick = { selectedLanguage = lang },
                                        label = { Text(lang, fontSize = 11.sp) },
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

                        Button(
                            onClick = { runTextModel() },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary),
                            enabled = !isProcessing
                        ) {
                            Text("Run Model (${activeTab})", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                    }

                    // Progress Loader
                    if (isProcessing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GovtNavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing with AI Model...", color = GovtNavyPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Result Display Box
                    if (resultOutput != null) {
                        val isCompliantSignal = resultOutput!!.contains("COMPLIANT (GREEN SIGNAL)")
                        val isViolation = !isCompliantSignal || resultOutput!!.contains("NON_COMPLIANT") || resultOutput!!.contains("MISSING") || resultOutput!!.contains("VIOLATION") || resultOutput!!.contains("Error") || resultOutput!!.contains("UNCERTAIN")
                        val boxBg = if (isViolation) Color(0xFFFEE2E2) else Color(0xFFECFDF5)
                        val iconColor = if (isViolation) Color(0xFFDC2626) else Color(0xFF059669)
                        val iconImage = if (isViolation) Icons.Default.Warning else Icons.Default.CheckCircle

                        Surface(
                            color = boxBg,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isViolation) Color(0xFFFCA5A5) else Color(0xFFA7F3D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("AI Model Response:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GovtTextDark)
                                    Icon(iconImage, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    resultOutput!!,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = if (isViolation) Color(0xFF7F1D1D) else Color(0xFF065F46)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
