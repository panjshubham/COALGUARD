package com.example.coalguard.data.local.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ViolationEntity(
    @SerialName("id")
    val id: String,
    @SerialName("inspection_id")
    val inspectionId: String?,
    @SerialName("mine_id")
    val mineId: String?,
    @SerialName("category")
    val category: String?,
    @SerialName("severity")
    val severity: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("photo_url")
    val photoUrl: String?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("status")
    val status: String?,
    @SerialName("corrective_action")
    val correctiveAction: String?
)
