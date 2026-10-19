package com.example.ra_app.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ra_app.R
import com.example.ra_app.data.model.Usuario
import com.example.ra_app.data.repository.UsuarioRepository

class RegistroViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()

    val registroExitoso = MutableLiveData<Boolean>()
    val mensajeError = MutableLiveData<Int>()

    fun validarRegistro(nombre: String, identificacion: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || identificacion.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            mensajeError.value = R.string.error_registro_campos_obligatorios
            return
        }
        if (!correo.contains("@")) {
            mensajeError.value = R.string.error_registro_correo_invalido
            return
        }
        if (contrasena.length < LONGITUD_MINIMA_CONTRASENA) {
            mensajeError.value = R.string.error_registro_contrasena_invalida
            return
        }

        val nuevoUsuario = Usuario(
            nombreApellido = nombre,
            numeroIdentificacion = identificacion,
            correoElectronico = correo,
            contrasena = contrasena
        )

        repositorio.registrarUsuario(
            usuario = nuevoUsuario,
            alExito = { registroExitoso.value = true },
            alError = { mensajeError.value = R.string.error_registro_datos_incompletos }
        )
    }

    private companion object {
        const val LONGITUD_MINIMA_CONTRASENA = 6
    }
}
