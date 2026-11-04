
package com.riesgosra.app.rutasegura.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.riesgosra.app.R
import com.riesgosra.app.rutasegura.model.EscenarioRuta

/**
 * Actividad encargada de mostrar la cámara
 * durante el recorrido de Ruta segura.
 */
class CamaraRutaSeguraActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ESCENARIO =
            "com.riesgosra.app.rutasegura.EXTRA_ESCENARIO"
    }

    private lateinit var previewView: PreviewView
    private lateinit var txtEscenario: TextView
    private lateinit var txtMensaje: TextView
    private lateinit var txtEstado: TextView
    private lateinit var btnCerrar: MaterialButton
    private lateinit var btnVolver: ImageButton

    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_camara_ruta_segura
        )

        inicializarComponentes()
        configurarEventos()
        configurarEscenario()

        if (!tienePermisoCamara()) {
            Toast.makeText(
                this,
                R.string.ruta_segura_camara_permiso_faltante,
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        iniciarCamara()
    }

    private fun inicializarComponentes() {

        previewView = findViewById(
            R.id.preview_camara
        )

        txtEscenario = findViewById(
            R.id.txt_escenario_camara
        )

        txtMensaje = findViewById(
            R.id.txt_mensaje_recorrido
        )

        txtEstado = findViewById(
            R.id.txt_estado_camara
        )

        btnCerrar = findViewById(
            R.id.btn_cerrar_recorrido
        )

        btnVolver = findViewById(
            R.id.btn_volver_camara
        )
    }

    private fun configurarEventos() {

        btnCerrar.setOnClickListener {
            finish()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun obtenerEscenario(): EscenarioRuta {

        val nombreEscenario = intent.getStringExtra(
            EXTRA_ESCENARIO
        )

        return runCatching {
            EscenarioRuta.valueOf(
                nombreEscenario ?: EscenarioRuta.SISMO.name
            )
        }.getOrDefault(EscenarioRuta.SISMO)
    }

    private fun configurarEscenario() {

        when (obtenerEscenario()) {

            EscenarioRuta.SISMO -> {
                txtEscenario.text =
                    getString(R.string.ruta_segura_camara_sismo)

                txtMensaje.setText(
                    R.string.ruta_segura_orientacion_sismo
                )
            }

            EscenarioRuta.INUNDACION -> {
                txtEscenario.text =
                    getString(R.string.ruta_segura_camara_inundacion)

                txtMensaje.setText(
                    R.string.ruta_segura_orientacion_inundacion
                )
            }

            EscenarioRuta.INCENDIO -> {
                txtEscenario.text =
                    getString(R.string.ruta_segura_camara_incendio)

                txtMensaje.setText(
                    R.string.ruta_segura_orientacion_incendio
                )
            }
        }
    }

    private fun tienePermisoCamara(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Inicializa CameraX y vincula la vista previa
     * al ciclo de vida de esta actividad.
     */
    private fun iniciarCamara() {

        txtEstado.setText(
            R.string.ruta_segura_camara_preparando
        )

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            try {

                val provider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            previewView.surfaceProvider
                        )
                    }

                val selector =
                    CameraSelector.DEFAULT_BACK_CAMERA

                provider.unbindAll()

                provider.bindToLifecycle(
                    this,
                    selector,
                    preview
                )

                cameraProvider = provider

                txtEstado.setText(
                    R.string.ruta_segura_camara_activa
                )

            } catch (exception: Exception) {

                txtEstado.setText(
                    R.string.ruta_segura_camara_error
                )

                Toast.makeText(
                    this,
                    R.string.ruta_segura_camara_error,
                    Toast.LENGTH_LONG
                ).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        cameraProvider?.unbindAll()
        cameraProvider = null

        super.onDestroy()
    }
}
