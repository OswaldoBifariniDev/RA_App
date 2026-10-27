package com.example.ra_app.remote.risk
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class RiskResponseValidatorTest {
    @Test fun validaUnaRespuestaCompleta() {
        assertTrue(RiskResponseValidator.isValid(RiskJsonResponse("electrico", .8f, "Cable expuesto", listOf("Aislar el área"))))
    }
    @Test fun rechazaDatosAusentesONoValidos() {
        val incomplete = Gson().fromJson("""{"riesgo":"sin_riesgo","confianza":0.9}""", RiskJsonResponse::class.java)
        assertFalse(RiskResponseValidator.isValid(incomplete))
        assertFalse(RiskResponseValidator.isValid(RiskJsonResponse("inventado", 2f, "Error", listOf("Ejemplo"))))
    }
}
