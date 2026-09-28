package com.example.coalguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "contractor_incidents")
data class ContractorIncidentEntity(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @ColumnInfo(name = "contractor_id")
    @SerialName("contractor_id")
    val contractorId: Int,
    @ColumnInfo(name = "violation_id")
    @SerialName("violation_id")
    val violationId: Int? = null,
    @SerialName("severity")
    val severity: String,
    @SerialName("date")
    val date: String,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null
)
