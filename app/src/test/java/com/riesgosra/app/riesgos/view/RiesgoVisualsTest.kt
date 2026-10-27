package com.riesgosra.app.riesgos.view
import com.riesgosra.app.R
import org.junit.Assert.*
import org.junit.Test
class RiesgoVisualsTest {
    @Test fun cadaConsejoDelTextoTieneSuPropiaIlustracion() {
        val consejos = RiesgoVisuals.consejos("Mantener la calma.\nAlejarse del fuego.\nSeguir las rutas de evacuación.")
        assertEquals(3,consejos.size)
        assertEquals(listOf(R.drawable.escena_consejo_calma,R.drawable.escena_consejo_fuego,R.drawable.escena_consejo_evacuacion),
            consejos.map { RiesgoVisuals.ilustracion("Prevención de Incendio",it) })
    }
    @Test fun asociaInundacionYSismoConSusAcciones() {
        assertEquals(R.drawable.escena_consejo_ventanas,RiesgoVisuals.ilustracion("Sismo","Alejarse de ventanas y objetos que puedan caer."))
        assertEquals(R.drawable.escena_consejo_lugar_seguro,RiesgoVisuals.ilustracion("Inundación","Dirigirse a lugares seguros."))
        assertEquals(R.drawable.escena_consejo_inundacion,RiesgoVisuals.ilustracion("Inundación","Evitar zonas inundadas."))
    }
}
