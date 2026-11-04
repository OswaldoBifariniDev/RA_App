package com.riesgosra.app.rutasegura.presenter

import com.riesgosra.app.rutasegura.model.EscenarioRuta

/**
 * Contrato MVP de la funcionalidad Ruta segura.
 */
interface RutaSeguraContrato {

    interface Vista {

        fun mostrarEscenarioSeleccionado(
            escenario: EscenarioRuta
        )

        fun solicitarPermisoCamara()

        fun mostrarExplicacionPermiso()

        fun debeMostrarJustificacionPermiso(): Boolean

        fun mostrarPermisoDenegado(
            puedeSolicitarNuevamente: Boolean
        )

        fun abrirCamara(
            escenario: EscenarioRuta
        )
    }

    interface Presentador {

        fun alSeleccionarEscenario(
            escenario: EscenarioRuta
        )

        fun alPulsarActivarCamara()

        fun alConfirmarExplicacionPermiso()

        fun alResultadoPermisoCamara(
            concedido: Boolean,
            puedeSolicitarNuevamente: Boolean
        )
    }
}