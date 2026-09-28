package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "statutory_registers")
data class StatutoryRegister(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: Int,
    @ColumnInfo(name = "register_type")
    @SerialName("register_type")
    val registerType: String,
    @SerialName("shift")
    val shift: String,
    @ColumnInfo(name = "seam_or_pit")
    @SerialName("seam_or_pit")
    val seamOrPit: String,
    @ColumnInfo(name = "inspector_name")
    @SerialName("inspector_name")
    val inspectorName: String,
    @ColumnInfo(name = "inspector_role")
    @SerialName("inspector_role")
    val inspectorRole: String,
    @SerialName("parameters")
    val parameters: String = "{}",
    @ColumnInfo(name = "compliance_status")
    @SerialName("compliance_status")
    val complianceStatus: String = "COMPLIANT",
    @ColumnInfo(name = "statutory_regulation")
    @SerialName("statutory_regulation")
    val statutoryRegulation: String,
    @SerialName("remarks")
    val remarks: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("hash")
    val hash: String? = null,
    @ColumnInfo(name = "prev_hash")
    @SerialName("prev_hash")
    val prevHash: String? = null,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null
)
