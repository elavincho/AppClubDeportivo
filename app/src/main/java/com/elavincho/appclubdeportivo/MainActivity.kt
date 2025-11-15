package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnAcceder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar eventos
        configurarEventos()


    }

    private fun inicializarVistas() {
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnAcceder = findViewById(R.id.btnAcceder)
    }

    private fun configurarEventos() {
        /* Botón Acceder */
        btnAcceder.setOnClickListener {
            realizarLogin()
        }
    }

    private fun realizarLogin() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        // Validaciones
        if (username.isEmpty()) {
            edtUsername.error = "Ingrese un usuario"
            edtUsername.requestFocus()
            return
        }

        if (password.isEmpty()) {
            edtPassword.error = "Ingrese una contraseña"
            edtPassword.requestFocus()
            return
        }

        try {
            // Verificar credenciales en la base de datos
            val credencialesValidas = dbHelper.verificarUsuario(username, password)

            if (credencialesValidas) {
                // Login exitoso
                Toast.makeText(this, "✅ Login exitoso", Toast.LENGTH_SHORT).show()

                // Inicializar socios con vencimiento hoy
                android.util.Log.d("MAIN_ACTIVITY", "Inicializando socios con vencimiento hoy...")
                dbHelper.inicializarSociosConVencimientoHoy()

                // Verificar que los datos se crearon correctamente
                android.util.Log.d("MAIN_ACTIVITY", "Verificando datos de prueba...")
                dbHelper.verificarDatosDePrueba()

                // Navegar a la pantalla principal
                val intent = Intent(this, PantallaPrincipalActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Login fallido
                Toast.makeText(this, "❌ Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                edtPassword.text.clear()
                edtPassword.requestFocus()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error en la base de datos: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("LOGIN_ERROR", "Error durante login: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}