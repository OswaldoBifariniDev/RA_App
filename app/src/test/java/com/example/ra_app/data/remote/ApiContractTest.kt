package com.example.ra_app.data.remote
import com.example.ra_app.data.remote.dto.RutaProtegidaResponse
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test
import retrofit2.http.GET
import retrofit2.http.POST
class ApiContractTest {
    @Test fun onlyDeclaredBackendEndpointsAreUsed() {
        val paths = ApiService::class.java.declaredMethods.mapNotNull {
            it.getAnnotation(GET::class.java)?.value ?: it.getAnnotation(POST::class.java)?.value
        }.toSet()
        assertEquals(setOf("login.php", "ruta_protegida.php", "ruta_crear.php", "prueba_conexion.php"), paths)
    }
    @Test fun protectedResponseMapsRoutes() {
        val body = Gson().fromJson("""{"autorizado":true,"mensaje":"OK","rutas":[{"id":1,"nombre":"Salida","descripcion":"Norte"}]}""", RutaProtegidaResponse::class.java)
        assertTrue(body.autorizado)
        assertEquals("Salida", body.rutas!!.single().toDomain().nombre)
    }
    @Test fun missingRouteListIsNotTreatedAsEmptyCache() {
        val body = Gson().fromJson("""{"autorizado":true,"mensaje":"OK"}""", RutaProtegidaResponse::class.java)
        assertNull(body.rutas)
    }
}
