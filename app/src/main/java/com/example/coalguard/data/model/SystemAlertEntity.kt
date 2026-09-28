package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "system_alerts")
data class SystemAlertEntity(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("severity")
    val severity: String,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String,
    @ColumnInfo(name = "is_read")
    @SerialName("is_read")
    val isRead: Boolean = false
)
