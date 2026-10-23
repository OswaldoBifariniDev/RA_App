package com.riesgosra.app.perfil.model

/** Datos ficticios para la vista de demostración. */
class PerfilDemostracionRepository {

    fun obtenerPerfil(): PerfilUsuario {
        return PerfilUsuario(
            nombreMostrar = "Ana Pérez",
            nombresCompletos = "Ana María Pérez Torres",
            correoElectronico = "ana.perez@example.com",
            descripcion = "Me gusta aprender cosas nuevas, afrontar nuevos retos y formar parte de proyectos que generen un impacto positivo."
        )
    }
}