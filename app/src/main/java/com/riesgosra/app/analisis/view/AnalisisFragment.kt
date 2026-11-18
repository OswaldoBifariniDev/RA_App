package com.riesgosra.app.analisis.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.ContextThemeWrapper
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.riesgosra.app.analisis.presenter.AnalisisPresenter
import com.riesgosra.app.analisis.presenter.AnalisisView
import com.riesgosra.app.R
import java.io.File
import java.util.UUID

class AnalisisFragment : Fragment(), AnalisisView {
    private val presentador: AnalisisPresenter by activityViewModels()
    private var fotoPendiente: Uri? = null
    private var imagen: ImageView? = null
    private var tomarFoto: MaterialButton? = null
    private var reintentar: MaterialButton? = null
    private var progreso: ProgressBar? = null
    private var estado: TextView? = null
    private var resultado: TextView? = null
    private var elegirFoto: MaterialButton? = null
    private var desplazamiento: ScrollView? = null

    private val galeria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            runCatching {
                val archivo = File(requireContext().cacheDir, "analisis_${UUID.randomUUID()}.jpg")
                requireContext().contentResolver.openInputStream(uri).use { entrada ->
                    checkNotNull(entrada)
                    archivo.outputStream().use { salida ->
                        val buffer = ByteArray(8192)
                        var total = 0
                        var leidos = entrada.read(buffer)
                        while (leidos != -1) {
                            total += leidos
                            check(total <= 15 * 1024 * 1024) { "La imagen supera 15 MB." }
                            salida.write(buffer, 0, leidos)
                            leidos = entrada.read(buffer)
                        }
                    }
                }
                presentador.cargarFoto(archivo)
            }.onFailure { mostrarMensaje("No se pudo abrir la imagen. Elige una fotografía de menos de 15 MB.") }
        }
    }

    private val camara = registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val uri = fotoPendiente
        fotoPendiente = null
        if (uri != null) {
            requireContext().revokeUriPermission(uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val archivo = File(requireContext().cacheDir, uri.lastPathSegment.orEmpty())
            if (exito && archivo.length() > 0) presentador.cargarFoto(archivo)
            else {
                archivo.delete()
                mostrarMensaje("No se guardó una fotografía. Puedes volver a intentarlo.")
            }
        }
    }
    private val permisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
        if (permitido) abrirCamara()
        else mostrarMensaje("Se necesita permiso de cámara. Puedes habilitarlo en los ajustes de la aplicación.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fotoPendiente = savedInstanceState?.getString("foto_pendiente")?.let(Uri::parse)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val contexto = ContextThemeWrapper(requireContext(), R.style.tema_riesgos)
        val columna = LinearLayout(contexto).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(24))
            setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_fondo))
        }
        fun texto(contenido: String, tamano: Float = 16f) = TextView(contexto).apply {
            text = contenido
            textSize = tamano
            setTextColor(ContextCompat.getColor(contexto, R.color.color_texto_principal))
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            setPadding(0, dp(8), 0, dp(12))
            columna.addView(this)
        }
        val cabecera = LinearLayout(contexto).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_superficie))
        }
        cabecera.addView(ImageButton(contexto).apply {
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setImageResource(R.drawable.ic_riesgos_volver)
            imageTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.color_principal))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            contentDescription = getString(R.string.riesgos_volver)
            setOnClickListener { volver() }
        })
        cabecera.addView(TextView(contexto).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            text = getString(R.string.analisis_titulo)
            textSize = 22f
            setTextColor(ContextCompat.getColor(contexto, R.color.color_texto_principal))
            setTypeface(Typeface.create("sans-serif", Typeface.NORMAL), Typeface.BOLD)
            androidx.core.view.ViewCompat.setAccessibilityHeading(this, true)
        })
        texto(getString(R.string.analisis_introduccion))
        imagen = ImageView(contexto).apply {
            contentDescription = "Fotografía seleccionada para analizar"
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(ContextCompat.getColor(contexto, R.color.color_secondary))
            columna.addView(this, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(240)))
        }
        tomarFoto = MaterialButton(contexto).apply {
            minHeight = dp(48)
            cornerRadius = dp(12)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            text = "Tomar fotografía"
            setOnClickListener {
                ocultarTeclado()
                if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara()
                } else permisoCamara.launch(Manifest.permission.CAMERA)
            }
            columna.addView(this)
        }
        elegirFoto = MaterialButton(contexto).apply {
            setText(R.string.analisis_elegir_foto)
            minHeight = dp(48)
            cornerRadius = dp(12)
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.color_superficie))
            setTextColor(ContextCompat.getColor(contexto, R.color.color_principal))
            strokeWidth = dp(1)
            strokeColor = ColorStateList.valueOf(ContextCompat.getColor(contexto, R.color.color_borde))
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnClickListener { galeria.launch("image/*") }
            columna.addView(this)
        }
        reintentar = MaterialButton(contexto).apply {
            minHeight = dp(48)
            cornerRadius = dp(12)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setText(R.string.analisis_reintentar)
            isVisible = false
            setOnClickListener { presentador.analizar() }
            columna.addView(this)
        }
        progreso = ProgressBar(contexto).apply { columna.addView(this) }
        estado = texto(getString(R.string.analisis_inicio)).apply {
            accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        }
        resultado = texto("").apply { setTextIsSelectable(true) }
        texto(getString(R.string.analisis_aviso_breve), 14f)
        val scroll = ScrollView(contexto).apply {
            isFillViewport = true
            addView(columna)
            desplazamiento = this
        }
        return LinearLayout(contexto).apply {
            orientation = LinearLayout.VERTICAL
            addView(cabecera)
            addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = volver()
            })
        presentador.adjuntar(this)
    }

    private fun abrirCamara() {
        try {
            val contexto = requireContext()
            val archivo = File(contexto.cacheDir, "analisis_${UUID.randomUUID()}.jpg")
            archivo.createNewFile()
            val uri = Uri.Builder().scheme("content").authority("${contexto.packageName}.analisis.fotos")
                .appendPath(archivo.name).build()
            fotoPendiente = uri
            camara.launch(uri)
        } catch (_: ActivityNotFoundException) {
            limpiarFotoPendiente()
            mostrarMensaje("No hay una aplicación de cámara disponible en este dispositivo.")
        } catch (_: Exception) {
            limpiarFotoPendiente()
            mostrarMensaje("No se pudo abrir la cámara. Comprueba los permisos y que el dispositivo tenga una cámara activa.")
        }
    }

    private fun limpiarFotoPendiente() {
        fotoPendiente?.lastPathSegment?.let { File(requireContext().cacheDir, it).delete() }
        fotoPendiente = null
    }

    private fun mostrarMensaje(mensaje: String) {
        estado?.text = mensaje
        estado?.isVisible = true
    }

    override fun mostrarEstado(foto: Bitmap?, ocupado: Boolean, resultado: String, error: String?) {
        val resultadoAnterior = this.resultado?.text?.toString().orEmpty()
        imagen?.setImageBitmap(foto)
        imagen?.isVisible = foto != null
        tomarFoto?.isEnabled = !ocupado
        elegirFoto?.isEnabled = !ocupado
        reintentar?.isVisible = error != null && foto != null && !ocupado
        progreso?.isVisible = ocupado
        estado?.text = error ?: when {
            ocupado -> getString(R.string.analisis_procesando)
            resultado.isNotBlank() -> getString(R.string.analisis_resultado)
            else -> getString(R.string.analisis_inicio)
        }
        estado?.isVisible = ocupado || error != null
        this.resultado?.text = resultado
        if (resultado.isNotBlank() && resultado != resultadoAnterior) {
            desplazamiento?.post {
                desplazamiento?.smoothScrollTo(0, this.resultado?.top ?: 0)
            }
        }
    }

    private fun volver() {
        ocultarTeclado()
        (activity as? NavegacionAnalisis)?.volverDesdeAnalisis()
    }

    private fun ocultarTeclado() {
        val gestor = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        gestor.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun dp(valor: Int) = (valor * resources.displayMetrics.density).toInt()

    override fun onSaveInstanceState(outState: Bundle) {
        fotoPendiente?.let { outState.putString("foto_pendiente", it.toString()) }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        presentador.separar()
        imagen = null
        tomarFoto = null
        reintentar = null
        progreso = null
        estado = null
        resultado = null
        elegirFoto = null
        desplazamiento = null
        super.onDestroyView()
    }
}
