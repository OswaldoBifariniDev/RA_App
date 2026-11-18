package com.riesgosra.app.riesgos.model

interface RiesgosRepository {
    val usandoDatosGuardados: Boolean get() = false
    val usandoCatalogoIncluido: Boolean get() = false
    fun obtenerRiesgos(alExito: (List<Riesgo>) -> Unit, alError: () -> Unit)
    fun cancelar()
}
