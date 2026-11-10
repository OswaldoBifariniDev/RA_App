package com.riesgosra.app.login.model.repository

import com.riesgosra.app.login.model.data.UsuarioRegistro
import com.riesgosra.app.network.apiconfig
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class UsuarioRepository {
    private val ejecutor = Executors.newSingleThreadExecutor()
    private val principal = Handler(Looper.getMainLooper())
    @Volatile private var cerrado = false
    @Volatile private var conexionActual: HttpURLConnection? = null
    // Esta función sera llamada por  el ViewModel
    fun registrarUsuario(
        usuario: UsuarioRegistro,
        alExito: () -> Unit,
        alError: (String) -> Unit
    ) {
        ejecutor.execute {
            val resultado = runCatching {
                val conexion = URL(apiconfig.REGISTRO_URL).openConnection() as HttpURLConnection
                conexionActual = conexion
                try {
                    conexion.requestMethod = "POST"
                    conexion.connectTimeout = apiconfig.CONNECT_TIMEOUT_MS
                    conexion.readTimeout = apiconfig.READ_TIMEOUT_MS
                    conexion.doOutput = true
                    conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    conexion.setRequestProperty("Accept", "application/json")
                    val cuerpo = JSONObject().apply {
                        put("nombreApellido", usuario.nombreApellido.trim())
                        put("numeroIdentificacion", usuario.numeroIdentificacion.trim())
                        put("correoElectronico", usuario.correoElectronico.trim())
                        put("contrasena", usuario.contrasena)
                    }
                    conexion.outputStream.use { it.write(cuerpo.toString().toByteArray(Charsets.UTF_8)) }
                    val codigo = conexion.responseCode
                    val flujo = if (codigo in 200..299) conexion.inputStream else conexion.errorStream
                    val texto = flujo?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    val respuesta = try {
                        JSONObject(texto ?: "")
                    } catch (_: Exception) {
                        throw IllegalStateException("La API no devolvio una respuesta valida (HTTP $codigo).")
                    }
                    if (codigo !in 200..299 || !respuesta.optBoolean("registrado", false)) {
                        throw IllegalStateException(respuesta.optString("mensaje", "No fue posible crear la cuenta."))
                    }
                } finally {
                    conexionActual = null
                    conexion.disconnect()
                }
            }
            principal.post {
                if (!cerrado) resultado.fold(
                    onSuccess = { alExito() },
                    onFailure = { error -> alError(
                        if (error is IllegalStateException) error.message ?: "No fue posible crear la cuenta."
                        else "No fue posible conectar con la API. Comprueba la red y que el servidor este encendido."
                    ) }
                )
            }
        }
    }

    fun cerrar() {
        cerrado = true
        conexionActual?.disconnect()
        ejecutor.shutdownNow()
    }
}
