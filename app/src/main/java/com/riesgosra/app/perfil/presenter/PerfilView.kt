package com.riesgosra.app.perfil.presenter

import com.riesgosra.app.perfil.model.PerfilUsuario

interface PerfilView {
    fun mostrarPerfil(perfil: PerfilUsuario)
}
