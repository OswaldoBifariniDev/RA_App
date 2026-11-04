package com.riesgosra.app.riesgos.model

import android.os.Handler
import android.os.Looper
import com.riesgosra.app.BuildConfig
import org.json.JSONArray
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

    override fun obtenerRiesgos(alExito: (List<Riesgo>) -> Unit, alError: () -> Unit) {
        val versionSolicitud = ++versionActual
        ejecutor.execute {
            val resultado = runCatching { consultarRiesgos() }
            hiloPrincipal.post {
                if (versionSolicitud == versionActual) resultado.fold(alExito, { alError() })
            }
        }
    }

    private fun consultarRiesgos(): List<Riesgo> {
        val conexion = URL("${BuildConfig.RIESGOS_API_URL}api/riesgos")
            .openConnection() as HttpURLConnection
        conexionActual = conexion
        try {
            conexion.connectTimeout = TIEMPO_ESPERA_MS
            conexion.readTimeout = TIEMPO_ESPERA_MS
            conexion.useCaches = false
            conexion.requestMethod = "GET"
            conexion.setRequestProperty("Accept", "application/json")
            check(conexion.responseCode in 200..299)
            val registros = conexion.inputStream.bufferedReader(Charsets.UTF_8).use { lector ->
                JSONArray(lector.readText())
            }
            return List(registros.length()) { indice ->
                val registro = registros.getJSONObject(indice)
                Riesgo(
                    idRiesgo = registro.getInt("id"),
                    nombre = registro.getString("nombre"),
                    analisisRiesgo = registro.getString("analisis"),
                    planAccion = registro.getString("planAccion"),
                    consejosEstudiante = registro.getString("consejos")
                )
            }
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

    private companion object {
        const val TIEMPO_ESPERA_MS = 10000
    }
}
