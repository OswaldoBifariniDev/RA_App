package com.example.ra_app.remote.risk

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class GeminiRiskServiceTest {
    private class FakeApi(private val codes: List<Int>) : GeminiRetrofitApi {
        var calls = 0
        override suspend fun generateContent(apiKey: String, request: GeminiRequest): Response<GeminiResponse> {
            val code = codes[calls++]
            return if (code == 200) Response.success(GeminiResponse(listOf(GeminiCandidate(
                GeminiContentResponse(listOf(GeminiPartResponse("""{"riesgo":"inundacion","confianza":0.8,"descripcion":"Agua en la vía","sugerencias":["Evitar el agua"]}""")))
            )))) else Response.error(code, "{}".toResponseBody("application/json".toMediaType()))
        }
    }
    @Test fun seRecuperaDeUn503TemporalConEspera() = runBlocking {
        val api = FakeApi(listOf(503, 200)); val waits = mutableListOf<Long>()
        val result = GeminiRiskService(api, { "test-key" }, { waits.add(it) }).analyzeImage("test-image")
        assertTrue(result.isSuccess); assertEquals("inundacion",result.getOrThrow().riesgo)
        assertEquals(2,api.calls); assertEquals(listOf(1000L),waits)
    }
    @Test fun limitaLosReintentosYConservaElErrorReal() = runBlocking {
        val api = FakeApi(listOf(503,503,503)); val waits = mutableListOf<Long>()
        val result = GeminiRiskService(api,{ "test-key" },{ waits.add(it) }).analyzeImage("test-image")
        assertTrue(result.isFailure);assertEquals(3,api.calls)
        assertEquals(listOf(1000L,2000L),waits)
        assertEquals(503,(result.exceptionOrNull() as RiskServiceException).httpCode)
    }
    @Test fun noReintentaUnaClaveRechazada() = runBlocking {
        val api = FakeApi(listOf(403))
        val result = GeminiRiskService(api,{ "test-key" },{}).analyzeImage("test-image")
        assertEquals(1,api.calls);assertFalse((result.exceptionOrNull() as RiskServiceException).canRetry)
    }
    @Test(expected = CancellationException::class) fun respetaLaCancelacionDuranteLaEspera() = runBlocking {
        val api = FakeApi(listOf(503))
        GeminiRiskService(api,{ "test-key" },{ throw CancellationException() }).analyzeImage("test-image")
        Unit
    }
}
