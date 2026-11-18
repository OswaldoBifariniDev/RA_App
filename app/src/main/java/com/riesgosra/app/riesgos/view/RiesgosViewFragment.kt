package com.riesgosra.app.riesgos.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.riesgosra.app.R
import com.riesgosra.app.riesgos.model.ApiRiesgosRepository
import com.riesgosra.app.riesgos.model.Riesgo
import com.riesgosra.app.riesgos.model.TemaRiesgo
import com.riesgosra.app.riesgos.presenter.RiesgosContract
import com.riesgosra.app.riesgos.presenter.RiesgosPresenter

open class RiesgosViewFragment : Fragment(R.layout.fragment_riesgos), RiesgosContract.View {
    private var presentador: RiesgosPresenter? = null
    private lateinit var contenido: LinearLayout
    private var idRiesgoActual: Int? = null
    private var posicionLista = 0
    private val seccionesAbiertas = linkedSetOf<String>()
    private val panelesTema = mutableMapOf<String, Triple<MaterialButton, LinearLayout, TemaRiesgo>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState?.containsKey(CLAVE_RIESGO_ACTUAL) == true) {
            idRiesgoActual = savedInstanceState.getInt(CLAVE_RIESGO_ACTUAL)
        }
        posicionLista = savedInstanceState?.getInt(CLAVE_POSICION_LISTA) ?: 0
        savedInstanceState?.getStringArrayList(CLAVE_SECCIONES_ABIERTAS)?.let { seccionesAbiertas.addAll(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contenido = view.findViewById(R.id.layout_riesgos_contenido)
        presentador = RiesgosPresenter(this, ApiRiesgosRepository(requireContext()), idRiesgoActual)
        view.findViewById<View>(R.id.btn_riesgos_volver).setOnClickListener { volver() }
        view.findViewById<View>(R.id.btn_riesgos_reintentar).setOnClickListener { presentador?.cargarRiesgos() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = volver()
            }
        )
        presentador?.cargarRiesgos()
    }

    override fun mostrarCarga() {
        val raiz = view ?: return
        raiz.findViewById<View>(R.id.progreso_riesgos).isVisible = true
        raiz.findViewById<View>(R.id.layout_riesgos_estado).isVisible = true
        raiz.findViewById<TextView>(R.id.txt_riesgos_estado).setText(R.string.riesgos_cargando)
        raiz.findViewById<View>(R.id.btn_riesgos_reintentar).isVisible = false
    }

    override fun mostrarRiesgos(riesgos: List<Riesgo>) {
        val raiz = view ?: return
        idRiesgoActual = null
        prepararPantalla(getString(R.string.riesgos_titulo), R.string.riesgos_instruccion)
        raiz.findViewById<View>(R.id.txt_riesgos_registrados).isVisible = true
        if (riesgos.isEmpty()) mostrarAviso(R.string.riesgos_vacio)
        riesgos.forEach { riesgo ->
            val tarjeta = inflar(R.layout.item_riesgo, contenido)
            tarjeta.findViewById<TextView>(R.id.txt_riesgo_nombre).apply {
                text = riesgo.nombre
                ViewCompat.setAccessibilityHeading(this, true)
            }
            tarjeta.findViewById<MaterialButton>(R.id.btn_riesgo_informacion).apply {
                contentDescription = getString(R.string.riesgos_consultar, riesgo.nombre)
                setOnClickListener {
                    posicionLista = raiz.findViewById<NestedScrollView>(R.id.scroll_riesgos).scrollY
                    seccionesAbiertas.clear()
                    presentador?.seleccionarRiesgo(riesgo.idRiesgo)
                }
            }
            contenido.addView(tarjeta)
        }
        raiz.findViewById<NestedScrollView>(R.id.scroll_riesgos).apply {
            post { scrollTo(0, posicionLista) }
        }
    }

    private fun prepararPantalla(titulo: String, instruccion: Int) {
        val raiz = view ?: return
        raiz.findViewById<TextView>(R.id.txt_riesgos_titulo).text = titulo
        raiz.findViewById<TextView>(R.id.txt_riesgos_instruccion).setText(instruccion)
        raiz.findViewById<View>(R.id.progreso_riesgos).isVisible = false
        raiz.findViewById<View>(R.id.layout_riesgos_estado).isVisible = false
        contenido.removeAllViews()
        panelesTema.clear()
    }

    private fun agregarTema(riesgo: Riesgo, tema: TemaRiesgo, padre: LinearLayout) {
        val clave = "${riesgo.idRiesgo}:${tema.name}"
        val boton = inflar(R.layout.item_riesgo_tema, padre) as MaterialButton
        boton.setIconResource(when (tema) {
            TemaRiesgo.ANALISIS -> R.drawable.ic_informacion
            TemaRiesgo.PLAN_ACCION -> R.drawable.ic_ruta_evacuacion
            TemaRiesgo.CONSEJOS -> R.drawable.ic_recomendacion
        })
        val panel = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        val informacion = when (tema) {
            TemaRiesgo.ANALISIS -> riesgo.analisisRiesgo
            TemaRiesgo.PLAN_ACCION -> riesgo.planAccion
            TemaRiesgo.CONSEJOS -> riesgo.consejosEstudiante
        }
        if (tema == TemaRiesgo.CONSEJOS) {
            agregarConsejos(informacion, riesgo, panel)
        } else {
            agregarTarjetaIlustrada(informacion, imagenTema(riesgo, tema), panel)
        }
        panelesTema[clave] = Triple(boton, panel, tema)
        actualizarTema(clave)
        boton.setOnClickListener {
            if (!seccionesAbiertas.add(clave)) seccionesAbiertas.remove(clave)
            actualizarTema(clave)
        }
        padre.addView(boton)
        padre.addView(panel)
    }

    private fun agregarConsejos(informacion: String, riesgo: Riesgo, padre: LinearLayout) {
        val separador = Regex(
            """(?<=[.!?])\s+|\r?\n+|\s*[•;]\s*|,\s+(?=mant[eé]n|no\b|sigue\b|protege\b|comunica\b|evita\b|aleja\b)|\s+y\s+(?=sigue\b|comunica\b|mant[eé]n|protege\b|avisa\b|evita\b|aleja\b)""",
            RegexOption.IGNORE_CASE
        )
        val partes = if (informacion.contains('\n')) informacion.lines() else informacion.split(separador)
        val consejos = partes.map { texto ->
            texto.trim().replace(Regex("""^[\d]+[.)]\s*"""), "").trimStart('•', '-', ' ')
                .replaceFirstChar { it.titlecase() }
        }.filter { it.isNotBlank() }
        consejos.forEachIndexed { indice, consejo ->
            val tarjeta = inflar(R.layout.item_consejo_ilustrado, padre)
            tarjeta.findViewById<TextView>(R.id.txt_consejo).text = getString(R.string.riesgos_consejo_numerado, indice + 1, consejo)
            tarjeta.findViewById<ImageView>(R.id.img_consejo).apply {
                setImageResource(imagenConsejo(consejo, riesgo))
                contentDescription = getString(R.string.riesgos_consejo_imagen, consejo)
            }
            padre.addView(tarjeta)
        }
    }

    private fun imagenConsejo(consejo: String, riesgo: Riesgo): Int {
        val texto = consejo.lowercase()
        return when {
            "pared interior" in texto -> R.drawable.ilustracion_sismo_zona_interior
            "cabeza" in texto -> R.drawable.ilustracion_sismo_cubrir_cabeza
            "sism" in riesgo.nombre.lowercase() && "movimiento" in texto && "calma" in texto -> R.drawable.ilustracion_sismo_zona_interior
            "comunica" in texto && ("agua" in texto || "inund" in riesgo.nombre.lowercase()) -> R.drawable.ilustracion_avisar_agua
            "personal" in texto -> R.drawable.ilustracion_seguir_ruta
            listOf("avisa", "comunica", "profesor", "personal").any { it in texto } -> R.drawable.ilustracion_avisar
            "mesa" in texto || "escritorio" in texto -> R.drawable.ilustracion_bajo_mesa
            "cabeza" in texto -> R.drawable.ilustracion_proteger_cabeza
            "cable" in texto || "eléctric" in texto -> R.drawable.ilustracion_evitar_cables
            "inund" in texto || "agua" in texto -> R.drawable.ilustracion_evitar_agua
            "ruta" in texto || "evacua" in texto -> R.drawable.ilustracion_seguir_ruta
            "calma" in texto || "corras" in texto -> R.drawable.ilustracion_caminar
            else -> R.drawable.ilustracion_avisar
        }
    }

    private fun agregarTarjetaIlustrada(texto: String, imagen: Int, padre: LinearLayout) {
        val tarjeta = inflar(R.layout.item_consejo_ilustrado, padre)
        tarjeta.findViewById<TextView>(R.id.txt_consejo).text = texto
        tarjeta.findViewById<ImageView>(R.id.img_consejo).apply {
            setImageResource(imagen)
            contentDescription = texto
        }
        padre.addView(tarjeta)
    }

    private fun imagenTema(riesgo: Riesgo, tema: TemaRiesgo): Int {
        val nombre = riesgo.nombre.lowercase()
        return when {
             "incend" in nombre -> if (tema == TemaRiesgo.ANALISIS) R.drawable.ilustracion_analisis_incendio else R.drawable.ilustracion_plan_incendio
            "sism" in nombre -> if (tema == TemaRiesgo.ANALISIS) R.drawable.ilustracion_analisis_sismo else R.drawable.ilustracion_plan_sismo
            "inund" in nombre -> if (tema == TemaRiesgo.ANALISIS) R.drawable.ilustracion_analisis_inundacion else R.drawable.ilustracion_plan_inundacion
            else -> R.drawable.ilustracion_avisar
        }
    }

    private fun actualizarTema(clave: String) {
        val (boton, panel, tema) = panelesTema[clave] ?: return
        val abierto = clave in seccionesAbiertas
        panel.isVisible = abierto
        boton.text = getString(if (abierto) R.string.riesgos_tema_abierto else R.string.riesgos_tema_cerrado, getString(obtenerTitulo(tema)))
        ViewCompat.setStateDescription(boton, getString(if (abierto) R.string.riesgos_expandido else R.string.riesgos_contraido))
    }

    override fun mostrarTemas(riesgo: Riesgo) {
        val raiz = view ?: return
        val mismoRiesgo = idRiesgoActual == riesgo.idRiesgo
        val scroll = raiz.findViewById<NestedScrollView>(R.id.scroll_riesgos)
        val posicion = if (mismoRiesgo) scroll.scrollY else 0
        idRiesgoActual = riesgo.idRiesgo
        prepararPantalla(riesgo.nombre, R.string.riesgos_instruccion_detalle)
        raiz.findViewById<View>(R.id.txt_riesgos_registrados).isVisible = false
        TemaRiesgo.values().forEach { agregarTema(riesgo, it, contenido) }
        scroll.post { scroll.scrollTo(0, posicion) }
    }

    override fun mostrarInformacion(riesgo: Riesgo, tema: TemaRiesgo, informacion: String) {
        seccionesAbiertas.add("${riesgo.idRiesgo}:${tema.name}")
        mostrarTemas(riesgo)
    }

    override fun mostrarError() {
        view?.findViewById<View>(R.id.progreso_riesgos)?.isVisible = false
        mostrarAviso(R.string.riesgos_error_conexion)
    }

    override fun mostrarDatosGuardados(catalogoIncluido: Boolean) {
        mostrarAviso(if (catalogoIncluido) R.string.riesgos_catalogo_incluido else R.string.riesgos_datos_guardados)
    }

    private fun mostrarAviso(mensaje: Int) {
        val raiz = view ?: return
        raiz.findViewById<View>(R.id.layout_riesgos_estado).isVisible = true
        raiz.findViewById<TextView>(R.id.txt_riesgos_estado).setText(mensaje)
        raiz.findViewById<View>(R.id.btn_riesgos_reintentar).isVisible = true
    }

    private fun inflar(recurso: Int, padre: LinearLayout): View = LayoutInflater.from(padre.context).inflate(recurso, padre, false)

    private fun obtenerTitulo(tema: TemaRiesgo): Int = when (tema) {
        TemaRiesgo.ANALISIS -> R.string.riesgos_analisis
        TemaRiesgo.PLAN_ACCION -> R.string.riesgos_plan_accion
        TemaRiesgo.CONSEJOS -> R.string.riesgos_consejos
    }

    private fun volver() {
        if (presentador?.volver() == true) {
            seccionesAbiertas.clear()
        } else {
            (activity as? NavegacionRiesgos)?.volverDesdeRiesgos()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        idRiesgoActual?.let { outState.putInt(CLAVE_RIESGO_ACTUAL, it) }
        outState.putInt(CLAVE_POSICION_LISTA, posicionLista)
        outState.putStringArrayList(CLAVE_SECCIONES_ABIERTAS, ArrayList(seccionesAbiertas))
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        presentador?.destruir()
        presentador = null
        panelesTema.clear()
        super.onDestroyView()
    }

    private companion object {
        const val CLAVE_RIESGO_ACTUAL = "riesgo_actual"
        const val CLAVE_POSICION_LISTA = "posicion_lista"
        const val CLAVE_SECCIONES_ABIERTAS = "secciones_abiertas"
    }
}