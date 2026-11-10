package com.riesgosra.app.riesgos.model

import android.os.Handler
import android.os.Looper
import com.riesgosra.app.network.apiconfig
import org.json.JSONObject
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
        val conexion = URL(apiconfig.RIESGOS_URL)
            .openConnection() as HttpURLConnection
        conexionActual = conexion
        try {
            conexion.connectTimeout = apiconfig.CONNECT_TIMEOUT_MS
            conexion.readTimeout = apiconfig.READ_TIMEOUT_MS
            conexion.useCaches = false
            conexion.requestMethod = "GET"
            conexion.setRequestProperty("Accept", "application/json")
            check(conexion.responseCode in 200..299)
            val registros = conexion.inputStream.bufferedReader(Charsets.UTF_8).use { lector ->
                val respuesta = JSONObject(lector.readText())
                check(respuesta.getBoolean("success"))
                respuesta.getJSONArray("data")
            }
            return List(registros.length()) { indice ->
                val registro = registros.getJSONObject(indice)
                Riesgo(
                    idRiesgo = registro.getInt("id"),
                    nombre = registro.getString("nombre"),
                    analisisRiesgo = registro.getString("analisis_riesgo"),
                    planAccion = registro.getString("plan_accion"),
                    consejosEstudiante = registro.getString("consejos_estudiante")
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

}
