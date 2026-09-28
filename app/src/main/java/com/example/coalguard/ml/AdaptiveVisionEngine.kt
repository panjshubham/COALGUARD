package com.example.coalguard.ml

import android.graphics.Bitmap

data class AdaptiveDetectionConfig(
    val confidenceThreshold: Float,
    val isLowLighting: Boolean,
    val mappedRegulationRef: String,
    val autoSeverity: String
)

class AdaptiveVisionEngine {

    fun analyzeLuminanceAndAdjustThreshold(bitmap: Bitmap, detectionLabel: String): AdaptiveDetectionConfig {
        val width = bitmap.width
        val height = bitmap.height
        var totalLuminance = 0.0

        val step = 10
        var count = 0
        for (x in 0 until width step step) {
            for (y in 0 until height step step) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val luminance = 0.299 * r + 0.587 * g + 0.114 * b
                totalLuminance += luminance
                count++
            }
        }

        val avgLuminance = if (count > 0) totalLuminance / count else 128.0
        val isLowLighting = avgLuminance < 80.0

        val threshold = if (isLowLighting) 0.40f else 0.60f

        val (regRef, severity) = when (detectionLabel.lowercase()) {
            "no_helmet" -> Pair("CMR 2017 Reg 115", "critical")
            "no_vest" -> Pair("CMR 2017 Reg 115", "high")
            "methane_leak" -> Pair("CMR 2017 Reg 155", "critical")
            "haul_road_defect" -> Pair("CMR 2017 Reg 83", "high")
            else -> Pair("DGMS Statutory Guidance", "medium")
        }

        return AdaptiveDetectionConfig(
            confidenceThreshold = threshold,
            isLowLighting = isLowLighting,
            mappedRegulationRef = regRef,
            autoSeverity = severity
        )
    }
}
