package com.riesgosra.app.riesgos.model

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName

/** Contrato real de gestion_riesgo/obtener_riesgos.php. */
class RiesgosJsonParser(private val gson: Gson = Gson()) {
    fun parse(json: String): List<Riesgo> {
        val records = gson.fromJson(json, Array<RiesgoRegistro?>::class.java)
            ?: throw JsonParseException("Se esperaba una lista de riesgos")
        return records.map { item ->
            val row = item ?: throw JsonParseException("Registro de riesgo vacío")
            val id = row.id ?: throw JsonParseException("Falta id_riesgo")
            val name = row.nombre?.takeIf { it.isNotBlank() }
                ?: throw JsonParseException("Falta nombre del riesgo")
            Riesgo(id, name, row.analisis.orEmpty(), row.plan.orEmpty(), row.consejos.orEmpty())
        }
    }

    private data class RiesgoRegistro(
        @SerializedName("id_riesgo") val id: Int?,
        @SerializedName("nombre") val nombre: String?,
        @SerializedName("analisis_riesgo") val analisis: String?,
        @SerializedName("plan_accion") val plan: String?,
        @SerializedName("consejos_estudiante") val consejos: String?
    )
}
