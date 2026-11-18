package com.riesgosra.app.analisis.presenter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.riesgosra.app.analisis.model.GeminiAnalisisRepository
import java.io.File
import java.net.SocketTimeoutException
import java.util.concurrent.Executors

class AnalisisPresenter : ViewModel() {
    private val repositorio = GeminiAnalisisRepository()
    private val ejecutor = Executors.newSingleThreadExecutor()
    private val principal = Handler(Looper.getMainLooper())
    private var vista: AnalisisView? = null
    @Volatile private var cerrado = false
    private var foto: Bitmap? = null
    private var ocupado = false
    private var resultado = ""
    private var error: String? = null
    val tokensUltimoAnalisis: Int get() = repositorio.tokensUltimoAnalisis

    fun adjuntar(vista: AnalisisView) {
        this.vista = vista
        actualizar()
    }

    fun separar() { vista = null }

    fun cargarFoto(archivo: File) {
        if (ocupado) return
        ocupado = true
        resultado = ""
        error = null
        actualizar()
        ejecutor.execute {
            val carga = runCatching {
                val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(archivo.path, limites)
                check(limites.outWidth > 0 && limites.outHeight > 0) { "La cámara no devolvió una fotografía válida." }
                var muestra = 1
                while (maxOf(limites.outWidth, limites.outHeight) / muestra > 1600) muestra *= 2
                val imagen = BitmapFactory.decodeFile(archivo.path, BitmapFactory.Options().apply {
                    inSampleSize = muestra
                }) ?: error("No se pudo abrir la fotografía.")
                val orientacion = ExifInterface(archivo.path).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val matriz = Matrix().apply {
                    when (orientacion) {
                        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
                        ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
                        ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
                        ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
                        ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(270f); postScale(-1f, 1f) }
                        ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(270f)
                    }
                }
                Bitmap.createBitmap(imagen, 0, 0, imagen.width, imagen.height, matriz, true)
            }
            archivo.delete()
            principal.post {
                if (!cerrado) {
                    ocupado = false
                    carga.fold(
                        onSuccess = {
                            foto = it
                            analizar()
                        },
                        onFailure = {
                            foto = null
                            error = "No se pudo leer la fotografía. Vuelve a tomarla desde un lugar seguro."
                            actualizar()
                        }
                    )
                }
            }
        }
    }

    fun analizar() {
        if (ocupado) return
        val imagen = foto
        if (imagen == null) {
            error = "Toma o elige una fotografía antes de analizar."
            actualizar()
            return
        }
        ocupado = true
        resultado = ""
        error = null
        actualizar()
        ejecutor.execute {
            val analisis = runCatching { repositorio.analizar(imagen) }
            principal.post {
                if (!cerrado) {
                    ocupado = false
                    analisis.fold(
                        onSuccess = { resultado = it },
                        onFailure = {
                            error = when (it) {
                                is IllegalStateException -> it.message
                                is SocketTimeoutException -> "Gemini tardó demasiado. Revisa la conexión y vuelve a intentarlo."
                                else -> "No se pudo conectar con Gemini. Comprueba el Wi-Fi o los datos móviles y vuelve a intentar."
                            }
                        }
                    )
                    actualizar()
                }
            }
        }
    }

    private fun actualizar() { vista?.mostrarEstado(foto, ocupado, resultado, error) }

    override fun onCleared() {
        cerrado = true
        vista = null
        foto = null
        repositorio.cerrar()
        ejecutor.shutdownNow()
        principal.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}
