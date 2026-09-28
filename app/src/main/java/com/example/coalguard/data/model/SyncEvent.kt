package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "sync_events")
data class SyncEvent(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @ColumnInfo(name = "client_uuid")
    @SerialName("client_uuid")
    val clientUuid: String,
    @SerialName("endpoint")
    val endpoint: String,
    @SerialName("payload")
    val payload: String,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null
)
