# Respaldo real RegistroRiesgos.sql y consejos ilustrados

Fuente utilizada: RegistroRiesgos.sql en la carpeta indicada por el usuario, sin el guion bajo del archivo anterior. El original no se modifico; su SHA-256 y los detalles de restauracion estan en backend/RegistroRiesgos_importacion.json.

Se restauro el respaldo completo en una base separada llamada registro_riesgos, en PostgreSQL local del puerto 5433. Contiene informacion_riesgos (3 filas), riesgos (3), preguntas_simulacro (9) y resultados_simulacro (0). Se conservaron IDs, secuencias, claves, restricciones y relaciones del respaldo. Solo se omitieron los cambios de propietario y permisos web_anon que dependian de roles de la instalacion original.

La autenticacion sigue usando riesgos_ra. La base anterior se conservo. El endpoint obtener_riesgos.php ahora conecta el catalogo a registro_riesgos/public.informacion_riesgos, la tabla que referencian las preguntas y los resultados del respaldo. Los IDs son 1 Incendios, 2 Sismo y 3 Inundaciones. El otro catalogo, public.riesgos, tambien se restauro sin modificarlo.

Los textos originales siguen completos en PostgreSQL. Para presentarlos en pantalla se separan las acciones de cada consejo en 4 de incendio, 4 de sismo y 3 de inundacion. Se conservan las introducciones condicionales junto a su accion (por ejemplo, Si detectas humo o fuego, avisa inmediatamente a un profesor). Cada accion tiene su propio pictograma vectorial pequeno, de trazo sencillo y color discreto. No se generan imagenes por red.

Verificado: restauracion de las cuatro tablas y sus relaciones, integridad del archivo original, API HTTP 200 con los IDs originales desde el computador y desde el telefono, compilacion y 20 pruebas unitarias correctas. El APK actualizado fue instalado en el TECNO. La navegacion visual no se pudo comprobar de forma estable durante esta ejecucion; los datos recibidos y la separacion/iconos estan verificados por las pruebas.

Para usar: abrir Riesgos, entrar en Informacion y desplegar Consejos para estudiantes. La informacion permanece en la misma pantalla. Para reiniciar el entorno usar Iniciar_Proyecto.cmd con el telefono conectado a la misma Wi-Fi y autorizado por USB.
