package com.example.coalguard.data.local.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComplianceItemEntity(
    @SerialName("id")
    val id: String,
    @SerialName("mine_id")
    val mineId: String?,
    @SerialName("category")
    val category: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("due_date")
    val dueDate: String?,
    @SerialName("status")
    val status: String?,
    @SerialName("assigned_to")
    val assignedTo: String?,
    @SerialName("document_url")
    val documentUrl: String?
)
