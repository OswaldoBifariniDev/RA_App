package com.riesgosra.app.rutasegura.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Escenarios disponibles en Ruta segura.
 */
enum class EscenarioRuta {
    SISMO,
    INUNDACION,
    INCENDIO
}

/**
 * Modelo encargado de gestionar los datos
 * y comprobar los permisos necesarios.
 */
class RutaSeguraModelo(context: Context) {

    private val contextoAplicacion =
        context.applicationContext

    fun tienePermisoCamara(): Boolean {
        return ContextCompat.checkSelfPermission(
            contextoAplicacion,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun obtenerEscenarioInicial(): EscenarioRuta {
        return EscenarioRuta.SISMO
    }
}