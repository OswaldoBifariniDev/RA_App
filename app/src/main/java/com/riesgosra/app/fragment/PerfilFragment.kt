package com.riesgosra.app.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.riesgosra.app.R
import com.riesgosra.app.perfil.model.PerfilDemostracionRepository
import com.riesgosra.app.perfil.model.PerfilUsuario
import com.riesgosra.app.perfil.presenter.PerfilPresenter
import com.riesgosra.app.perfil.presenter.PerfilView

class PerfilFragment : Fragment(R.layout.fragment_perfil), PerfilView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PerfilPresenter(this, PerfilDemostracionRepository()).cargarPerfil()
    }

    override fun mostrarPerfil(perfil: PerfilUsuario) {
        val vista = requireView()
        vista.findViewById<TextView>(R.id.txt_nombre_perfil).text = perfil.nombre
        vista.findViewById<TextView>(R.id.txt_apellido_perfil).text = perfil.apellido
        vista.findViewById<TextView>(R.id.txt_correo_perfil).text = perfil.correoElectronico
    }
}
