package com.riesgosra.app.login.presenter

import com.riesgosra.app.login.model.data.SesionManager
import com.riesgosra.app.login.model.network.ApiService
import com.riesgosra.app.login.model.network.InicioSesionRequest
import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException
import android.util.Log

class LoginPresenter(
    private val view: LoginView,
    private val apiService: ApiService,
    private val sesionManager: SesionManager,
    private val mainExecutor: Executor
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    fun iniciarSesion(
        identificadorUsuario: String,
        contrasena: String
    ) {
        view.limpiarErrores()

        val identificadorLimpio = identificadorUsuario.trim()
        var formularioValido = true

        if (identificadorLimpio.isEmpty()) {
            view.mostrarErrorIdentificador(
                "Ingrese su correo electrónico o cédula."
            )
            formularioValido = false
        }

        if (contrasena.isEmpty()) {
            view.mostrarErrorContrasena("Ingrese su contraseña.")
            formularioValido = false
        }

        if (!formularioValido) return

        // No conserva un token anterior si las credenciales nuevas fallan.
        sesionManager.cerrarSesion()
        view.mostrarCargando()

        val request = InicioSesionRequest(
            identificadorUsuario = identificadorLimpio,
            contrasena = contrasena
        )

        scope.launch {
            try {
                val respuesta = apiService.iniciarSesion(request)
                mainExecutor.execute callback@{
                    if (!respuesta.autenticado) {
                        view.ocultarCargando()
                        sesionManager.cerrarSesion()
                        view.mostrarMensaje(respuesta.mensaje)
                        return@callback
                    }

                    val token = respuesta.token
                    if (token.isNullOrBlank()) {
                        view.ocultarCargando()
                        sesionManager.cerrarSesion()
                        view.mostrarError("La API no devolvió un token válido.")
                        return@callback
                    }

                    sesionManager.guardarToken(token)
                    verificarAccesoProtegido()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                Log.e("LoginPresenter", "Error al iniciar sesión", exception)
                mainExecutor.execute {
                    view.ocultarCargando()
                    sesionManager.cerrarSesion()
                    view.mostrarError(mensajeError(exception))
                }
            }
        }
    }

    private fun verificarAccesoProtegido() {
        scope.launch {
            try {
                val resultado = apiService.accederRutaProtegida()
                mainExecutor.execute {
                    view.ocultarCargando()
                    if (resultado.first) {
                        view.mostrarInicioSesionExitoso()
                        view.mostrarAccesoAutorizado()
                        view.abrirPantallaPrincipal()
                    } else {
                        sesionManager.cerrarSesion()
                        view.mostrarMensaje(resultado.second)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                Log.e("LoginPresenter", "No fue posible verificar la sesión", exception)
                mainExecutor.execute {
                    view.ocultarCargando()
                    sesionManager.cerrarSesion()
                    view.mostrarError(mensajeError(exception))
                }
            }
        }
    }

    private fun mensajeError(error: Exception): String = when (error) {
        is HttpException -> when (error.code()) {
            401 -> "Credenciales incorrectas o sesión vencida."
            403 -> "La API rechazó el acceso a esta cuenta."
            404 -> "El servidor respondió, pero no existe la ruta solicitada. Verifica la URL de la API."
            else -> "La API respondió con un error HTTP ${error.code()}."
        }
        is ConnectException -> "No se pudo conectar al servidor. Comprueba que el backend esté iniciado y que la dirección y el puerto sean correctos."
        is SocketTimeoutException -> "El servidor tardó demasiado en responder. Revisa la red y el backend."
        is UnknownHostException -> "No se encontró el servidor. Revisa la dirección de la API y la conexión."
        is JsonParseException -> "El servidor respondió con un JSON incompatible con el login."
        is IOException -> "Error de comunicación con el servidor. Revisa la red y la URL de la API."
        else -> "La respuesta de la API no pudo procesarse. Revisa el contrato del login."
    }

    fun destruir() {
        scope.cancel()
    }
}