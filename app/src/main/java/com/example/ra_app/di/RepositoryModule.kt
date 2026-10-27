package com.example.ra_app.di

import com.example.ra_app.data.repository.RutaRepositoryImpl
import com.example.ra_app.domain.repository.RutaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRutaRepository(
        rutaRepositoryImpl: RutaRepositoryImpl
    ): RutaRepository
}
