package com.example.ra_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ra_app.data.local.dao.RiskAnalysisDao
import com.example.ra_app.data.local.dao.RutaDao
import com.example.ra_app.data.local.entity.RutaEntity
import com.example.ra_app.data.model.RiskAnalysis

@Database(entities = [RiskAnalysis::class, RutaEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun riskAnalysisDao(): RiskAnalysisDao
    abstract fun rutaDao(): RutaDao

}
