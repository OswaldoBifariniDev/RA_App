package com.riesgosra.app.perfil.presenter

import com.riesgosra.app.login.model.data.SesionManager
import com.riesgosra.app.perfil.model.PerfilDemostracionRepository

class PerfilPresenter(
    private val view: PerfilView,
    private val repository: PerfilDemostracionRepository,
    private val sesionManager: SesionManager
) {

    fun cargarPerfil() {
        view.mostrarPerfil(repository.obtenerPerfil())
    }

    fun cerrarSesion() {
        sesionManager.cerrarSesion()
        view.abrirInicioSesion()
    }
}