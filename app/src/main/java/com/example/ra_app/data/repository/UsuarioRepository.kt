package com.example.ra_app.data.repository

import com.example.ra_app.data.model.Usuario

class UsuarioRepository {
    // Esta función sera llamada por  el ViewModel
    fun registrarUsuario(usuario: Usuario, alExito: () -> Unit, alError: (String) -> Unit) {
        // CONEXION: Aquí el equipo de BD conectará con SQLite o firebase  "osea  aqyui va la conección"
        // Por ahora, simulamos que se guardó exitosamente
        if (usuario.correoElectronico.isNotEmpty() && usuario.contrasena.isNotEmpty()) {
            alExito()
        } else {
            alError("Error: Datos incompletos")
        }
    }
}