package com.droidnova.studytogether.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ProgressDao {

    @Insert
    suspend fun insertProgress(progressEntity: ProgressEntity)

    @Update
   suspend fun updateProgress(progressEntity: ProgressEntity)

    @Delete
    suspend fun deleteProgress(progressEntity: ProgressEntity)

    @Query("SELECT * FROM `progress-table`")
    fun getProgress(): Flow<List<ProgressEntity>>



}