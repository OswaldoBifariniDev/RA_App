package com.riesgosra.app.riesgos.presenter

import com.riesgosra.app.riesgos.model.Riesgo
import com.riesgosra.app.riesgos.model.TemaRiesgo

interface RiesgosContract {
    interface View {
        fun mostrarCarga()
        fun mostrarRiesgos(riesgos: List<Riesgo>)
        fun mostrarTemas(riesgo: Riesgo)
        fun mostrarInformacion(riesgo: Riesgo, tema: TemaRiesgo, informacion: String)
        fun mostrarError()
    }
}
