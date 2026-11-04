package com.riesgosra.app.riesgos.model

interface RiesgosRepository {
    fun obtenerRiesgos(alExito: (List<Riesgo>) -> Unit, alError: () -> Unit)
    fun cancelar()
}
