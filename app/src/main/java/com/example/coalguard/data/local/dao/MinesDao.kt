package com.example.coalguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.coalguard.data.model.Mine

@Dao
interface MinesDao {
    @Query("SELECT * FROM mines")
    fun getAllMines(): LiveData<List<Mine>>

    @Query("SELECT * FROM mines WHERE id = :id")
    fun getMineById(id: String): LiveData<Mine?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mine: Mine)

    @Update
    suspend fun update(mine: Mine)

    @Delete
    suspend fun delete(mine: Mine)
}
