package com.riesgosra.app.riesgos.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
        prepararPantalla(R.string.riesgos_informacion_titulo)
        agregarNombre(riesgo.nombre)
        TemaRiesgo.values().forEach { tema ->
            val tarjeta = inflar(R.layout.item_riesgo_tema)
            tarjeta.findViewById<MaterialButton>(R.id.btn_riesgo_tema).apply {
                setText(obtenerTitulo(tema))
                setIconResource(when (tema) {
                    TemaRiesgo.ANALISIS -> R.drawable.ic_informacion
                    TemaRiesgo.PLAN_ACCION -> R.drawable.ic_ruta_evacuacion
                    TemaRiesgo.CONSEJOS -> R.drawable.ic_recomendacion
                })
                setOnClickListener { presentador?.seleccionarTema(tema) }
            }
            contenido.addView(tarjeta)
        }
    }

    override fun mostrarInformacion(riesgo: Riesgo, tema: TemaRiesgo, informacion: String) {
        prepararPantalla(obtenerTitulo(tema))
        agregarNombre(riesgo.nombre)
        val tarjeta = inflar(R.layout.item_riesgo_contenido)
        tarjeta.findViewById<TextView>(R.id.txt_riesgo_contenido).text = informacion
        contenido.addView(tarjeta)
    }

    override fun mostrarError() {
        view?.findViewById<View>(R.id.progreso_riesgos)?.isVisible = false
        mostrarAviso(R.string.riesgos_error_conexion)
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
        super.onDestroyView()
    }

    private companion object {
        const val CLAVE_RIESGO = "id_riesgo_seleccionado"
        const val CLAVE_TEMA = "tema_riesgo_seleccionado"
    }
}
