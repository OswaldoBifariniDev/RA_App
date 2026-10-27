package com.example.ra_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ra_app.data.local.entity.RutaEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RutaDao {
    @androidx.room.Transaction
    open suspend fun replaceAll(rutas: List<RutaEntity>) {
        clearRutas()
        insertAll(rutas)
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRuta(ruta: RutaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(rutas: List<RutaEntity>)

    @Query("SELECT * FROM rutas ORDER BY id DESC")
    abstract fun getAllRutas(): Flow<List<RutaEntity>>

    @Query("DELETE FROM rutas")
    abstract suspend fun clearRutas()
}
