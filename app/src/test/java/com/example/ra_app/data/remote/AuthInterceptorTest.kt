package com.example.ra_app.data.remote
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthInterceptorTest {
    private class Chain(private val initial: Request) : Interceptor.Chain {
        lateinit var sent: Request
        override fun request() = initial
        override fun proceed(request: Request): Response {
            sent = request
            return Response.Builder().request(request).protocol(Protocol.HTTP_1_1)
                .code(200).message("OK").body("{}".toResponseBody()).build()
        }
        override fun connection(): Connection? = null
        override fun call(): Call = OkHttpClient().newCall(initial)
        override fun connectTimeoutMillis() = 15000
        override fun readTimeoutMillis() = 15000
        override fun writeTimeoutMillis() = 15000
        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }
    @Test fun enviaBearerEnLaRutaDeProduccion() {
        val chain = Chain(Request.Builder().url("http://localhost/riesgos_ra_api_postgresql/ruta_protegida.php").build())
        AuthInterceptor { "token_de_prueba" }.intercept(chain).close()
        assertEquals("Bearer token_de_prueba", chain.sent.header("Authorization"))
    }
    @Test fun nuncaEnviaUnaSesionAnteriorAlIniciarSesion() {
        val chain = Chain(Request.Builder().url("http://localhost/riesgos_ra_api_postgresql/login.php")
            .header("Authorization", "Bearer anterior").build())
        AuthInterceptor { "token_de_prueba" }.intercept(chain).close()
        assertNull(chain.sent.header("Authorization"))
    }
    @Test fun perfilSinTokenNoReutilizaCabecerasAntiguas() {
        val chain = Chain(Request.Builder().url("http://localhost/riesgos_ra_api_postgresql/perfil.php")
            .header("Authorization", "Bearer anterior").build())
        AuthInterceptor { null }.intercept(chain).close()
        assertNull(chain.sent.header("Authorization"))
    }
}
