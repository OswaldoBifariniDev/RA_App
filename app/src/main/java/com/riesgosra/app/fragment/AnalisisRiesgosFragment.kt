package com.riesgosra.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ra_app.ui.risk.RiskCaptureActivity
import com.example.ra_app.utils.ApiKeyProvider
import com.riesgosra.app.R

class AnalisisRiesgosFragment : Fragment(R.layout.fragment_simulacros) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.txt_analisis_estado).apply {
            visibility = if (ApiKeyProvider.hasValidKey()) View.GONE else View.VISIBLE
        }
        fun abrir(accion: String) {
            startActivity(Intent(requireContext(), RiskCaptureActivity::class.java)
                .putExtra(RiskCaptureActivity.EXTRA_INITIAL_ACTION, accion))
        }
        view.findViewById<View>(R.id.btn_analisis_camara).setOnClickListener { abrir("camera") }
        view.findViewById<View>(R.id.btn_analisis_galeria).setOnClickListener { abrir("gallery") }
        view.findViewById<View>(R.id.btn_analisis_historial).setOnClickListener { abrir("history") }
    }
}
