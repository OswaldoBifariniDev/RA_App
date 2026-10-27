# Activar Análisis de Riesgos

La app no tenía una clave Gemini configurada. Se corrigió el cliente, se configuró el modelo gemini-2.5-flash (ajustable con geminiModel), se envía la clave mediante x-goog-api-key y se desactivó el registro de fotografías/credenciales. Falta una clave válida de tu cuenta para probar una imagen con Google.

1. Abrir https://aistudio.google.com/api-keys e iniciar sesión en tu cuenta de Google.
2. Elegir Crear clave de API / Create API key y el proyecto que corresponda. Si Google solicita aceptar condiciones o configurar un proyecto, completarlo en tu cuenta.
3. Copiar la clave y ejecutar Configurar_Gemini.cmd, incluido en esta carpeta de resultados y en la raíz del proyecto.
4. Pegarla en la ventana del asistente: la entrada permanece oculta. El asistente valida acceso al modelo, guarda la clave en local.properties, compila y actualiza el teléfono USB si hay uno conectado.

No pegar la clave en el chat. local.properties ya está excluido de Git. Esta configuración directa desde Android es para pruebas; antes de distribuir la app, la credencial debe gestionarse desde un servidor.

La pestaña Simulacros se sustituyó por Análisis de Riesgos y ahora reúne cámara, galería e historial. Se quitó el botón de análisis del inicio. Se corrigieron textos con codificación dañada, se diferenciaron fallos de configuración y del servicio y los errores ya no se guardan como análisis sin riesgo.

Documentación de Google: https://ai.google.dev/gemini-api/docs/api-key y https://ai.google.dev/gemini-api/docs/models/gemini-2.5-flash.
