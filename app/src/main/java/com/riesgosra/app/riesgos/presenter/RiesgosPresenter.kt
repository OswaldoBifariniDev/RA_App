package com.riesgosra.app.riesgos.presenter

import com.riesgosra.app.riesgos.model.Riesgo
import com.riesgosra.app.riesgos.model.RiesgosRepository
import com.riesgosra.app.riesgos.model.TemaRiesgo

class RiesgosPresenter(
    private var vista: RiesgosContract.View?,
    private val repositorio: RiesgosRepository,
    idRiesgoInicial: Int? = null,
    temaInicial: TemaRiesgo? = null
) {
    private var riesgos = emptyList<Riesgo>()
    private var cargando = false
    var idRiesgoSeleccionado: Int? = idRiesgoInicial
        private set
    var temaSeleccionado: TemaRiesgo? = temaInicial
        private set

    fun cargarRiesgos() {
        if (cargando) return
        cargando = true
        vista?.mostrarCarga()
        repositorio.obtenerRiesgos({ registros ->
            cargando = false
            riesgos = registros
            presentar()
        }, {
            cargando = false
            vista?.mostrarError()
        })
    }

    fun seleccionarRiesgo(idRiesgo: Int) {
        if (riesgos.none { it.idRiesgo == idRiesgo }) return
        idRiesgoSeleccionado = idRiesgo
        temaSeleccionado = null
        presentar()
    }

    fun seleccionarTema(tema: TemaRiesgo) {
        if (idRiesgoSeleccionado == null) return
        temaSeleccionado = tema
        presentar()
    }

    fun volver(): Boolean {
        if (temaSeleccionado != null) {
            temaSeleccionado = null
        } else if (idRiesgoSeleccionado != null) {
            idRiesgoSeleccionado = null
        } else {
            return false
        }
        if (riesgos.isNotEmpty()) presentar()
        return true
    }

    private fun presentar() {
        val riesgo = riesgos.find { it.idRiesgo == idRiesgoSeleccionado }
        if (riesgo == null) {
            idRiesgoSeleccionado = null
            temaSeleccionado = null
            vista?.mostrarRiesgos(riesgos)
            return
        }
        val tema = temaSeleccionado
        if (tema == null) {
            vista?.mostrarTemas(riesgo)
        } else {
            val informacion = when (tema) {
                TemaRiesgo.ANALISIS -> riesgo.analisisRiesgo
                TemaRiesgo.PLAN_ACCION -> riesgo.planAccion
                TemaRiesgo.CONSEJOS -> riesgo.consejosEstudiante
            }
            vista?.mostrarInformacion(riesgo, tema, informacion)
        }
    }

    fun destruir() {
        vista = null
        repositorio.cancelar()
    }
}
