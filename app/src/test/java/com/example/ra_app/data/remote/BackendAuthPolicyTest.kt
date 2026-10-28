package com.example.ra_app.data.remote
import org.junit.Assert.*
import org.junit.Test

class BackendAuthPolicyTest {
    @Test fun autenticaRutasEnRaizYEnCarpetaDeProduccion() {
        listOf("/", "/riesgos_ra_api_postgresql/", "/api/v2/").forEach { base ->
            listOf("ruta_protegida.php", "ruta_crear.php", "perfil.php").forEach {
                assertTrue(base + it, BackendAuthPolicy.requiresToken(base + it))
            }
        }
    }
    @Test fun noAdjuntaSesionALoginRegistroNiGemini() {
        listOf("/login.php", "/riesgos_ra_api_postgresql/registrar.php", "/prueba_conexion.php",
            "/v1beta/models/modelo:generateContent", "/perfil.php/otra").forEach {
            assertFalse(it, BackendAuthPolicy.requiresToken(it))
        }
    }
}
