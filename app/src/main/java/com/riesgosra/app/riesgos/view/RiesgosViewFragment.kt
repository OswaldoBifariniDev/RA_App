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
    private var idRiesgoGuardado: Int? = null
    private var temaGuardado: TemaRiesgo? = null
    private var riesgoMostrado: Int? = null
    private val secciones = mutableMapOf<TemaRiesgo, Pair<MaterialButton, View>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idRiesgoGuardado = savedInstanceState?.getInt(CLAVE_RIESGO)?.takeIf { it > 0 }
        temaGuardado = savedInstanceState?.getString(CLAVE_TEMA)?.let { nombre ->
            TemaRiesgo.values().find { it.name == nombre }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contenido = view.findViewById(R.id.layout_riesgos_contenido)
        presentador = RiesgosPresenter(this, ApiRiesgosRepository(), idRiesgoGuardado, temaGuardado)
        view.findViewById<View>(R.id.btn_riesgos_volver).setOnClickListener { volver() }
        view.findViewById<View>(R.id.btn_riesgos_reintentar).setOnClickListener {
            presentador?.cargarRiesgos()
        }
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
        prepararPantalla(R.string.riesgos_titulo)
        view?.findViewById<View>(R.id.txt_riesgos_instruccion)?.isVisible = true
        if (riesgos.isEmpty()) {
            mostrarAviso(R.string.riesgos_vacio)
            return
        }
        riesgos.forEach { riesgo ->
            val tarjeta = inflar(R.layout.item_riesgo)
            tarjeta.findViewById<ImageView>(R.id.img_riesgo_tipo).setImageResource(RiesgoVisuals.icono(riesgo.nombre))
            tarjeta.findViewById<TextView>(R.id.txt_riesgo_nombre).apply {
                text = riesgo.nombre
                ViewCompat.setAccessibilityHeading(this, true)
            }
            tarjeta.findViewById<MaterialButton>(R.id.btn_riesgo_informacion).apply {
                contentDescription = getString(R.string.riesgos_consultar, riesgo.nombre)
                setOnClickListener { presentador?.seleccionarRiesgo(riesgo.idRiesgo) }
            }
            contenido.addView(tarjeta)
        }
    }

    override fun mostrarTemas(riesgo: Riesgo) {
        mostrarSecciones(riesgo, null)
    }

    override fun mostrarInformacion(riesgo: Riesgo, tema: TemaRiesgo, informacion: String) {
        mostrarSecciones(riesgo, tema)
    }

    private fun mostrarSecciones(riesgo: Riesgo, temaAbierto: TemaRiesgo?) {
        // Mantener las vistas y la posición de lectura al abrir o cerrar una sección.
        if (riesgoMostrado != riesgo.idRiesgo || secciones.isEmpty()) {
            prepararPantalla(R.string.riesgos_informacion_titulo)
            riesgoMostrado = riesgo.idRiesgo
            agregarNombre(riesgo.nombre)
            TemaRiesgo.values().forEach { tema ->
                val boton = inflar(R.layout.item_riesgo_tema) as MaterialButton
                boton.setText(obtenerTitulo(tema))
                boton.setIconResource(RiesgoVisuals.icono(riesgo.nombre, tema))
                boton.setOnClickListener { presentador?.seleccionarTema(tema) }
                contenido.addView(boton)
                val detalle = if (tema == TemaRiesgo.CONSEJOS) {
                    crearConsejosIlustrados(riesgo)
                } else {
                    inflar(R.layout.item_riesgo_contenido).apply {
                        findViewById<TextView>(R.id.txt_riesgo_contenido).text =
                            if (tema == TemaRiesgo.ANALISIS) riesgo.analisisRiesgo else riesgo.planAccion
                    }
                }
                detalle.isVisible = false
                contenido.addView(detalle)
                secciones[tema] = boton to detalle
            }
        }
        secciones.forEach { (tema, vistas) ->
            val (boton, detalle) = vistas
            val abierto = tema == temaAbierto
            detalle.isVisible = abierto
            ViewCompat.setStateDescription(boton, getString(
                if (abierto) R.string.riesgos_seccion_expandida else R.string.riesgos_seccion_contraida
            ))
        }
    }

    private fun crearConsejosIlustrados(riesgo: Riesgo): View {
        val lista = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        RiesgoVisuals.consejosParaMostrar(riesgo.consejosEstudiante).forEach { consejo ->
            val tarjeta = LayoutInflater.from(requireContext()).inflate(R.layout.item_consejo_estudiante, lista, false)
            tarjeta.findViewById<ImageView>(R.id.img_consejo_estudiante)
                .setImageResource(RiesgoVisuals.ilustracion(riesgo.nombre, consejo))
            tarjeta.findViewById<TextView>(R.id.txt_consejo_estudiante).text = consejo
            lista.addView(tarjeta)
        }
        return lista
    }

    override fun mostrarError(mensaje: String) {
        view?.findViewById<View>(R.id.progreso_riesgos)?.isVisible = false
        val raiz = view ?: return
        raiz.findViewById<View>(R.id.layout_riesgos_estado).isVisible = true
        raiz.findViewById<TextView>(R.id.txt_riesgos_estado).text = mensaje
        raiz.findViewById<View>(R.id.btn_riesgos_reintentar).isVisible = true
    }

    private fun mostrarAviso(mensaje: Int) {
        val raiz = view ?: return
        raiz.findViewById<View>(R.id.layout_riesgos_estado).isVisible = true
        raiz.findViewById<TextView>(R.id.txt_riesgos_estado).setText(mensaje)
        raiz.findViewById<View>(R.id.btn_riesgos_reintentar).isVisible = true
    }

    private fun prepararPantalla(titulo: Int) {
        val raiz = view ?: return
        raiz.findViewById<TextView>(R.id.txt_riesgos_titulo).setText(titulo)
        raiz.findViewById<View>(R.id.txt_riesgos_instruccion).isVisible = false
        raiz.findViewById<View>(R.id.progreso_riesgos).isVisible = false
        raiz.findViewById<View>(R.id.layout_riesgos_estado).isVisible = false
        secciones.clear()
        riesgoMostrado = null
        contenido.removeAllViews()
        raiz.findViewById<NestedScrollView>(R.id.scroll_riesgos).scrollTo(0, 0)
    }

    private fun agregarNombre(nombre: String) {
        val vistaNombre = inflar(R.layout.item_riesgo_nombre) as TextView
        vistaNombre.text = nombre
        ViewCompat.setAccessibilityHeading(vistaNombre, true)
        contenido.addView(vistaNombre)
    }

    private fun inflar(recurso: Int): View = LayoutInflater.from(contenido.context)
        .inflate(recurso, contenido, false)

    private fun obtenerTitulo(tema: TemaRiesgo): Int = when (tema) {
        TemaRiesgo.ANALISIS -> R.string.riesgos_analisis
        TemaRiesgo.PLAN_ACCION -> R.string.riesgos_plan_accion
        TemaRiesgo.CONSEJOS -> R.string.riesgos_consejos
    }

    private fun volver() {
        if (presentador?.volver() != true) (activity as? NavegacionRiesgos)?.volverDesdeRiesgos()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val idRiesgo = if (presentador != null) presentador?.idRiesgoSeleccionado else idRiesgoGuardado
        val tema = if (presentador != null) presentador?.temaSeleccionado else temaGuardado
        outState.putInt(CLAVE_RIESGO, idRiesgo ?: 0)
        outState.putString(CLAVE_TEMA, tema?.name)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        idRiesgoGuardado = presentador?.idRiesgoSeleccionado
        temaGuardado = presentador?.temaSeleccionado
        presentador?.destruir()
        presentador = null
        secciones.clear()
        riesgoMostrado = null
        super.onDestroyView()
    }

    private companion object {
        const val CLAVE_RIESGO = "id_riesgo_seleccionado"
        const val CLAVE_TEMA = "tema_riesgo_seleccionado"
    }
}
