package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.model.Contractor

@Dao
interface ContractorsDao {
    @Query("SELECT * FROM contractors")
    fun getAllContractors(): LiveData<List<Contractor>>

    @Query("SELECT * FROM contractors WHERE id = :id")
    fun getContractorById(id: String): LiveData<Contractor?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contractor: Contractor)

    @Update
    suspend fun update(contractor: Contractor)

    @Delete
    suspend fun delete(contractor: Contractor)
}
