package com.example.coalguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "risk_scores")
data class RiskScoreEntity(
    @PrimaryKey
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: String,
    @SerialName("score")
    val score: Double?,
    @ColumnInfo(name = "contributing_factors")
    @SerialName("contributing_factors")
    val contributingFactors: String?
)
