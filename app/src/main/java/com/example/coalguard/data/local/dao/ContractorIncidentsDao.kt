package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.local.entity.ContractorIncidentEntity

@Dao
interface ContractorIncidentsDao {
    @Query("SELECT * FROM contractor_incidents")
    fun getAllContractorIncidents(): LiveData<List<ContractorIncidentEntity>>

    @Query("SELECT * FROM contractor_incidents WHERE id = :id")
    fun getContractorIncidentById(id: String): LiveData<ContractorIncidentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: ContractorIncidentEntity)

    @Update
    suspend fun update(incident: ContractorIncidentEntity)

    @Delete
    suspend fun delete(incident: ContractorIncidentEntity)
}
