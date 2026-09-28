package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "risk_scores")
data class RiskScore(
    @PrimaryKey
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: Int,
    @SerialName("score")
    val score: Double,
    @ColumnInfo(name = "risk_level")
    @SerialName("risk_level")
    val riskLevel: String,
    @ColumnInfo(name = "last_updated")
    @SerialName("last_updated")
    val lastUpdated: String? = null,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("explanation")
    val explanation: String? = null,
    @ColumnInfo(name = "contributing_factors")
    @SerialName("contributing_factors")
    val contributingFactors: String? = null
)
