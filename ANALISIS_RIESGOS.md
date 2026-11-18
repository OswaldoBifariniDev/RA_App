# Riesgos y análisis fotográfico

La versión actual muestra ilustraciones y textos breves en Análisis, Plan de acción y Consejos de cada riesgo. Las tres secciones se despliegan dentro de la pantalla del riesgo seleccionado. El catálogo tiene respaldo local para que un fallo de la API no deje la pantalla vacía.

El análisis fotográfico consulta Gemini directamente por HTTPS desde Android. Tomar o elegir una foto inicia el análisis automáticamente. El resultado contiene una descripción breve e indicaciones numeradas. No necesita USB, servidor local ni computadora encendida; sí necesita Internet.

## Compilar sin publicar credenciales

La clave no está en el repositorio. El responsable de cada compilación debe aportar una clave válida mediante la variable de entorno `RIESGORA_GEMINI_API_KEY`. No pegar el valor en archivos versionados, incidencias ni documentación. Gradle también admite la configuración privada de Windows existente, protegida con DPAPI para el mismo usuario y equipo.

Para crear esa configuración local en Windows, ejecutar desde la raíz:

```powershell
powershell.exe -NoProfile -ExecutionPolicy RemoteSigned -File backend_analisis/configurar_backend.ps1
```

El script solicita el valor mediante entrada oculta y lo guarda en `backend_analisis/.local`, excluido de Git. Después abrir la raíz que contiene `settings.gradle.kts` en Android Studio y ejecutar el módulo `app`, o compilar con `gradlew.bat :app:assembleDebug` usando JDK 17.

**Esta APK de pruebas incorpora la credencial durante la compilación.** Puede extraerse del instalador: no publicar APK, DEX, clases compiladas, cachés ni carpetas build en repositorios o releases públicos. No es un mecanismo de protección de una clave compartida para distribución general. En ese caso se debe migrar el acceso a un servicio administrado.

Los archivos del antiguo servicio de análisis local permanecen en `backend_analisis` como respaldo. La app actual no los invoca ni inicia ese servicio al compilar. `leer_clave_compilacion.ps1` solo entrega la configuración privada al proceso de compilación; no debe ejecutarse para mostrar su salida en una consola o registro compartido.

## Datos revisados

El catálogo local incluye la revisión del 17 de noviembre: consejo de incendios combinado, protección de cabeza y cuello durante sismos, alternativa si no hay refugio resistente cercano y aviso a un docente ante inundaciones. Las ilustraciones se distribuyen con la app.

La base local `Registro_Riesgo`, tabla `public.informacion_riesgos`, ya fue actualizada. La copia publicada en Render todavía requiere sincronizar esa revisión. La migración `database/20261117_actualizar_catalogo.sql` actualiza solo los tres riesgos por su nombre, conservando IDs. Confirmar el esquema del despliegue antes de aplicarla y conservar un respaldo.

Mientras se sincroniza, el repositorio Android reemplaza únicamente los textos anteriores conocidos con la revisión exportada de la base local. Conserva otros textos nuevos del servidor e identifica el catálogo pendiente de sincronizar.

## Validación realizada

Compilación Debug correcta; instalación en teléfono y emulador; pantalla sin formulario de clave; cámara habilitada desde el primer acceso; captura y selección de imágenes con análisis automático real desde Gemini. Se conservan inicio de sesión, registro, navegación inferior y flecha de regreso. Los límites de 2.048 tokens de entrada y 1.024 de salida son controles del cliente, no un cupo diario por usuario.
