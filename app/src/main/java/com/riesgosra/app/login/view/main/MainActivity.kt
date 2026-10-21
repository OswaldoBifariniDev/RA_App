package com.riesgosra.app.login.view.main

import android.os.Bundle
import android.view.View
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

class MainActivity : AppCompatActivity() {

    private lateinit var navegacionInferior: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }

        val fondoBarraNavegacionSistema = findViewById<View>(R.id.navigation_bar_background)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(
                barrasSistema.left,
                barrasSistema.top,
                barrasSistema.right,
                barrasSistema.bottom
            )

            fondoBarraNavegacionSistema.layoutParams =
                fondoBarraNavegacionSistema.layoutParams.apply {
                    height = barrasSistema.bottom
                }
            fondoBarraNavegacionSistema.translationY = barrasSistema.bottom.toFloat()
            insets
        }

        navegacionInferior = findViewById(R.id.navegacion_inferior)
        configurarNavegacion()

        if (savedInstanceState == null) {
            navegacionInferior.selectedItemId = R.id.navigation_inicio
        }
    }

    private fun configurarNavegacion() {
        navegacionInferior.setOnItemSelectedListener { item ->
            val fragment = obtenerFragment(item.itemId) ?: return@setOnItemSelectedListener false

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_contenedor_principal, fragment)
                .commit()
            true
        }
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
