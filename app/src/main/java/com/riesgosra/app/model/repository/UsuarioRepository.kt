package com.riesgosra.app.model.repository

import com.riesgosra.app.model.data.UsuarioRegistro

class UsuarioRepository {
    // Esta función sera llamada por  el ViewModel
    fun registrarUsuario(
        usuario: UsuarioRegistro,
        alExito: () -> Unit,
        alError: (String) -> Unit
    ) {
        // La API entregada no dispone de un endpoint de registro.
        // No simular un guardado ni afirmar que se creó una cuenta en SQL Server.
        alError("El registro todavía no está conectado a la API.")
    }
}