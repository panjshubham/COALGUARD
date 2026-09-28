package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "contractors")
data class Contractor(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @ColumnInfo(name = "license_no")
    @SerialName("license_no")
    val licenseNo: String,
    @ColumnInfo(name = "license_expiry")
    @SerialName("license_expiry")
    val licenseExpiry: String,
    @ColumnInfo(name = "document_url")
    @SerialName("document_url")
    val documentUrl: String? = null
)
