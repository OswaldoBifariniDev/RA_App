package com.riesgosra.app.riesgos.model

data class Riesgo(
    val idRiesgo: Int,
    val nombre: String,
    val analisisRiesgo: String,
    val planAccion: String,
    val consejosEstudiante: String
)
