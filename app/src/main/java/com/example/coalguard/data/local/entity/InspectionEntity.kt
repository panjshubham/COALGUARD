package com.example.coalguard.data.local.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InspectionEntity(
    @SerialName("id")
    val id: String,
    @SerialName("mine_id")
    val mineId: String?,
    @SerialName("inspector_id")
    val inspectorId: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("scheduled_date")
    val scheduledDate: String?,
    @SerialName("status")
    val status: String?
)
