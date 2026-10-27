package com.example.ra_app.di

import android.content.Context
import androidx.room.Room
import com.example.ra_app.data.local.AppDatabase
import com.example.ra_app.data.local.dao.RutaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Version 1 contained risk_analyses only; preserve existing user data.
    private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `rutas` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `createdAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        }
    }


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "risk_analysis_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideRutaDao(database: AppDatabase): RutaDao {
        return database.rutaDao()
    }
}
