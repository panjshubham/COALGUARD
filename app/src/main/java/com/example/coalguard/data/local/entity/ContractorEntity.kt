package com.example.coalguard.data.local.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContractorEntity(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String?,
    @SerialName("mine_id")
    val mineId: String?,
    @SerialName("contract_start")
    val contractStart: String?,
    @SerialName("contract_end")
    val contractEnd: String?,
    @SerialName("status")
    val status: String?
)
