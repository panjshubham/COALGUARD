package com.example.coalguard.ui.screens.water

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted
import com.example.coalguard.ui.viewmodel.WaterUiState
import com.example.coalguard.ui.viewmodel.WaterInrushViewModel

@Composable
fun WaterResultScreen(viewModel: WaterInrushViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    when (val s = state) {
        is WaterUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GovtNavyPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Executing XGBoost & TreeSHAP Analysis...", style = MaterialTheme.typography.bodyMedium, color = GovtTextMuted)
                }
            }
        }
        is WaterUiState.Error -> {
            Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFFCA5A5))) {
                    Text(text = s.message, color = Color(0xFFDC2626), modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
        is WaterUiState.Success -> {
            val res = s.response
            val isEmergency = res.predictedClassShort == "G1"
            val bannerColor = when (res.predictedClassShort) {
                "G1" -> Color(0xFFDC2626) // High Risk Red
                "G2" -> GovtGoldAmber     // Medium Risk Amber
                else -> Color(0xFF059669) // Low Risk Green
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Risk Header Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = bannerColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEmergency) "⚠️ CRITICAL INRUSH HAZARD" else "DIAGNOSTIC COMPLETE",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = res.predictedClass,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "AI Confidence: ${res.confidence}%",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Probability Distribution Bars
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AQUIFER PROBABILITY DISTRIBUTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        res.probabilities.forEach { (cls, prob) ->
                            Column {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(cls, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                                    Text("${(prob * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GovtNavyPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { prob.toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = if (cls.contains("G1")) Color(0xFFDC2626) else GovtNavyPrimary,
                                    trackColor = Color(0xFFF1F5F9)
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }

                // Action Protocol
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("MANDATORY SAFETY ACTION PROTOCOL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = when (res.predictedClassShort) {
                                "G1" -> "1. Evacuate Lower Face 4 immediately.\n2. Engage main sumps & high-head pumps.\n3. Inform DGMS Zone Controller & Colliery Manager."
                                "G2" -> "1. Inspect limestone fracture zone.\n2. Verify intercept drainage pumps and water barrier."
                                else -> "1. Normal roof drainage protocol.\n2. Log shift discharge volume in Statutory Book."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = GovtTextDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // SHAP Waterfall Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GovtCardBorder)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("TREESHAP FEATURE ATTRIBUTION (WHY?)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)
                        Spacer(modifier = Modifier.height(10.dp))

                        res.keyFeatures.forEach { feat ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(feat.feature, fontSize = 12.sp, color = GovtTextDark)
                                Text(
                                    text = if (feat.shap > 0) "+${feat.shap}" else "${feat.shap}",
                                    color = if (feat.shap > 0) Color(0xFF059669) else Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        }
        WaterUiState.Idle -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Select inputs and tap 'Analyze Inrush Source' to view results.", color = GovtTextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
