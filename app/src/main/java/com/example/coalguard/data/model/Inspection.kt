package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "inspections")
data class Inspection(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: Int? = 42,
    @ColumnInfo(name = "contractor_id")
    @SerialName("contractor_id")
    val contractorId: Int? = 9,
    @SerialName("date")
    val date: String = "2026-09-17T12:00:00.000Z",
    @ColumnInfo(name = "inspector_name")
    @SerialName("inspector_name")
    val inspectorName: String = "Field Inspector",
    @ColumnInfo(name = "synced_at")
    @SerialName("synced_at")
    val syncedAt: String? = null,
    @ColumnInfo(name = "tracking_id")
    @SerialName("tracking_id")
    val trackingId: String? = null,
    @ColumnInfo(name = "type")
    @Transient
    val type: String? = "Statutory Safety Audit",
    @ColumnInfo(name = "scheduled_date")
    @Transient
    val scheduledDate: String? = "2026-09-17",
    @ColumnInfo(name = "status")
    @Transient
    val status: String? = "completed"
)
