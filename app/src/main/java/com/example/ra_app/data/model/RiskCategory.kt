package com.example.ra_app.data.model

import com.riesgosra.app.R

enum class RiskCategory(
    val key: String,
    val displayName: String,
    val colorResId: Int
) {
    ELECTRICO("electrico", "Riesgo Eléctrico", R.color.riesgo_electrico),
    ESTRUCTURAL("estructural", "Riesgo Estructural", R.color.riesgo_estructural),
    QUIMICO("quimico", "Riesgo Químico", R.color.riesgo_quimico),
    OBSTRUCCION("obstruccion", "Obstrucción de Vía", R.color.riesgo_obstruccion),
    INCENDIO("incendio", "Riesgo de incendio", R.color.riesgo_incendio),
    SISMO("sismo", "Riesgo por sismo", R.color.riesgo_sismo),
    INUNDACION("inundacion", "Riesgo de inundación", R.color.riesgo_inundacion),
    NO_EVALUABLE("no_evaluable", "Imagen no evaluable", R.color.riesgo_no_evaluable),
    SIN_RIESGO("sin_riesgo", "Sin riesgo visible en la foto", R.color.riesgo_sin_riesgo);

    companion object {
        fun fromKey(key: String?): RiskCategory {
            if (key == null) return SIN_RIESGO
            val normalized = key.lowercase().trim()
            return entries.find { it.key == normalized } ?: SIN_RIESGO
        }
    }
}
