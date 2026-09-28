package com.example.coalguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String = "",
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: String = "mine_official",
    @ColumnInfo(name = "assigned_mine_id")
    @SerialName("assigned_mine_id")
    val assignedMineId: Int? = null,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: String? = null
)
