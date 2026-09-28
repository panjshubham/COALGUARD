package com.example.coalguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cg_sync_queue")
data class SyncQueueEntity(
    @PrimaryKey
    val id: String,
    val endpoint: String,
    val method: String, // POST, PUT, PATCH, DELETE
    val payloadJson: String,
    val timestamp: String
)
