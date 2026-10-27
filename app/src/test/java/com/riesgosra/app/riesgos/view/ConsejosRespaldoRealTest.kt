package com.riesgosra.app.riesgos.view
import com.riesgosra.app.R
import com.riesgosra.app.riesgos.model.RiesgosJsonParser
import org.junit.Assert.*
import org.junit.Test

class ConsejosRespaldoRealTest {
    private fun registros() = RiesgosJsonParser().parse(javaClass.getResource("/registro_riesgos_real.json")!!.readText())
    @Test fun incendioConservaLaCondicionYSeparaCuatroAcciones() {
        val row = registros().single { it.idRiesgo == 1 }
        val tips = RiesgoVisuals.consejos(row.consejosEstudiante)
        assertEquals(4,tips.size)
        assertEquals("Si detectas humo o fuego, avisa inmediatamente a un profesor, ",tips[0])
        assertEquals(listOf(R.drawable.img_consejo_avisar,R.drawable.escena_consejo_calma,R.drawable.img_consejo_caminar,R.drawable.escena_consejo_evacuacion),tips.map { RiesgoVisuals.ilustracion(row.nombre,it) })
    }
    @Test fun sismoTieneUnaIlustracionParaCadaAccion() {
        val row = registros().single { it.idRiesgo == 2 }
        val tips = RiesgoVisuals.consejos(row.consejosEstudiante)
        assertEquals(4,tips.size)
        assertTrue(tips[0].startsWith("Durante un sismo, busca protección"))
        assertEquals(listOf(R.drawable.img_consejo_proteger_mesa,R.drawable.img_consejo_proteger_cabeza,R.drawable.escena_consejo_calma,R.drawable.img_consejo_indicaciones),tips.map { RiesgoVisuals.ilustracion(row.nombre,it) })
    }
    @Test fun inundacionTieneTresConsejosConSusPictogramas() {
        val row = registros().single { it.idRiesgo == 3 }
        val tips = RiesgoVisuals.consejos(row.consejosEstudiante)
        assertEquals(3,tips.size)
        assertEquals(listOf(R.drawable.escena_consejo_inundacion,R.drawable.img_consejo_electricidad,R.drawable.img_consejo_avisar),tips.map { RiesgoVisuals.ilustracion(row.nombre,it) })
    }
    @Test fun conservaLiteralmenteElTextoDeLaBaseConPuntuacionYConectores() {
        registros().forEach { row ->
            assertEquals(row.consejosEstudiante, RiesgoVisuals.consejos(row.consejosEstudiante).joinToString(""))
        }
    }
}
