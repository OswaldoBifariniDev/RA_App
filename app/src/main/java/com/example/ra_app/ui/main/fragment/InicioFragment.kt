package com.example.ra_app.ui.main.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import com.example.ra_app.R
import kotlin.math.abs

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private val manejadorCarrusel = Handler(Looper.getMainLooper())
    private var carruselAyuda: ViewFlipper? = null
    private var filaIndicadores: LinearLayout? = null

    private val avanzarCarrusel = object : Runnable {
        override fun run() {
            navegarCarrusel(avanzar = true)
            programarAvanceAutomatico()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carruselAyuda = view.findViewById(R.id.carrusel_ayuda_emergencia)
        filaIndicadores = view.findViewById(R.id.fila_indicadores_carrusel)
        val nombreUsuario = arguments?.getString(ARG_NOMBRE_USUARIO)
            ?.takeIf(String::isNotBlank)
            ?: getString(R.string.nombre_usuario_predeterminado)
        view.findViewById<TextView>(R.id.txt_saludo_inicio).text =
            getString(R.string.saludo_inicio, nombreUsuario)

        configurarDiapositivas()
        actualizarIndicadores(0)
        configurarGestos()
    }

    override fun onResume() {
        super.onResume()
        programarAvanceAutomatico()
    }

    override fun onPause() {
        manejadorCarrusel.removeCallbacks(avanzarCarrusel)
        super.onPause()
    }

    override fun onDestroyView() {
        manejadorCarrusel.removeCallbacks(avanzarCarrusel)
        carruselAyuda = null
        filaIndicadores = null
        super.onDestroyView()
    }

    private fun configurarDiapositivas() {
        val carrusel = carruselAyuda ?: return
        val titulos = resources.getStringArray(R.array.titulos_ayuda_emergencia)
        val detalles = resources.getStringArray(R.array.detalles_ayuda_emergencia)
        val imagenes = intArrayOf(
            R.drawable.ic_ruta_evacuacion,
            R.drawable.ic_mochila_emergencia,
            R.drawable.ic_punto_encuentro
        )

        for (indice in 0 until carrusel.childCount) {
            val diapositiva = carrusel.getChildAt(indice)
            diapositiva.findViewById<ImageView>(R.id.img_ayuda_emergencia).apply {
                setImageResource(imagenes[indice])
                contentDescription = getString(R.string.descripcion_imagen_ayuda, titulos[indice])
            }
            diapositiva.findViewById<TextView>(R.id.txt_titulo_ayuda_emergencia).text = titulos[indice]
            diapositiva.findViewById<TextView>(R.id.txt_detalle_ayuda_emergencia).text = detalles[indice]
        }
    }

    private fun configurarGestos() {
        val carrusel = carruselAyuda ?: return
        val detectorGestos = GestureDetector(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(evento: MotionEvent): Boolean = true

                override fun onFling(
                    inicio: MotionEvent?,
                    fin: MotionEvent,
                    velocidadX: Float,
                    velocidadY: Float
                ): Boolean {
                    if (inicio == null) return false

                    val distanciaMinima = 48 * resources.displayMetrics.density
                    if (abs(inicio.x - fin.x) < distanciaMinima || abs(velocidadX) < 100) {
                        return false
                    }

                    navegarCarrusel(avanzar = inicio.x > fin.x)
                    programarAvanceAutomatico()
                    return true
                }
            }
        )

        carrusel.setOnTouchListener { _, evento ->
            detectorGestos.onTouchEvent(evento)
            true
        }
    }

    private fun navegarCarrusel(avanzar: Boolean) {
        val carrusel = carruselAyuda ?: return
        val animacionEntrada = if (avanzar) {
            R.anim.deslizar_entrada_derecha
        } else {
            R.anim.deslizar_entrada_izquierda
        }
        val animacionSalida = if (avanzar) {
            R.anim.deslizar_salida_izquierda
        } else {
            R.anim.deslizar_salida_derecha
        }

        carrusel.inAnimation = AnimationUtils.loadAnimation(requireContext(), animacionEntrada)
        carrusel.outAnimation = AnimationUtils.loadAnimation(requireContext(), animacionSalida)
        if (avanzar) carrusel.showNext() else carrusel.showPrevious()
        actualizarIndicadores(carrusel.displayedChild)
    }

    private fun actualizarIndicadores(indiceSeleccionado: Int) {
        val fila = filaIndicadores ?: return
        val anchoSeleccionado = resources.getDimensionPixelSize(R.dimen.indicador_carrusel_seleccionado)
        val anchoNormal = resources.getDimensionPixelSize(R.dimen.indicador_carrusel_normal)

        for (indice in 0 until fila.childCount) {
            val indicador = fila.getChildAt(indice)
            val seleccionado = indice == indiceSeleccionado
            val parametros = indicador.layoutParams
            parametros.width = if (seleccionado) anchoSeleccionado else anchoNormal
            indicador.layoutParams = parametros
            indicador.setBackgroundResource(
                if (seleccionado) R.drawable.bg_indicador_seleccionado else R.drawable.bg_indicador
            )
        }

        fila.contentDescription = getString(
            R.string.descripcion_pagina_carrusel,
            indiceSeleccionado + 1,
            fila.childCount
        )
    }

    private fun programarAvanceAutomatico() {
        manejadorCarrusel.removeCallbacks(avanzarCarrusel)
        manejadorCarrusel.postDelayed(avanzarCarrusel, INTERVALO_AVANCE_CARRUSEL_MS)
    }

    companion object {
        private const val INTERVALO_AVANCE_CARRUSEL_MS = 5_000L
        private const val ARG_NOMBRE_USUARIO = "nombre_usuario"

        fun crearConNombre(nombreUsuario: String): InicioFragment = InicioFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NOMBRE_USUARIO, nombreUsuario)
            }
        }
    }
}
