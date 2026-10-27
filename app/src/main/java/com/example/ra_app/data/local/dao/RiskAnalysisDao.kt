package com.example.ra_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ra_app.data.model.RiskAnalysis
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskAnalysisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analysis: RiskAnalysis): Long

    @Query("SELECT * FROM risk_analyses ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 20): Flow<List<RiskAnalysis>>

    @Query("SELECT * FROM risk_analyses ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentList(limit: Int = 20): List<RiskAnalysis>

    @Query("SELECT * FROM risk_analyses WHERE isSynced = 0")
    suspend fun getPendingSync(): List<RiskAnalysis>

    @Query("SELECT * FROM risk_analyses WHERE id = :id")
    suspend fun getById(id: Long): RiskAnalysis?
}
