package com.riesgosra.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.riesgosra.app.R
import com.riesgosra.app.login.model.data.SesionManager
import com.riesgosra.app.login.view.login.LoginActivity
import com.riesgosra.app.perfil.model.PerfilDemostracionRepository
import com.riesgosra.app.perfil.model.PerfilUsuario
import com.riesgosra.app.perfil.presenter.PerfilPresenter
import com.riesgosra.app.perfil.presenter.PerfilView

class PerfilFragment : Fragment(R.layout.fragment_perfil), PerfilView {

    private var presenter: PerfilPresenter? = null

    private val selectorImagenPerfil =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imagenSeleccionada: Uri? ->
            imagenSeleccionada?.let { uri ->
                view?.findViewById<ImageView>(R.id.img_foto_perfil)?.setImageURI(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(view.findViewById(R.id.txt_titulo_perfil), true)
        ViewCompat.setAccessibilityHeading(view.findViewById(R.id.txt_nombre_perfil), true)
        ViewCompat.setAccessibilityHeading(view.findViewById(R.id.txt_titulo_info_perfil), true)

        presenter = PerfilPresenter(
            view = this,
            repository = PerfilDemostracionRepository(),
            sesionManager = SesionManager(requireContext())
        )

        configurarEventos(view)
        presenter?.cargarPerfil()
    }

    private fun configurarEventos(vista: View) {
        vista.findViewById<View>(R.id.btn_volver_inicio).setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.navegacion_inferior)
                .selectedItemId = R.id.navigation_inicio
        }

        vista.findViewById<View>(R.id.btn_cambiar_foto_perfil).setOnClickListener {
            selectorImagenPerfil.launch("image/*")
        }

        vista.findViewById<MaterialButton>(R.id.btn_cerrar_sesion).setOnClickListener {
            presenter?.cerrarSesion()
        }
    }

    override fun mostrarPerfil(perfil: PerfilUsuario) {
        val vistaPerfil = view ?: return

        vistaPerfil.findViewById<TextView>(R.id.txt_nombre_perfil).text =
            perfil.nombreMostrar

        configurarDato(
            vista = vistaPerfil,
            contenedorId = R.id.dato_nombre_completo,
            iconoId = R.drawable.ic_persona,
            etiqueta = getString(R.string.perfil_nombres_completos),
            valor = perfil.nombresCompletos
        )

        configurarDato(
            vista = vistaPerfil,
            contenedorId = R.id.dato_correo,
            iconoId = R.drawable.ic_correo_perfil,
            etiqueta = getString(R.string.perfil_correo),
            valor = perfil.correoElectronico
        )

        configurarDato(
            vista = vistaPerfil,
            contenedorId = R.id.dato_descripcion,
            iconoId = R.drawable.ic_descripcion_perfil,
            etiqueta = getString(R.string.perfil_descripcion),
            valor = perfil.descripcion
        )
    }

    private fun configurarDato(
        vista: View,
        contenedorId: Int,
        iconoId: Int,
        etiqueta: String,
        valor: String
    ) {
        val contenedor = vista.findViewById<View>(contenedorId)

        contenedor.findViewById<ImageView>(R.id.img_icono_dato)
            .setImageResource(iconoId)

        contenedor.findViewById<TextView>(R.id.txt_etiqueta_dato).text =
            etiqueta

        contenedor.findViewById<TextView>(R.id.txt_valor_dato).text =
            valor
    }

    override fun abrirInicioSesion() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
    }

    override fun onDestroyView() {
        presenter = null
        super.onDestroyView()
    }
}
