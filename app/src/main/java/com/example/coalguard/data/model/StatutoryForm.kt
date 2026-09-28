package com.example.coalguard.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "statutory_forms")
data class StatutoryForm(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @ColumnInfo(name = "form_type")
    @SerialName("form_type")
    val formType: String,
    @SerialName("title")
    val title: String,
    @ColumnInfo(name = "mine_id")
    @SerialName("mine_id")
    val mineId: Int,
    @ColumnInfo(name = "generated_at")
    @SerialName("generated_at")
    val generatedAt: String,
    @ColumnInfo(name = "content_json")
    @SerialName("content_json")
    val contentJson: String
)
