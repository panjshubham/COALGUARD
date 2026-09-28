package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.model.ComplianceItem

@Dao
interface ComplianceItemsDao {
    @Query("SELECT * FROM compliance_items")
    fun getAllComplianceItems(): LiveData<List<ComplianceItem>>

    @Query("SELECT * FROM compliance_items WHERE id = :id")
    fun getComplianceItemById(id: String): LiveData<ComplianceItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ComplianceItem)

    @Update
    suspend fun update(item: ComplianceItem)

    @Delete
    suspend fun delete(item: ComplianceItem)
}
