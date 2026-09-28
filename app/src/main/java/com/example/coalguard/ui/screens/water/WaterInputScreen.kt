package com.example.coalguard.ui.screens.water

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coalguard.ui.theme.GovtCardBorder
import com.example.coalguard.ui.theme.GovtGoldAmber
import com.example.coalguard.ui.theme.GovtNavyPrimary
import com.example.coalguard.ui.theme.GovtSurfaceWhite
import com.example.coalguard.ui.theme.GovtTextDark
import com.example.coalguard.ui.theme.GovtTextMuted
import com.example.coalguard.ui.viewmodel.WaterInrushViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun WaterInputScreen(
    viewModel: WaterInrushViewModel,
    onNavigateToResult: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, GovtCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = GovtNavyPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MINE WATER INRUSH & AQUIFER AI ANALYZER", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GovtTextDark)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Input 8 hydrochemical ions to identify breaching aquifer source (G1 Limestone Karst, G2 Tai-grey, G3 Coal Sandstone).",
                    style = MaterialTheme.typography.bodySmall,
                    color = GovtTextMuted
                )
            }
        }

        // Benchmark Presets
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, GovtCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GovtGoldAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("STANDARD BENCHMARK PRESETS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtGoldAmber)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.loadPreset("G1") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("G1 Karst", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.loadPreset("G2") },
                        colors = ButtonDefaults.buttonColors(containerColor = GovtGoldAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("G2 Tai-grey", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.loadPreset("G3") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("G3 Sandstone", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Chemical Field Inputs Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GovtSurfaceWhite),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, GovtCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("HYDROCHEMICAL IONS (mg/L)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GovtTextDark)

                ChemicalField("Ca²⁺ (Calcium)", viewModel.ca)
                ChemicalField("Mg²⁺ (Magnesium)", viewModel.mg)
                ChemicalField("K⁺ + Na⁺ (Alkali Metals)", viewModel.kNa)
                ChemicalField("HCO₃⁻ (Bicarbonate)", viewModel.hco3)
                ChemicalField("Cl⁻ (Chloride)", viewModel.cl)
                ChemicalField("SO₄²⁻ (Sulfate)", viewModel.so4)
                ChemicalField("Hardness (mg/L)", viewModel.hardness)
                ChemicalField("pH Value", viewModel.ph)
            }
        }

        Button(
            onClick = {
                viewModel.submitAnalysis("Face 4, Gallery B")
                onNavigateToResult()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GovtNavyPrimary)
        ) {
            Icon(Icons.Default.Science, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("ANALYZE INRUSH SOURCE (AI)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}

@Composable
fun ChemicalField(label: String, flow: MutableStateFlow<String>) {
    val textVal by flow.collectAsState()
    OutlinedTextField(
        value = textVal,
        onValueChange = { flow.value = it },
        label = { Text(label, fontSize = 12.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
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
}
