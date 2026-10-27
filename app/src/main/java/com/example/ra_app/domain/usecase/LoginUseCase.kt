package com.example.ra_app.domain.usecase

import com.example.ra_app.domain.model.User
import com.example.ra_app.domain.repository.RutaRepository
import com.example.ra_app.domain.util.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: RutaRepository
) {
    suspend operator fun invoke(identificador: String, contrasena: String): Resource<User> {
        if (identificador.isBlank() || contrasena.isBlank()) {
            return Resource.Error("Los campos no pueden estar vacíos")
        }
        return repository.login(identificador, contrasena)
    }
}
