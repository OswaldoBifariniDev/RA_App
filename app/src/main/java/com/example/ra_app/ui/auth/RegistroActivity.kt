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

        val etNombreApellido = findViewById<EditText>(R.id.et_nombre_apellido)
        val etIdentificacion = findViewById<EditText>(R.id.et_identificacion)
        val etCorreoElectronico = findViewById<EditText>(R.id.et_correo_electronico)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena)
        val btnRegistrar = findViewById<Button>(R.id.btn_registrar)
        val txtIrLogin = findViewById<TextView>(R.id.txt_ir_login)

        btnRegistrar.setOnClickListener {
            val nombre = etNombreApellido.text.toString()
            val identificacion = etIdentificacion.text.toString()
            val correo = etCorreoElectronico.text.toString()
            val contrasena = etContrasena.text.toString()

            viewModel.validarRegistro(nombre, identificacion, correo, contrasena)
        }

        txtIrLogin.setOnClickListener {
            // CONEXION: Navegar a LoginActivity
        }

        viewModel.registroExitoso.observe(this) { exito ->
            if (exito) {
                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_LONG).show()
                // CONEXION: Navegar a MainMenuActivity
            }
        }

        viewModel.mensajeError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}