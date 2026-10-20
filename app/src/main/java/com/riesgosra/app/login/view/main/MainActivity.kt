package com.riesgosra.app.login.view.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.riesgosra.app.fragment.InicioFragment
import com.riesgosra.app.fragment.PerfilFragment
import com.riesgosra.app.fragment.RiesgosFragment
import com.riesgosra.app.fragment.RutaSeguraFragment
import com.riesgosra.app.fragment.SimulacrosFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.riesgosra.app.R
import com.riesgosra.app.riesgos.view.NavegacionRiesgos

class MainActivity : AppCompatActivity(), NavegacionRiesgos {

    private lateinit var navegacionInferior: BottomNavigationView
    private var destinoActual = R.id.navigation_inicio
    private var destinoAnteriorRiesgos = R.id.navigation_inicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        WindowCompat.getInsetsController(window, findViewById(R.id.main)).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barrasSistema.left, barrasSistema.top, barrasSistema.right, barrasSistema.bottom)
            insets
        }

        navegacionInferior = findViewById(R.id.navegacion_inferior)
        destinoActual = savedInstanceState?.getInt(CLAVE_DESTINO_ACTUAL) ?: R.id.navigation_inicio
        destinoAnteriorRiesgos = savedInstanceState?.getInt(CLAVE_DESTINO_ANTERIOR) ?: R.id.navigation_inicio
        configurarNavegacion()

        if (savedInstanceState == null) {
            navegacionInferior.selectedItemId = R.id.navigation_inicio
        }
    }

    private fun configurarNavegacion() {
        navegacionInferior.setOnItemSelectedListener { item ->
            if (item.itemId == destinoActual && supportFragmentManager.findFragmentById(
                    R.id.fragment_contenedor_principal) != null) return@setOnItemSelectedListener true
            val fragment = obtenerFragment(item.itemId) ?: return@setOnItemSelectedListener false
            if (item.itemId == R.id.navigation_riesgos) destinoAnteriorRiesgos = destinoActual
            destinoActual = item.itemId

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_contenedor_principal, fragment)
                .commit()
            true
        }
    }

    override fun volverDesdeRiesgos() {
        navegacionInferior.selectedItemId = destinoAnteriorRiesgos
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CLAVE_DESTINO_ACTUAL, destinoActual)
        outState.putInt(CLAVE_DESTINO_ANTERIOR, destinoAnteriorRiesgos)
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val CLAVE_DESTINO_ACTUAL = "destino_actual"
        const val CLAVE_DESTINO_ANTERIOR = "destino_anterior_riesgos"
    }

    private fun obtenerFragment(menuItemId: Int): Fragment? {
        return when (menuItemId) {
            R.id.navigation_inicio -> InicioFragment()
            R.id.navigation_riesgos -> RiesgosFragment()
            R.id.navigation_simulacros -> SimulacrosFragment()
            R.id.navigation_ruta_segura -> RutaSeguraFragment()
            R.id.navigation_perfil -> PerfilFragment()
            else -> null
        }
    }
}

