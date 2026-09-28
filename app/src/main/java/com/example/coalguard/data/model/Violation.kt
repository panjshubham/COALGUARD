package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "violations")
data class Violation(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: Int? = 42,
    @ColumnInfo(name = "inspection_id")
    @SerialName("inspection_id")
    val inspectionId: Int? = null,
    @ColumnInfo(name = "regulation_ref")
    @SerialName("regulation_ref")
    val regulationRef: String = "CMR 2017 Reg 115",
    @SerialName("description")
    val description: String,
    @SerialName("status")
    val status: String = "open",
    @SerialName("severity")
    val severity: String = "medium",
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "escalated_at")
    @SerialName("escalated_at")
    val escalatedAt: String? = null,
    @ColumnInfo(name = "photo_url")
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @ColumnInfo(name = "corrective_action")
    @SerialName("corrective_action")
    val correctiveAction: String? = null,
    @ColumnInfo(name = "closed_at")
    @SerialName("closed_at")
    val closedAt: String? = null,
    @ColumnInfo(name = "approved_by")
    @SerialName("approved_by")
    val approvedBy: String? = null,
    @ColumnInfo(name = "approved_at")
    @SerialName("approved_at")
    val approvedAt: String? = null,
    @SerialName("category")
    val category: String? = null,
    @ColumnInfo(name = "tracking_id")
    @SerialName("tracking_id")
    val trackingId: String? = null,
    @ColumnInfo(name = "data_hash")
    @Transient
    val dataHash: String? = null,
    @ColumnInfo(name = "prev_hash")
    @Transient
    val prevHash: String? = null
) {
    val title: String get() = category?.replace('_', ' ')?.uppercase() ?: "Violation #$id"
}
