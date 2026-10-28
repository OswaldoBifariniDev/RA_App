package com.example.ra_app.data.remote

/** Funciona tanto en la raiz del servidor local como en la carpeta de produccion. */
object BackendAuthPolicy {
    fun requiresToken(path: String): Boolean = path.substringAfterLast('/') in
        setOf("ruta_protegida.php", "ruta_crear.php", "perfil.php")
}
