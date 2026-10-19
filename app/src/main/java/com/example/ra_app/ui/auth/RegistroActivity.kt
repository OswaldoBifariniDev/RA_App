package com.example.ra_app.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ra_app.R

class RegistroActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val campoNombreApellido = findViewById<EditText>(R.id.et_nombre_apellido)
        val campoIdentificacion = findViewById<EditText>(R.id.et_identificacion)
        val campoCorreoElectronico = findViewById<EditText>(R.id.et_correo_electronico)
        val campoContrasena = findViewById<EditText>(R.id.et_contrasena)
        val botonRegistrar = findViewById<Button>(R.id.btn_registrar)
        val enlaceIniciarSesion = findViewById<TextView>(R.id.txt_ir_login)

        botonRegistrar.setOnClickListener {
            val nombre = campoNombreApellido.text.toString()
            val identificacion = campoIdentificacion.text.toString()
            val correo = campoCorreoElectronico.text.toString()
            val contrasena = campoContrasena.text.toString()

            viewModel.validarRegistro(nombre, identificacion, correo, contrasena)
        }

        enlaceIniciarSesion.setOnClickListener {
            // CONEXION: Navegar a LoginActivity
        }

        viewModel.registroExitoso.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_LONG).show()
                // CONEXION: Navegar a MainMenuActivity
            }
        }

        viewModel.mensajeError.observe(this) { recursoMensaje ->
            Toast.makeText(this, getString(recursoMensaje), Toast.LENGTH_SHORT).show()
        }
    }
}
