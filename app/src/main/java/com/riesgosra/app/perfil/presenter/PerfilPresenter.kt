package com.riesgosra.app.perfil.presenter

import com.riesgosra.app.perfil.model.PerfilDemostracionRepository

class PerfilPresenter(
    private val view: PerfilView,
    private val repository: PerfilDemostracionRepository
) {

    fun cargarPerfil() {
        view.mostrarPerfil(repository.obtenerPerfil())
    }
}
