package com.riesgosra.app.riesgos.view

import com.riesgosra.app.R
import com.riesgosra.app.riesgos.model.TemaRiesgo
import java.text.Normalizer
import java.util.Locale

object RiesgoVisuals {
    private fun normalizar(text: String) = Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").lowercase(Locale.ROOT)
    private fun tipo(nombre: String): String = when {
        normalizar(nombre).contains("incend") -> "incendio"
        normalizar(nombre).contains("sism") -> "sismo"
        normalizar(nombre).contains("inund") -> "inundacion"
        else -> "otro"
    }
    fun icono(nombre: String, tema: TemaRiesgo = TemaRiesgo.ANALISIS): Int = when (tipo(nombre)) {
        "incendio" -> when (tema) {
            TemaRiesgo.ANALISIS -> R.drawable.ic_riesgo_incendio
            TemaRiesgo.PLAN_ACCION -> R.drawable.ic_plan_incendio
            TemaRiesgo.CONSEJOS -> R.drawable.ic_consejo_incendio
        }
        "sismo" -> when (tema) {
            TemaRiesgo.ANALISIS -> R.drawable.ic_riesgo_sismo
            TemaRiesgo.PLAN_ACCION -> R.drawable.ic_plan_sismo
            TemaRiesgo.CONSEJOS -> R.drawable.ic_consejo_sismo
        }
        "inundacion" -> when (tema) {
            TemaRiesgo.ANALISIS -> R.drawable.ic_riesgo_inundacion
            TemaRiesgo.PLAN_ACCION -> R.drawable.ic_plan_inundacion
            TemaRiesgo.CONSEJOS -> R.drawable.ic_consejo_inundacion
        }
        else -> R.drawable.ic_informacion
    }
    fun consejos(texto: String): List<String> {
        val separador = Regex("""\r?\n|,\s+(?=(?:mant[eé]n|mantente|no\s+corras|sigue|seguir|protege|comunica|alejarse|dirigirse)\b)|\s+y\s+(?=(?:sigue|seguir|comunica|mant[eé]n|mantente|alejarse|dirigirse)\b)""", RegexOption.IGNORE_CASE)
        if (texto.isBlank()) return emptyList()
        val partes = mutableListOf<String>()
        var inicio = 0
        separador.findAll(texto).forEach { coincidencia ->
            // Conservar todos los caracteres: la coma queda con la frase anterior,
            // y la conjunción "y" permanece al inicio de la siguiente.
            val conector = coincidencia.value.indexOfFirst { !it.isWhitespace() }
            val corte = if (conector >= 0 && coincidencia.value[conector].equals('y', ignoreCase = true))
                coincidencia.range.first + conector else coincidencia.range.last + 1
            val fragmento = texto.substring(inicio, corte)
            if (fragmento.isNotBlank()) {
                partes.add(fragmento)
                inicio = corte
            }
        }
        val resto = texto.substring(inicio)
        if (resto.isNotBlank()) partes.add(resto)
        else if (partes.isNotEmpty()) partes[partes.lastIndex] += resto
        return partes
    }

    // Dividir solo acciones, no inventar consejos ni separar "objetos que puedan caer".
    // La puntuacion y el conector de una enumeracion no forman parte del titulo.
    fun consejosParaMostrar(texto: String): List<String> = consejos(texto).map { parte ->
        parte.trim().removePrefix("y ").removePrefix("Y ").trimEnd(',').trim()
            .replaceFirstChar { it.titlecase(Locale.forLanguageTag("es")) }
    }

    fun ilustracion(nombre: String, consejo: String): Int {
        val text = normalizar(consejo)
        return when {
            text.contains("avisa") || text.contains("comunica") -> R.drawable.img_consejo_avisar
            text.contains("no corras") -> R.drawable.img_consejo_caminar
            text.contains("mesa") || text.contains("escritorio") -> R.drawable.img_consejo_proteger_mesa
            text.contains("protege") && text.contains("cabeza") -> R.drawable.img_consejo_proteger_cabeza
            text.contains("cable") || text.contains("electric") -> R.drawable.img_consejo_electricidad
            text.contains("indicaciones") || text.contains("personal responsable") -> R.drawable.img_consejo_indicaciones
            text.contains("calma") -> R.drawable.escena_consejo_calma
            text.contains("fuego") -> R.drawable.escena_consejo_fuego
            text.contains("evacu") || text.contains("salida") -> R.drawable.escena_consejo_evacuacion
            text.contains("ventana") || text.contains("caer") -> R.drawable.escena_consejo_ventanas
            text.contains("inundad") || text.contains("agua") -> R.drawable.escena_consejo_inundacion
            text.contains("segur") && tipo(nombre) == "inundacion" -> R.drawable.escena_consejo_lugar_seguro
            text.contains("segur") -> R.drawable.escena_consejo_zona_segura
            else -> R.drawable.img_consejo_indicaciones
        }
    }
}
