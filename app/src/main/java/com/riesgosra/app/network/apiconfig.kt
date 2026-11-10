package com.riesgosra.app.network

object apiconfig {

        const val BASE_URL = "https://riesgo-rapp.onrender.com/"
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 90_000
        const val USUARIOS_URL = BASE_URL + "usuarios.php"
        const val LOGIN_URL = BASE_URL + "login.php"
        const val REGISTRO_URL = BASE_URL + "registro.php"
        const val RIESGOS_URL = BASE_URL + "obtener_riesgo.php"

}
