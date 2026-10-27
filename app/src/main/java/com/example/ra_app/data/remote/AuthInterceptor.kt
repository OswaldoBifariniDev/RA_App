package com.example.ra_app.data.remote
import com.example.ra_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val protected = request.url.encodedPath in setOf("/ruta_protegida.php", "/ruta_crear.php")
        val builder = request.newBuilder().removeHeader("Authorization")
        if (protected) tokenManager.getToken()?.takeIf { it.isNotBlank() }?.let {
            builder.header("Authorization", "Bearer $it")
        }
        return chain.proceed(builder.build())
    }
}
