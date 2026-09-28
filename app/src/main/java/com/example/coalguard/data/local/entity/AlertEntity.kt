package com.example.coalguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: String,
    @ColumnInfo(name = "related_entity_id")
    @SerialName("related_entity_id")
    val relatedEntityId: Int,
    @SerialName("message")
    val message: String,
    @SerialName("severity")
    val severity: String,
    @ColumnInfo(name = "is_read")
    @SerialName("is_read")
    val isRead: Boolean = false,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null
)
