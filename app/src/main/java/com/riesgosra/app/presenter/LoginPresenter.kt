package com.riesgosra.app.presenter

import com.riesgosra.app.model.data.SesionManager
import com.riesgosra.app.model.network.ApiService
import com.riesgosra.app.model.network.InicioSesionRequest
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.util.Log

class LoginPresenter(
    private val view: LoginView,
    private val apiService: ApiService,
    private val sesionManager: SesionManager,
    private val mainExecutor: Executor
) {

    private val backgroundExecutor =
        Executors.newSingleThreadExecutor()

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

        backgroundExecutor.execute {
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
                    verificarAccesoProtegido(token)
                }
            } catch (exception: Exception) {
                Log.e("LoginPresenter", "Error al iniciar sesión", exception)
                mainExecutor.execute {
                    view.ocultarCargando()
                    sesionManager.cerrarSesion()
                    view.mostrarError("No fue posible conectar con la API.")
                }
            }
        }
    }

    private fun verificarAccesoProtegido(token: String) {
        backgroundExecutor.execute {
            try {
                val resultado = apiService.accederRutaProtegida(token)
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
            } catch (exception: Exception) {
                Log.e("LoginPresenter", "No fue posible verificar la sesión", exception)
                mainExecutor.execute {
                    view.ocultarCargando()
                    sesionManager.cerrarSesion()
                    view.mostrarError("No fue posible validar el token.")
                }
            }
        }
    }

    fun destruir() {
        backgroundExecutor.shutdown()
    }
}