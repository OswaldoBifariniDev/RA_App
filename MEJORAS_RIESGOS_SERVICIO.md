# Análisis de fotos e ilustraciones de riesgos

El aviso Servicio no disponible aparece cuando Gemini devuelve un error HTTP 5xx. Corresponde al proveedor de análisis de imágenes en Google, distinto del servidor local que valida el login. Una conexión correcta ayer no garantiza que el proveedor tenga disponibilidad en todas las solicitudes. El mensaje anterior no conservaba el código HTTP específico; no se puede afirmar cuál fue el código de la captura. La comprobación técnica de hoy con la clave guardada devolvió HTTP 200.

La actualización reintenta únicamente errores transitorios 500, 502, 503 y 504, hasta tres intentos totales y con pausas de 1 y 2 segundos. No repite automáticamente claves rechazadas ni límites de uso. Si sigue fallando, muestra un mensaje explícito y permite volver a intentar posteriormente. No se registra un error como un análisis sin riesgo. Esto mejora la recuperación ante fallos temporales; no garantiza la disponibilidad de Google.

El análisis por foto ahora contempla incendio, sismo e inundación, además de las categorías anteriores, y una categoría Imagen no evaluable. El resultado describe lo visible en una foto, no certifica la seguridad de un lugar.

En el módulo Riesgos, la lista y cada sección usan iconos propios de incendio, sismo o inundación. Cada consejo de estudiantes se presenta por separado, con una ilustración vectorial local de la acción y el texto original de la base. Las imágenes funcionan sin descargas adicionales y la información se sigue desplegando en la misma pantalla.

Documentación de Google sobre errores temporales y reintentos: https://ai.google.dev/gemini-api/docs/troubleshooting

Validación final: compilación correcta, 16 pruebas unitarias sin errores y APK instalado en el teléfono TECNO. Se verificaron en la interfaz los consejos separados y sus ilustraciones; se guardó una captura de los consejos de inundación. La comprobación técnica de Gemini devolvió HTTP 200. Una avería del proveedor aún puede requerir esperar y volver a intentar.
