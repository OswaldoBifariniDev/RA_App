package com.riesgosra.app.login.model.data

import android.content.Context

class SesionManager(context: Context) {

    companion object {
        private const val NOMBRE_PREFERENCIAS = "auth_prefs"
        private const val CLAVE_TOKEN = "jwt_token"
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