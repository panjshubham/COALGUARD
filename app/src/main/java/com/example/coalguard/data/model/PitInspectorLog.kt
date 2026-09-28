package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "pit_inspector_logs")
data class PitInspectorLog(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @ColumnInfo(name = "inspector_name")
    @SerialName("inspector_name")
    val inspectorName: String,
    @ColumnInfo(name = "pit_id")
    @SerialName("pit_id")
    val pitId: String,
    @SerialName("notes")
    val notes: String,
    @SerialName("timestamp")
    val timestamp: String,
    @ColumnInfo(name = "data_hash")
    @SerialName("data_hash")
    val dataHash: String?,
    @ColumnInfo(name = "prev_hash")
    @SerialName("prev_hash")
    val prevHash: String?
)
