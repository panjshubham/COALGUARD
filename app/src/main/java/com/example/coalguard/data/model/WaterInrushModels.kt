package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 1. Request to AI service
@Serializable
data class WaterSampleRequest(
    @SerialName("ca") val ca: Double,       // Ca2+ (mg/L)
    @SerialName("mg") val mg: Double,       // Mg2+ (mg/L)
    @SerializedName("k_na") @SerialName("k_na") val kNa: Double, // K+ + Na+ (mg/L)
    @SerialName("hco3") val hco3: Double,     // HCO3- (mg/L)
    @SerialName("cl") val cl: Double,       // Cl- (mg/L)
    @SerialName("so4") val so4: Double,      // SO42- (mg/L)
    @SerialName("hardness") val hardness: Double, // Hardness (mg/L)
    @SerialName("ph") val ph: Double        // pH
)

// 2. SHAP Feature Attribution
@Serializable
data class KeyFeature(
    @SerialName("feature") val feature: String,
    @SerialName("shap") val shap: Double,
    @SerialName("value") val value: Double,
    @SerialName("direction") val direction: String // "positive" or "negative"
)

// 3. Prediction Response
@Serializable
data class WaterInrushResponse(
    @SerialName("status") val status: String,
    @SerializedName("predicted_class") @SerialName("predicted_class") val predictedClass: String,
    @SerializedName("predicted_class_short") @SerialName("predicted_class_short") val predictedClassShort: String, // "G1", "G2", "G3"
    @SerializedName("class_index") @SerialName("class_index") val classIndex: Int,
    @SerialName("confidence") val confidence: Double,
    @SerialName("probabilities") val probabilities: Map<String, Double> = emptyMap(),
    @SerializedName("shap_baseline") @SerialName("shap_baseline") val shapBaseline: Double = 0.0,
    @SerializedName("key_features") @SerialName("key_features") val keyFeatures: List<KeyFeature> = emptyList()
)

// 4. Offline Test Record Entity for Room Database
@Serializable
@Entity(tableName = "water_test_records")
data class WaterTestRecord(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id") val id: Long = 0,
    @ColumnInfo(name = "timestamp")
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "mine_section")
    @SerialName("mine_section") val mineSection: String,
    @ColumnInfo(name = "ca") val ca: Double,
    @ColumnInfo(name = "mg") val mg: Double,
    @ColumnInfo(name = "k_na") val kNa: Double,
    @ColumnInfo(name = "hco3") val hco3: Double,
    @ColumnInfo(name = "cl") val cl: Double,
    @ColumnInfo(name = "so4") val so4: Double,
    @ColumnInfo(name = "hardness") val hardness: Double,
    @ColumnInfo(name = "ph") val ph: Double,
    @ColumnInfo(name = "predicted_class_short") val predictedClassShort: String?,
    @ColumnInfo(name = "confidence") val confidence: Double?,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false
)
