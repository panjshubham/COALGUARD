package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "compliance_items")
data class ComplianceItem(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: Int,
    @SerialName("category")
    val category: String,
    @SerialName("title")
    val title: String,
    @ColumnInfo(name = "due_date")
    @SerialName("due_date")
    val dueDate: String,
    @SerialName("status")
    val status: String = "pending",
    @ColumnInfo(name = "assigned_to")
    @SerialName("assigned_to")
    val assignedTo: String? = null,
    @ColumnInfo(name = "document_url")
    @SerialName("document_url")
    val documentUrl: String? = null,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "tracking_id")
    @SerialName("tracking_id")
    val trackingId: String? = null
)
