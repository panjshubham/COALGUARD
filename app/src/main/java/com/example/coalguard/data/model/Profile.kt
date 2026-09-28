package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @ColumnInfo(name = "full_name")
    @SerialName("full_name")
    val fullName: String,
    @SerialName("role")
    val role: String,
    @SerialName("designation")
    val designation: String? = null,
    @ColumnInfo(name = "badge_id")
    @SerialName("badge_id")
    val badgeId: String? = null,
    @SerialName("subsidiary")
    val subsidiary: String? = null,
    @ColumnInfo(name = "assigned_mine_id")
    @SerialName("assigned_mine_id")
    val assignedMineId: Int? = null
)
