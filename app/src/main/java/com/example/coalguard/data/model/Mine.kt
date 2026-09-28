package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "mines")
data class Mine(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String = "underground",
    @SerialName("subsidiary")
    val subsidiary: String,
    @SerialName("lat")
    val lat: Double? = null,
    @SerialName("lng")
    val lng: Double? = null,
    @ColumnInfo(name = "radius_m")
    @SerialName("radius_m")
    val radiusM: Int = 500,
    @SerialName("region")
    val region: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("status")
    val status: String = "active"
) {
    val riskScore: Float get() = 78.5f
}
