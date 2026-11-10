package com.riesgosra.app.network
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object usuarioapi {


    class UsuariosApi {

        fun consultarTotalUsuarios(): Int {

            val conexion = URL(apiconfig.USUARIOS_URL)
                .openConnection() as HttpURLConnection

            return try {
                conexion.requestMethod = "GET"
                conexion.connectTimeout = apiconfig.CONNECT_TIMEOUT_MS
                conexion.readTimeout = apiconfig.READ_TIMEOUT_MS

                if (conexion.responseCode != 200) {
                    throw Exception("Error HTTP: ${conexion.responseCode}")
                }

                val respuesta = conexion.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val json = JSONObject(respuesta)

                if (json.getString("estado") != "exito") {
                    throw Exception("Error en la API")
                }

                json.getInt("total_usuarios")

            } finally {
                conexion.disconnect()
            }
        }
    }

}
