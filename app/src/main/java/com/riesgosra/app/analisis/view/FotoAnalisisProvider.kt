package com.riesgosra.app.analisis.view

import android.content.ContentProvider
import android.content.ContentValues
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File
import java.io.FileNotFoundException

// Comparte únicamente las capturas temporales de este módulo con la aplicación de cámara.
class FotoAnalisisProvider : ContentProvider() {
    override fun onCreate() = true

    private fun archivo(uri: Uri): File {
        val nombre = uri.lastPathSegment.orEmpty()
        if (uri.pathSegments.size != 1 || !nombre.matches(Regex("analisis_[a-f0-9-]{36}\\.jpg"))) {
            throw FileNotFoundException("Fotografía no válida")
        }
        return File(requireNotNull(context).cacheDir, nombre)
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor =
        ParcelFileDescriptor.open(archivo(uri), ParcelFileDescriptor.parseMode(mode))

    override fun getType(uri: Uri): String { archivo(uri); return "image/jpeg" }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?): MatrixCursor {
        val foto = archivo(uri)
        val columnas = projection ?: arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        return MatrixCursor(columnas).apply {
            addRow(columnas.map {
                when (it) {
                    OpenableColumns.DISPLAY_NAME -> foto.name
                    OpenableColumns.SIZE -> foto.length()
                    else -> null
                }
            })
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = throw UnsupportedOperationException()
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
}
