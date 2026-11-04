package com.riesgosra.app.rutasegura.presenter

import com.riesgosra.app.rutasegura.model.EscenarioRuta
import com.riesgosra.app.rutasegura.model.RutaSeguraModelo

/**
 * Presentador de la funcionalidad Ruta segura.
 */
class RutaSeguraPresenter(
    private val vista: RutaSeguraContrato.Vista,
    private val modelo: RutaSeguraModelo
) : RutaSeguraContrato.Presentador {

    private var escenarioSeleccionado =
        modelo.obtenerEscenarioInicial()

    init {
        vista.mostrarEscenarioSeleccionado(
            escenarioSeleccionado
        )
    }

    override fun alSeleccionarEscenario(
        escenario: EscenarioRuta
    ) {
        if (escenarioSeleccionado == escenario) return

        escenarioSeleccionado = escenario

        vista.mostrarEscenarioSeleccionado(
            escenarioSeleccionado
        )
    }

    override fun alPulsarActivarCamara() {

        if (modelo.tienePermisoCamara()) {
            vista.abrirCamara(escenarioSeleccionado)
            return
        }

        if (vista.debeMostrarJustificacionPermiso()) {
            vista.mostrarExplicacionPermiso()
        } else {
            vista.solicitarPermisoCamara()
        }
    }

    override fun alConfirmarExplicacionPermiso() {
        vista.solicitarPermisoCamara()
    }

    override fun alResultadoPermisoCamara(
        concedido: Boolean,
        puedeSolicitarNuevamente: Boolean
    ) {
        if (concedido) {
            vista.abrirCamara(escenarioSeleccionado)
        } else {
            vista.mostrarPermisoDenegado(
                puedeSolicitarNuevamente
            )
        }
    }
}