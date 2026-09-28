package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "audit_ledger")
data class AuditLedgerEntry(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @ColumnInfo(name = "table_name")
    @SerialName("table_name")
    val tableName: String,
    @ColumnInfo(name = "record_id")
    @SerialName("record_id")
    val recordId: Int,
    @SerialName("action")
    val action: String,
    @ColumnInfo(name = "data_hash")
    @SerialName("data_hash")
    val dataHash: String,
    @ColumnInfo(name = "prev_hash")
    @SerialName("prev_hash")
    val prevHash: String? = null,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "user_id")
    @SerialName("user_id")
    val userId: String? = null,
    @ColumnInfo(name = "old_values")
    @SerialName("old_values")
    val oldValues: String? = null,
    @ColumnInfo(name = "new_values")
    @SerialName("new_values")
    val newValues: String? = null
) {
    val recordType: String get() = tableName
    val timestamp: String get() = createdAt ?: ""
}
