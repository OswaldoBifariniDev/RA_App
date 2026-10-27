package com.riesgosra.app.riesgos.model
import com.google.gson.JsonParseException
import org.junit.Assert.*
import org.junit.Test

class RiesgosJsonParserTest {
    @Test fun cargaElContratoRealIncluyendoIdComoTextoYAcentos() {
        val json = javaClass.getResource("/riesgos_api.json")!!.readText()
        val registros = RiesgosJsonParser().parse(json)
        assertEquals(3, registros.size)
        assertEquals(1, registros.first().idRiesgo)
        assertEquals("Prevención de Incendio", registros.first().nombre)
        assertTrue(registros.first().analisisRiesgo.contains("fallas eléctricas"))
        assertTrue(registros.first().planAccion.contains("salidas de emergencia"))
        assertTrue(registros.first().consejosEstudiante.contains("Mantener la calma"))
    }
    @Test(expected = JsonParseException::class) fun rechazaErrorDelServidorComoLista() {
        RiesgosJsonParser().parse("""{"error":"base de datos no disponible"}""")
    }
    @Test(expected = JsonParseException::class) fun noOcultaUnContratoIncorrecto() {
        RiesgosJsonParser().parse("""[{"id":1,"nombre":"Incendio"}]""")
    }
    @Test fun leeLosRegistrosImportadosDePostgresql() {
        val json = javaClass.getResource("/riesgos_postgresql.json")!!.readText()
        val registros = RiesgosJsonParser().parse(json)
        assertEquals(3, registros.size)
        val incendio = registros.single { it.nombre == "Prevención de Incendios" }
        assertTrue(incendio.idRiesgo > 0)
        assertTrue(incendio.analisisRiesgo.contains("Un incendio escolar"))
        assertTrue(incendio.consejosEstudiante.contains("avisa inmediatamente a un profesor"))
        assertTrue(registros.any { it.nombre == "Prevención de Inundaciones" })
    }
}
