package com.example.coalguard.data.local.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MineEntity(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String?,
    @SerialName("subsidiary")
    val subsidiary: String?,
    @SerialName("region")
    val region: String?,
    @SerialName("state")
    val state: String?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("status")
    val status: String?
)
