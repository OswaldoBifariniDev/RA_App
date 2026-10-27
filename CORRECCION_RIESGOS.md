# Corrección de la pantalla Riesgos

La pantalla consultaba /api/riesgos en el servidor de autenticación del puerto 8000. Esa ruta no existe y devolvía HTTP 404. El repositorio mostraba cualquier fallo HTTP o de JSON como Sin conexión.

Los registros existentes están disponibles en Apache, en http://192.168.33.221/gestion_riesgo/obtener_riesgos.php. Ese servicio devuelve id_riesgo, nombre, analisis_riesgo, plan_accion y consejos_estudiante; la app esperaba id, analisis, planAccion y consejos. Ambos problemas impedían mostrar la información.

Se añadió riesgosCatalogoUrl en gradle.properties y el campo RIESGOS_CATALOGO_URL en BuildConfig. El catálogo usa su dirección propia; el login mantiene su API PHP/PostgreSQL del puerto 8000. RiesgosJsonParser traduce los campos del servicio real y acepta los identificadores devueltos como texto. La pantalla distingue errores de servidor, formato y conexión. No se modificaron los registros ni se copiaron entre bases.

Verificación: compilación correcta, seis pruebas unitarias correctas y APK actualizado instalado en el TECNO KJ6. En la interfaz del teléfono se inició sesión con la cuenta demo, se mostraron los tres riesgos y se comprobaron el análisis, el plan de acción y los consejos de Prevención de Incendio. Se dejó abierta la lista de Riesgos con los tres registros.

Si cambia la IP del computador hay que actualizar tanto riesgosApiUrl (autenticación) como riesgosCatalogoUrl (catálogo) en gradle.properties, además de la configuración local del backend. El catálogo requiere que Apache y su base de datos estén activos.
