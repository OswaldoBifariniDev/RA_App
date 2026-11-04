
package com.riesgosra.app.rutasegura.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.riesgosra.app.R
import com.riesgosra.app.rutasegura.model.EscenarioRuta
import com.riesgosra.app.rutasegura.model.RutaSeguraModelo
import com.riesgosra.app.rutasegura.presenter.RutaSeguraContrato
import com.riesgosra.app.rutasegura.presenter.RutaSeguraPresenter

open class RutaSeguraMvpFragment :
    Fragment(R.layout.fragment_ruta_segura),
    RutaSeguraContrato.Vista {

    private var presentador: RutaSeguraContrato.Presentador? = null

    private lateinit var tarjetaSismo: MaterialCardView
    private lateinit var tarjetaInundacion: MaterialCardView
    private lateinit var tarjetaIncendio: MaterialCardView
    private lateinit var txtOrientacion: TextView
    private lateinit var btnIniciarRecorrido: MaterialButton

    /**
     * Solicitud del permiso de cámara.
     */
    private val solicitudPermisoCamara =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            val puedeSolicitarNuevamente =
                shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA
                )

            presentador?.alResultadoPermisoCamara(
                concedido,
                puedeSolicitarNuevamente
            )
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        inicializarComponentes(view)
        configurarEventos()

        presentador = RutaSeguraPresenter(
            this,
            RutaSeguraModelo(requireContext())
        )
    }

    private fun inicializarComponentes(view: View) {

        tarjetaSismo = view.findViewById(
            R.id.card_escenario_sismo
        )

        tarjetaInundacion = view.findViewById(
            R.id.card_escenario_inundacion
        )

        tarjetaIncendio = view.findViewById(
            R.id.card_escenario_incendio
        )

        txtOrientacion = view.findViewById(
            R.id.txt_orientacion_escenario
        )

        btnIniciarRecorrido = view.findViewById(
            R.id.btn_iniciar_recorrido
        )
    }

    private fun configurarEventos() {

        tarjetaSismo.setOnClickListener {
            presentador?.alSeleccionarEscenario(
                EscenarioRuta.SISMO
            )
        }

        tarjetaInundacion.setOnClickListener {
            presentador?.alSeleccionarEscenario(
                EscenarioRuta.INUNDACION
            )
        }

        tarjetaIncendio.setOnClickListener {
            presentador?.alSeleccionarEscenario(
                EscenarioRuta.INCENDIO
            )
        }

        btnIniciarRecorrido.setOnClickListener {
            presentador?.alPulsarActivarCamara()
        }
    }

    /**
     * Actualiza la recomendación y el aspecto
     * de la tarjeta seleccionada.
     */
    override fun mostrarEscenarioSeleccionado(
        escenario: EscenarioRuta
    ) {

        val recomendacionResId = when (escenario) {

            EscenarioRuta.SISMO ->
                R.string.ruta_segura_orientacion_sismo

            EscenarioRuta.INUNDACION ->
                R.string.ruta_segura_orientacion_inundacion

            EscenarioRuta.INCENDIO ->
                R.string.ruta_segura_orientacion_incendio
        }

        txtOrientacion.setText(recomendacionResId)

        actualizarTarjeta(
            tarjetaSismo,
            escenario == EscenarioRuta.SISMO
        )

        actualizarTarjeta(
            tarjetaInundacion,
            escenario == EscenarioRuta.INUNDACION
        )

        actualizarTarjeta(
            tarjetaIncendio,
            escenario == EscenarioRuta.INCENDIO
        )
    }

    /**
     * Aplica el estilo visual correspondiente
     * a cada tarjeta de emergencia.
     */
    private fun actualizarTarjeta(
        tarjeta: MaterialCardView,
        seleccionada: Boolean
    ) {

        val colorFondo = if (seleccionada) {
            R.color.color_secondary
        } else {
            R.color.color_surface
        }

        val colorBorde = if (seleccionada) {
            R.color.color_primary
        } else {
            R.color.color_border
        }

        tarjeta.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                colorFondo
            )
        )

        tarjeta.strokeColor = ContextCompat.getColor(
            requireContext(),
            colorBorde
        )

        tarjeta.strokeWidth = resources.getDimensionPixelSize(
            R.dimen.ancho_borde_delgado
        ) * if (seleccionada) 2 else 1

        tarjeta.cardElevation = dp(
            if (seleccionada) 3f else 1f
        )

        tarjeta.animate().cancel()

        tarjeta.scaleX = 1f
        tarjeta.scaleY = 1f

        if (seleccionada) {
            tarjeta.animate()
                .scaleX(1.025f)
                .scaleY(1.025f)
                .setDuration(170)
                .start()
        }
    }

    private fun dp(valor: Float): Float {
        return valor * resources.displayMetrics.density
    }

    /**
     * Solicita el permiso de cámara.
     */
    override fun solicitarPermisoCamara() {
        solicitudPermisoCamara.launch(
            Manifest.permission.CAMERA
        )
    }

    override fun debeMostrarJustificacionPermiso(): Boolean {
        return shouldShowRequestPermissionRationale(
            Manifest.permission.CAMERA
        )
    }

    /**
     * Explica al usuario por qué se necesita
     * acceder a la cámara.
     */
    override fun mostrarExplicacionPermiso() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(
                R.string.ruta_segura_permiso_titulo
            )
            .setMessage(
                R.string.ruta_segura_permiso_mensaje
            )
            .setPositiveButton(
                R.string.ruta_segura_permiso_continuar
            ) { _, _ ->

                presentador?.alConfirmarExplicacionPermiso()
            }
            .setNegativeButton(
                R.string.ruta_segura_permiso_cancelar,
                null
            )
            .show()
    }

    /**
     * Informa cuando el permiso fue rechazado.
     */
    override fun mostrarPermisoDenegado(
        puedeSolicitarNuevamente: Boolean
    ) {

        val dialogo = MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle(
                R.string.ruta_segura_permiso_denegado_titulo
            )
            .setMessage(
                R.string.ruta_segura_permiso_denegado_mensaje
            )
            .setNegativeButton(
                R.string.ruta_segura_permiso_cancelar,
                null
            )

        if (puedeSolicitarNuevamente) {

            dialogo.setPositiveButton(
                R.string.ruta_segura_permiso_reintentar
            ) { _, _ ->

                presentador?.alConfirmarExplicacionPermiso()
            }

        } else {

            dialogo.setPositiveButton(
                R.string.ruta_segura_permiso_ajustes
            ) { _, _ ->

                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts(
                        "package",
                        requireContext().packageName,
                        null
                    )
                )

                startActivity(intent)
            }
        }

        dialogo.show()
    }

    /**
     * Se conectará con CameraX en el siguiente bloque.
     */
    override fun abrirCamara(escenario: EscenarioRuta) {

        Toast.makeText(
            requireContext(),
            R.string.ruta_segura_camara_pendiente,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        presentador = null
        super.onDestroyView()
    }
}
