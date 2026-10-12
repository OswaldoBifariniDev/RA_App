package com.riesgosra.app.model.data

import android.content.Context

class SesionManager(context: Context) {

    companion object {
        private const val NOMBRE_PREFERENCIAS = "sesion_usuario"
        private const val CLAVE_TOKEN = "token"
    }

    private val preferencias = context.getSharedPreferences(
        NOMBRE_PREFERENCIAS,
        Context.MODE_PRIVATE
    )

    fun guardarToken(token: String) {
        preferencias.edit()
            .putString(CLAVE_TOKEN, token)
            .apply()
    }

    fun obtenerToken(): String? {
        return preferencias.getString(CLAVE_TOKEN, null)
    }

    fun cerrarSesion() {
        preferencias.edit()
            .remove(CLAVE_TOKEN)
            .apply()
    }
}