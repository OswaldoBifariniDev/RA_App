package com.riesgosra.app.login.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.riesgosra.app.R
import com.riesgosra.app.login.view.auth.RegistroActivity
import com.riesgosra.app.login.view.main.MainActivity
import com.riesgosra.app.login.model.data.SesionManager
import com.riesgosra.app.login.model.network.ApiService
import com.riesgosra.app.login.presenter.LoginPresenter
import com.riesgosra.app.login.presenter.LoginView
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var inputIdentificador: TextInputLayout
    private lateinit var inputContrasena: TextInputLayout

    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_login)

        configurarVistas()
        configurarPresenter()
        configurarEventos()
        configurarInsets()
    }

    private fun configurarVistas() {

        inputIdentificador =
            findViewById(R.id.input_identificador)

        inputContrasena =
            findViewById(R.id.input_contrasena)
    }

    private fun configurarPresenter() {

        val apiService = ApiService()

        val sesionManager =
            SesionManager(this)

        val mainExecutor = Executor { runnable ->
            runOnUiThread(runnable)
        }

        presenter = LoginPresenter(
            view = this,
            apiService = apiService,
            sesionManager = sesionManager,
            mainExecutor = mainExecutor
        )
    }

    private fun configurarEventos() {

        val txtIdentificador =
            findViewById<TextInputEditText>(
                R.id.txt_identificador
            )

        val txtContrasena =
            findViewById<TextInputEditText>(
                R.id.txt_contrasena
            )

        val btnIniciarSesion =
            findViewById<MaterialButton>(
                R.id.btn_iniciar_sesion
            )

        txtIdentificador.doAfterTextChanged {
            inputIdentificador.error = null
        }

        txtContrasena.doAfterTextChanged {
            inputContrasena.error = null
        }

        btnIniciarSesion.setOnClickListener {

            presenter.iniciarSesion(
                identificadorUsuario =
                    txtIdentificador.text
                        ?.toString()
                        .orEmpty(),

                contrasena =
                    txtContrasena.text
                        ?.toString()
                        .orEmpty()
            )
        }

        findViewById<MaterialButton>(R.id.btn_registrarse).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    override fun mostrarCargando() {

        cambiarEstadoBoton(
            habilitado = false,
            texto = "Validando..."
        )
    }

    override fun ocultarCargando() {

        cambiarEstadoBoton(
            habilitado = true,
            texto = getString(
                R.string.btn_iniciar_sesion
            )
        )
    }

    override fun limpiarErrores() {

        inputIdentificador.error = null
        inputContrasena.error = null
    }

    override fun mostrarErrorIdentificador(
        mensaje: String
    ) {

        inputIdentificador.error = mensaje
    }

    override fun mostrarErrorContrasena(
        mensaje: String
    ) {

        inputContrasena.error = mensaje
    }

    override fun mostrarInicioSesionExitoso() {

        Toast.makeText(
            this,
            "Inicio de sesión correcto.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun mostrarAccesoAutorizado() {

        Toast.makeText(
            this,
            "Acceso autorizado a la API.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun mostrarMensaje(
        mensaje: String
    ) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun mostrarError(
        mensaje: String
    ) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun abrirPantallaPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun cambiarEstadoBoton(
        habilitado: Boolean,
        texto: String
    ) {

        val btnIniciarSesion =
            findViewById<MaterialButton>(
                R.id.btn_iniciar_sesion
            )

        btnIniciarSesion.isEnabled =
            habilitado

        btnIniciarSesion.text =
            texto
    }

    private fun configurarInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.layout_principal)
        ) { vista, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            vista.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    override fun onDestroy() {

        if (::presenter.isInitialized) {
            presenter.destruir()
        }

        super.onDestroy()
    }
}