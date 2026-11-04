package com.riesgosra.app.login.presenter

interface LoginView {

    fun mostrarCargando()

    fun ocultarCargando()

    fun limpiarErrores()

    fun mostrarErrorIdentificador(mensaje: String)

    fun mostrarErrorContrasena(mensaje: String)

    fun mostrarInicioSesionExitoso()

    fun mostrarAccesoAutorizado()

    fun mostrarMensaje(mensaje: String)

    fun mostrarError(mensaje: String)

    fun abrirPantallaPrincipal()
}