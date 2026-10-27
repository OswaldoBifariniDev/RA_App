# Catalogo de Riesgos conectado a PostgreSQL

Causas encontradas: la app apuntaba a la IP de otra red (192.168.1.112) y seguia consultando el catalogo MySQL de XAMPP. El archivo Registro_Riesgo.sql incluye un borrador invalido al principio (VALUES sin INSERT y una cadena sin cerrar), un DROP TABLE y un bloque corregido al final. La tabla informacion_riesgos no existia en el PostgreSQL utilizado por esta API.

Se importo solamente la estructura y los tres registros del bloque final corregido en public.informacion_riesgos, dentro de riesgos_ra en la instancia local de pruebas del puerto 5433. No se modifico la instancia PostgreSQL del puerto 5432 ni el archivo SQL original. No se ejecuto DROP TABLE. La importacion preparada en backend/04_catalogo_riesgos_postgresql.sql es repetible: crea la tabla si falta e inserta solo nombres inexistentes. Se verifico una segunda ejecucion sin duplicados.

El endpoint backend/api/obtener_riesgos.php consulta PostgreSQL mediante PDO y entrega los campos que consume la app. Tanto riesgosApiUrl como riesgosCatalogoUrl usan la misma API en el puerto 8000; el catalogo ya no depende de Apache/MySQL. La direccion actual es http://192.168.33.221:8000/obtener_riesgos.php. Se actualizaron la regla local del firewall, el APK y el script Iniciar_Proyecto.cmd.

Verificacion: respuesta HTTP 200 con los tres registros desde el computador y desde el telefono TECNO por Wi-Fi; importacion sin duplicados; compilacion y 17 pruebas unitarias correctas, incluida lectura de los registros reales de PostgreSQL. El APK actualizado fue instalado por USB. La comprobacion final confirma recepcion de los datos desde el telefono; no se completo una comprobacion visual estable de la pantalla durante esta ejecucion.

Para volver a usarlo despues de reiniciar o cambiar de Wi-Fi: conectar ambos equipos a la misma red y el telefono por USB, ejecutar Iniciar_Proyecto.cmd y aceptar el permiso de Windows si necesita actualizar la regla de red. Despues abrir Riesgos y pulsar Volver a cargar.
