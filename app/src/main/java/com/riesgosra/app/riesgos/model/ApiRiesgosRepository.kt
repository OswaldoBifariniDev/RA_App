package com.riesgosra.app.riesgos.model

import android.os.Handler
import android.os.Looper
import com.riesgosra.app.BuildConfig
import com.google.gson.JsonParseException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ApiRiesgosRepository : RiesgosRepository {
    private val hiloPrincipal = Handler(Looper.getMainLooper())
    private val ejecutor = Executors.newSingleThreadExecutor()
    @Volatile
    private var conexionActual: HttpURLConnection? = null
    @Volatile
    private var versionActual = 0

    override fun obtenerRiesgos(alExito: (List<Riesgo>) -> Unit, alError: (String) -> Unit) {
        val versionSolicitud = ++versionActual
        ejecutor.execute {
            val resultado = runCatching { consultarRiesgos() }
            hiloPrincipal.post {
                if (versionSolicitud == versionActual) resultado.fold(alExito, { error ->
                    Log.e("RiesgosRepository", "No se pudo cargar el catálogo", error)
                    val mensaje = when (error) {
                        is ErrorHttp -> when (error.status) {
                            404 -> "El servidor no encuentra el catálogo de riesgos. Revisa la configuración de la API."
                            401, 403 -> "El servidor no autorizó la consulta de riesgos."
                            else -> "El servidor no pudo consultar los riesgos (HTTP ${error.status})."
                        }
                        is SocketTimeoutException -> "El servidor tardó demasiado en responder. Vuelve a intentarlo."
                        is UnknownHostException, is ConnectException -> "No se pudo conectar con el servidor de riesgos. Verifica que el computador y el teléfono estén en la misma red."
                        is JsonParseException -> "La respuesta del servidor no tiene el formato esperado para los riesgos."
                        else -> "No se pudo cargar la información de riesgos. Vuelve a intentarlo."
                    }
                    alError(mensaje)
                })
            }
        }
    }

    private fun consultarRiesgos(): List<Riesgo> {
        val conexion = URL("${BuildConfig.RIESGOS_CATALOGO_URL}obtener_riesgos.php")
            .openConnection() as HttpURLConnection
        conexionActual = conexion
        try {
            conexion.connectTimeout = TIEMPO_ESPERA_MS
            conexion.readTimeout = TIEMPO_ESPERA_MS
            conexion.useCaches = false
            conexion.requestMethod = "GET"
            conexion.setRequestProperty("Accept", "application/json")
            val status = conexion.responseCode
            if (status !in 200..299) throw ErrorHttp(status)
            val json = conexion.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            return RiesgosJsonParser().parse(json)
        } finally {
            conexionActual = null
            conexion.disconnect()
        }
    }

    override fun cancelar() {
        versionActual++
        ejecutor.shutdownNow()
        conexionActual?.disconnect()
    }

    private class ErrorHttp(val status: Int) : java.io.IOException("HTTP $status")

    private companion object {
        const val TIEMPO_ESPERA_MS = 10000
    }
}
