package com.riesgosra.app.riesgos.view
import com.riesgosra.app.riesgos.model.RiesgosJsonParser
import org.junit.Assert.*
import org.junit.Test
class ConsejosConfirmadosTest {
    @Test fun muestraSoloLosOchoConsejosConfirmadosDelRespaldo() {
        val rows = RiesgosJsonParser().parse(javaClass.getResource("/consejos_confirmados.json")!!.readText())
        val expected = mapOf(
            "Prevención de Sismo" to listOf("Mantener la calma", "Alejarse de ventanas y objetos que puedan caer", "Dirigirse a una zona segura."),
            "Prevención de Incendio" to listOf("Mantener la calma", "Alejarse del fuego", "Seguir las rutas de evacuación."),
            "Prevención de Inundación" to listOf("Evitar zonas inundadas", "Dirigirse a lugares seguros.")
        )
        rows.forEach { row ->
            val tips = RiesgoVisuals.consejosParaMostrar(row.consejosEstudiante)
            assertEquals(expected.getValue(row.nombre), tips)
            assertEquals(tips.size, tips.map { RiesgoVisuals.ilustracion(row.nombre, it) }.distinct().size)
        }
        assertEquals(8, rows.sumOf { RiesgoVisuals.consejosParaMostrar(it.consejosEstudiante).size })
    }
}
