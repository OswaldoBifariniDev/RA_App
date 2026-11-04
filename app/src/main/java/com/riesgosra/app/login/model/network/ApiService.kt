package com.riesgosra.app.login.model.network

import com.riesgosra.app.login.model.data.UsuarioAutenticado
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiService {

    companion object {
        // URL configurada para el teléfono físico y la red local verificada.
        // Para usar el emulador Android, cambia la IP por 10.0.2.2.
        private const val BASE_URL = "http://192.168.137.245/riesgos_ra_api/"
        private const val URL_LOGIN = "${BASE_URL}login.php"
        private const val URL_RUTA_PROTEGIDA = "${BASE_URL}ruta_protegida.php"
    }

    fun iniciarSesion(
        request: InicioSesionRequest
    ): InicioSesionResponse {
        val url = URL(URL_LOGIN)

        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            val jsonRequest = JSONObject().apply {
                put(
                    "identificadorUsuario",
                    request.identificadorUsuario
                )

                put(
                    "contrasena",
                    request.contrasena
                )
            }

            connection.outputStream.use { outputStream ->
                outputStream.write(
                    jsonRequest.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val responseCode = connection.responseCode

            val responseStream =
                if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val responseBody = BufferedReader(
                InputStreamReader(
                    responseStream,
                    Charsets.UTF_8
                )
            ).use { reader ->
                reader.readText()
            }

            convertirRespuesta(
                responseBody
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun convertirRespuesta(
        responseBody: String
    ): InicioSesionResponse {
        val jsonResponse = JSONObject(responseBody)

        val usuarioJson =
            jsonResponse.optJSONObject("usuario")

        val usuario = usuarioJson?.let {
            UsuarioAutenticado(
                idUsuario = it.getInt("idUsuario"),
                identificadorUsuario =
                    it.getString("identificadorUsuario"),
                rolUsuario =
                    it.getString("rolUsuario")
            )
        }

        return InicioSesionResponse(
            autenticado =
                jsonResponse.getBoolean("autenticado"),
            mensaje =
                jsonResponse.getString("mensaje"),
            token =
                (jsonResponse.opt("token") as? String)
                    ?.takeIf { it.isNotBlank() },
            usuario = usuario
        )
    }

    fun accederRutaProtegida(
        token: String
    ): Pair<Boolean, String> {

        val url = URL(URL_RUTA_PROTEGIDA)

        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.setRequestProperty(
                "Authorization",
                "Bearer $token"
            )

            val responseCode = connection.responseCode

            val responseStream =
                if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val responseBody = BufferedReader(
                InputStreamReader(
                    responseStream,
                    Charsets.UTF_8
                )
            ).use { reader ->
                reader.readText()
            }

            val jsonResponse = JSONObject(responseBody)

            val autorizado =
                jsonResponse.optBoolean(
                    "autorizado",
                    false
                )

            val mensaje =
                jsonResponse.optString(
                    "mensaje",
                    "Respuesta desconocida."
                )

            Pair(
                autorizado,
                mensaje
            )
        } finally {
            connection.disconnect()
        }
    }
}