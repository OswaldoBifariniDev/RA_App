package com.riesgosra.app.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.riesgosra.app.model.data.UsuarioRegistro
import com.riesgosra.app.model.repository.UsuarioRepository

class RegistroViewModel : ViewModel() {
    private val repositorio = UsuarioRepository()

    val registroExitoso = MutableLiveData<Boolean>()
    val mensajeError = MutableLiveData<String>()

    fun validarRegistro(nombre: String, identificacion: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || identificacion.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            mensajeError.value = "Todos los campos son obligatorios"
            return
        }
        if (!correo.contains("@")) {
            mensajeError.value = "Ingrese un correo electrónico válido"
            return
        }
        if (contrasena.length < 6) {
            mensajeError.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        val nuevoUsuario = UsuarioRegistro(
            nombreApellido = nombre,
            numeroIdentificacion = identificacion,
            correoElectronico = correo,
            contrasena = contrasena
        )

        repositorio.registrarUsuario(
            usuario = nuevoUsuario,
            alExito = { registroExitoso.value = true },
            alError = { error -> mensajeError.value = error }
        )
    }
}