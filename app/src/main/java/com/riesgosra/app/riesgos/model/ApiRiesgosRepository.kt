package com.riesgosra.app.riesgos.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.riesgosra.app.network.apiconfig
import org.json.JSONObject
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ApiRiesgosRepository(contexto: Context) : RiesgosRepository {
    private val contexto = contexto.applicationContext
    private val preferencias = this.contexto.getSharedPreferences("catalogo_riesgos", Context.MODE_PRIVATE)
    override var usandoDatosGuardados = false
        private set
    override var usandoCatalogoIncluido = false
        private set
    private var correccionEditorialAplicada = false
    private val revisiones by lazy {
        contexto.assets.open("correcciones_catalogo_20261117.json").bufferedReader().use { JSONArray(it.readText()) }
    }
    private val hiloPrincipal = Handler(Looper.getMainLooper())
    private val ejecutor = Executors.newSingleThreadExecutor()
    @Volatile
    private var conexionActual: HttpURLConnection? = null
    @Volatile
    private var versionActual = 0
    private var lecturaActual: Future<*>? = null

    override fun obtenerRiesgos(alExito: (List<Riesgo>) -> Unit, alError: () -> Unit) {
        val versionSolicitud = ++versionActual
        lecturaActual?.cancel(true)
        conexionActual?.disconnect()
        lecturaActual = ejecutor.submit {
            val guardados = runCatching {
                preferencias.getString("registros", null)?.let(::interpretar)
            }.getOrNull()
            val incluidos = runCatching {
                contexto.assets.open("riesgos_registrados.json").bufferedReader().use { interpretar(it.readText()) }
            }.getOrNull()
            val respaldo = guardados ?: incluidos
            if (respaldo != null) hiloPrincipal.post {
                if (versionSolicitud == versionActual) {
                    usandoDatosGuardados = true
                    usandoCatalogoIncluido = guardados == null
                    alExito(respaldo)
                }
            }
            val resultado = runCatching { consultarRiesgos() }
            hiloPrincipal.post {
                if (versionSolicitud == versionActual) resultado.fold({ registros ->
                    usandoDatosGuardados = correccionEditorialAplicada
                    usandoCatalogoIncluido = correccionEditorialAplicada
                    alExito(registros)
                }, { if (respaldo == null) alError() })
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
            val contenido = conexion.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            correccionEditorialAplicada = false
            val registros = interpretar(contenido)
            preferencias.edit().putString("registros", contenido).commit()
            return registros
        } finally {
            conexionActual = null
            conexion.disconnect()
        }
    }

    private fun interpretar(contenido: String): List<Riesgo> {
        val registros = if (contenido.trimStart().startsWith("[")) JSONArray(contenido) else {
            val respuesta = JSONObject(contenido)
            check(respuesta.getBoolean("success"))
            respuesta.getJSONArray("data")
        }
        return List(registros.length()) { indice ->
                val registro = registros.getJSONObject(indice)
                Riesgo(
                    idRiesgo = registro.getInt("id"),
                    nombre = registro.getString("nombre"),
                    analisisRiesgo = registro.optString("analisis_riesgo", registro.optString("analisis")),
                    planAccion = registro.optString("plan_accion", registro.optString("planAccion")),
                    consejosEstudiante = registro.optString("consejos_estudiante", registro.optString("consejos"))
                ).let(::aplicarRevisionAnterior).also {
                    check(it.nombre.isNotBlank() && it.analisisRiesgo.isNotBlank() &&
                        it.planAccion.isNotBlank() && it.consejosEstudiante.isNotBlank())
                }
            }
    }

    // Solo migra textos anteriores conocidos; conserva cualquier contenido nuevo del servidor.
    // La revision incluida es una exportacion de la base local Registro_Riesgo.
    private fun aplicarRevisionAnterior(riesgo: Riesgo): Riesgo {
        val revision = (0 until revisiones.length()).map { revisiones.getJSONObject(it) }
            .firstOrNull { it.getInt("id") == riesgo.idRiesgo } ?: return riesgo
        val anterior = revision.getJSONObject("anterior")
        val revisado = revision.getJSONObject("revisado")
        fun actualizar(campo: String, valor: String): String {
            if (valor != anterior.getString(campo)) return valor
            correccionEditorialAplicada = true
            return revisado.getString(campo)
        }
        return riesgo.copy(
            analisisRiesgo = actualizar("analisis", riesgo.analisisRiesgo),
            planAccion = actualizar("planAccion", riesgo.planAccion),
            consejosEstudiante = actualizar("consejos", riesgo.consejosEstudiante)
        )
    }

    override fun cancelar() {
        versionActual++
        lecturaActual?.cancel(true)
        ejecutor.shutdownNow()
        conexionActual?.disconnect()
    }

}
