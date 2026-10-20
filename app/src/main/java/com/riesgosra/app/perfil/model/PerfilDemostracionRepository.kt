package com.riesgosra.app.perfil.model

/** Datos ficticios para la demostración; no representan a la sesión autenticada. */
class PerfilDemostracionRepository {

    fun obtenerPerfil(): PerfilUsuario {
        return PerfilUsuario(
            nombre = "Ana",
            apellido = "Pérez",
            correoElectronico = "ana.perez@example.com"
        )
    }
}
