package com.example.ra_app.data.repository

import android.content.Context
import com.example.ra_app.data.model.Usuario

class UsuarioRepository {
    fun registrarUsuario(usuario: Usuario, alExito: () -> Unit, alError: () -> Unit) {
        // TODO: Conectar con SQLite o Firebase al integrar la persistencia.
        // Esta respuesta provisional permite probar el flujo de registro.
        if (usuario.correoElectronico.isNotEmpty() && usuario.contrasena.isNotEmpty()) {
            alExito()
        } else {
            alError()
        }
    }

    companion object {
        private const val PREFERENCIAS_USUARIO = "preferencias_usuario"
        private const val CLAVE_NOMBRE_USUARIO = "nombre_usuario"

        fun guardarNombreUsuario(contexto: Context, nombre: String) {
            contexto.applicationContext
                .getSharedPreferences(PREFERENCIAS_USUARIO, Context.MODE_PRIVATE)
                .edit()
                .putString(CLAVE_NOMBRE_USUARIO, nombre.trim())
                .apply()
        }

        fun obtenerNombreUsuario(contexto: Context): String? {
            return contexto.applicationContext
                .getSharedPreferences(PREFERENCIAS_USUARIO, Context.MODE_PRIVATE)
                .getString(CLAVE_NOMBRE_USUARIO, null)
                ?.trim()
                ?.takeIf(String::isNotBlank)
        }
    }
}
