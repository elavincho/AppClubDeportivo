package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import org.w3c.dom.Text

import android.widget.EditText
import android.widget.Toast

import android.os.Handler
import android.os.Looper

class RegistrarSocioActivity : AppCompatActivity() {
<<<<<<< HEAD
=======
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()

      val edTxtNombre = findViewById<TextView>(R.id.edTxtNombre)
      val edTxtApellido = findViewById<TextView>(R.id.edTxtApellido)
      val edTxtNroDoc = findViewById<TextView>(R.id.edTxtNroDoc)
      val edTxtFechaNac = findViewById<TextView>(R.id.edTxtFechaNac)
      val edTxtTelefono = findViewById<TextView>(R.id.edTxtTelefono)
      val edTxtMail = findViewById<TextView>(R.id.edTxtMail)
      val edTxtDireccion = findViewById<TextView>(R.id.edTxtDireccion)

      setContentView(R.layout.activity_registrar_socio)
      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }
>>>>>>> 33a2d9d11d6bbfdc138365bd32e00314154d2d38

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*Botón Inicio (img_casa)*/

        val btnInicio = findViewById<ImageView>(R.id.btnInicio)

        btnInicio.setOnClickListener {
            val intentPantallaPrincipal = Intent(this, PantallaPrincipalActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentPantallaPrincipal)
        }

<<<<<<< HEAD
        /*Botón Apto Físico*/

        val btnAptoFisico = findViewById<Button>(R.id.btnAptoFisico)

        btnAptoFisico.setOnClickListener {
            val intentAptoFisico = Intent(this, AptoFisicoActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentAptoFisico)
        }


        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Verificar estructura de la tabla
        dbHelper.verificarEstructuraTabla()

        // Inicializar los EditText
        inicializarVistas()

        // Configurar los listeners de los botones
        configurarBotones()


    }


    // Declarar las variables para los EditText
    private lateinit var edTxtTipo: EditText
    private lateinit var edTxtTipoSocio: EditText
    private lateinit var edTxtNombre: EditText
    private lateinit var edTxtApellido: EditText
    private lateinit var edTxtTipoDoc: EditText
    private lateinit var edTxtNroDoc: EditText
    private lateinit var edTxtFechaNac: EditText
    private lateinit var edTxtTelefono: EditText
    private lateinit var edTxtMail: EditText
    private lateinit var edTxtDireccion: EditText

    private lateinit var btnOk: ImageView
    private lateinit var btnInicio: ImageView
    private lateinit var btnCerrar: ImageView


    private fun inicializarVistas() {
        edTxtTipo = findViewById(R.id.edTxtTipo)
        edTxtTipoSocio = findViewById(R.id.edTxtTipoSocio)
        edTxtNombre = findViewById(R.id.edTxtNombre)
        edTxtApellido = findViewById(R.id.edTxtApellido)
        edTxtTipoDoc = findViewById(R.id.edTxtTipoDoc)
        edTxtNroDoc = findViewById(R.id.edTxtNroDoc)
        edTxtFechaNac = findViewById(R.id.edTxtFechaNac)
        edTxtTelefono = findViewById(R.id.edTxtTelefono)
        edTxtMail = findViewById(R.id.edTxtMail)
        edTxtDireccion = findViewById(R.id.edTxtDireccion)

        btnOk = findViewById(R.id.btnOk)
        btnInicio = findViewById(R.id.btnInicio)
        btnCerrar = findViewById(R.id.btnCerrar)
    }

    private fun configurarBotones() {
        // Botón OK - Guardar socio
        btnOk.setOnClickListener {
            guardarSocio()
        }

        // Botón Inicio - Volver al inicio
        btnInicio.setOnClickListener {
            finish() // o la lógica para volver a la actividad principal
        }

        // Botón Cerrar - Limpiar campos o cerrar
        btnCerrar.setOnClickListener {
            limpiarCampos()
        }
    }

    private fun guardarSocio() {
        // Obtener los valores de los EditText
        val tipo = edTxtTipo.text.toString().trim()
        val tipoSocio = edTxtTipoSocio.text.toString().trim()
        val nombre = edTxtNombre.text.toString().trim()
        val apellido = edTxtApellido.text.toString().trim()
        val tipoDoc = edTxtTipoDoc.text.toString().trim()
        val nroDoc = edTxtNroDoc.text.toString().trim()
        val fechaNac = edTxtFechaNac.text.toString().trim()
        val telefono = edTxtTelefono.text.toString().trim()
        val mail = edTxtMail.text.toString().trim()
        val direccion = edTxtDireccion.text.toString().trim()

        // Validar campos obligatorios
        if (nombre.isEmpty()) {
            mostrarMensaje("El nombre es obligatorio")
            return
        }

        if (nroDoc.isEmpty()) {
            mostrarMensaje("El número de documento es obligatorio")
            return
        }

        // Insertar en la base de datos
        try {
            val resultado = dbHelper.agregarSocio(
                tipo, tipoSocio, nombre, apellido, tipoDoc, nroDoc,
                fechaNac, telefono, mail, direccion
            )

            if (resultado != -1L) {
                mostrarMensaje("Socio guardado correctamente")

                // Esperar un poco para que se vea el mensaje y luego ir a la lista
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, ListaSocioActivity::class.java)
                    startActivity(intent)
                    finish() // Opcional
                }, 1000) // 1 segundo de delay
            } else {
                mostrarMensaje("Error al guardar el socio")
            }
        } catch (e: Exception) {
            mostrarMensaje("Error: ${e.message}")
        }
    }

    private fun limpiarCampos() {
        edTxtTipo.setText("")
        edTxtTipoSocio.setText("")
        edTxtNombre.setText("")
        edTxtApellido.setText("")
        edTxtTipoDoc.setText("")
        edTxtNroDoc.setText("")
        edTxtFechaNac.setText("")
        edTxtTelefono.setText("")
        edTxtMail.setText("")
        edTxtDireccion.setText("")

        // Opcional: restaurar los hints si es necesario
        edTxtTipo.hint = "Tipo"
        edTxtTipoSocio.hint = "Socio / No Socio"
        edTxtNombre.hint = "Nombre"
        edTxtApellido.hint = "Apellido"
        edTxtTipoDoc.hint = "DNI"
        edTxtNroDoc.hint = "Número"
        edTxtFechaNac.hint = "Fecha Nac."
        edTxtTelefono.hint = "Teléfono"
        edTxtMail.hint = "Mail"
        edTxtDireccion.hint = "Dirección"
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
=======
      btnAptoFisico.setOnClickListener {
         val intentAptoFisico= Intent(this, AptoFisicoActivity::class.java)
         /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
         startActivity(intentAptoFisico)

        /* Toast.makeText("contexto", "complete los campos",5)*/
      }
>>>>>>> 33a2d9d11d6bbfdc138365bd32e00314154d2d38


}