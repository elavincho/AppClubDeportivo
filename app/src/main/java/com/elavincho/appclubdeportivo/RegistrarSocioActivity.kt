package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    // Variable para pasar el id del socio al apto físico
    private var idSocioActual: Int = -1

    // Declarar las variables para las vistas
    private lateinit var txtTipo: TextView
    private lateinit var spinnerTipoSocio: Spinner
    private lateinit var spinnerTipoDoc: Spinner
    private lateinit var edTxtNombre: EditText
    private lateinit var edTxtApellido: EditText
    private lateinit var edTxtNroDoc: EditText
    private lateinit var edTxtFechaNac: EditText
    private lateinit var edTxtTelefono: EditText
    private lateinit var edTxtMail: EditText
    private lateinit var edTxtDireccion: EditText

    private lateinit var btnOk: ImageView
    private lateinit var btnInicio: ImageView
    private lateinit var btnCerrar: ImageView

    private lateinit var btnBack: ImageView

    // Arrays para los Spinners
    private val tiposSocio = arrayOf("Socio", "No Socio")
    private val tiposDocumento = arrayOf("DNI", "Cédula", "Pasaporte", "LC", "LE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_socio)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        /*Botón Apto Físico*/
        val btnAptoFisico = findViewById<Button>(R.id.btnAptoFisico)
        btnAptoFisico.setOnClickListener {
            if (idSocioActual != -1) {
                val intentAptoFisico = Intent(this, AptoFisicoActivity::class.java)
                intentAptoFisico.putExtra("socio_id", idSocioActual)
                startActivity(intentAptoFisico)
            } else {
                Toast.makeText(this, "Primero guarde el socio para cargar el apto físico", Toast.LENGTH_LONG).show()
            }
        }

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar Spinners
        configurarSpinners()

        // Configurar los listeners de los botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        // TextView para Tipo (fijo)
        txtTipo = findViewById(R.id.txtTipo)

        // Spinners
        spinnerTipoSocio = findViewById(R.id.spinnerTipoSocio)
        spinnerTipoDoc = findViewById(R.id.spinnerTipoDoc)

        // EditTexts
        edTxtNombre = findViewById(R.id.edTxtNombre)
        edTxtApellido = findViewById(R.id.edTxtApellido)
        edTxtNroDoc = findViewById(R.id.edTxtNroDoc)
        edTxtFechaNac = findViewById(R.id.edTxtFechaNac)
        edTxtTelefono = findViewById(R.id.edTxtTelefono)
        edTxtMail = findViewById(R.id.edTxtMail)
        edTxtDireccion = findViewById(R.id.edTxtDireccion)

        // Botones
        btnOk = findViewById(R.id.btnOk)
        btnInicio = findViewById(R.id.btnInicio)
        btnCerrar = findViewById(R.id.btnCerrar)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun configurarSpinners() {

        // Adaptador para el Spinner de Tipo Socio
        val adapterTipoSocio = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposSocio)
        adapterTipoSocio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoSocio.adapter = adapterTipoSocio

        // Adaptador para el Spinner de Tipo Documento
        val adapterTipoDoc = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposDocumento)
        adapterTipoDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDoc.adapter = adapterTipoDoc

        // Listener para el Spinner de Tipo Socio
        spinnerTipoSocio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Aquí puedes manejar la selección si es necesario
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        // Listener para el Spinner de Tipo Documento
        spinnerTipoDoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Aquí puedes manejar la selección si es necesario
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    private fun configurarBotones() {
        // Botón OK - Guardar socio
        btnOk.setOnClickListener {
            guardarSocio()
        }

        btnBack.setOnClickListener {
            finish()
        }

        // Botón Cerrar - Confirmar antes de salir
        btnCerrar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar Salida")
            builder.setMessage("¿Está seguro de que desea salir del registro de socio?")

            builder.setPositiveButton("Salir") { dialog, which ->
                val intent = Intent(this, PantallaPrincipalActivity::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                // No hacer nada, permanecer en la actividad
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun guardarSocio() {
        // Obtener los valores de los Spinners y EditText
        val tipo = "TIPO:" // Valor fijo ya que eliminamos el Spinner de Tipo
        val tipoSocio = spinnerTipoSocio.selectedItem.toString()
        val tipoDoc = spinnerTipoDoc.selectedItem.toString()
        val nombre = edTxtNombre.text.toString().trim()
        val apellido = edTxtApellido.text.toString().trim()
        val nroDoc = edTxtNroDoc.text.toString().trim()
        val fechaNac = edTxtFechaNac.text.toString().trim()
        val telefono = edTxtTelefono.text.toString().trim()
        val mail = edTxtMail.text.toString().trim()
        val direccion = edTxtDireccion.text.toString().trim()

        // Validar campos obligatorios
        if (nombre.isEmpty()) {
            mostrarMensaje("El nombre es obligatorio")
            edTxtNombre.requestFocus()
            return
        }

        if (apellido.isEmpty()) {
            mostrarMensaje("El apellido es obligatorio")
            edTxtApellido.requestFocus()
            return
        }

        if (nroDoc.isEmpty()) {
            mostrarMensaje("El número de documento es obligatorio")
            edTxtNroDoc.requestFocus()
            return
        }

        if (fechaNac.isEmpty()) {
            mostrarMensaje("La fecha de nacimiento es obligatoria")
            edTxtFechaNac.requestFocus()
            return
        }

        if (telefono.isEmpty()) {
            mostrarMensaje("El teléfono es obligatorio")
            edTxtTelefono.requestFocus()
            return
        }

        if (mail.isEmpty()) {
            mostrarMensaje("El mail es obligatorio")
            edTxtMail.requestFocus()
            return
        }

        if (direccion.isEmpty()) {
            mostrarMensaje("La dirección es obligatoria")
            edTxtDireccion.requestFocus()
            return
        }

        // Insertar en la base de datos
        try {
            val resultado = dbHelper.agregarSocio(
                tipo, tipoSocio, nombre, apellido, tipoDoc, nroDoc,
                fechaNac, telefono, mail, direccion
            )

            if (resultado != -1L) {
                // GUARDAR EL ID LOCALMENTE
                idSocioActual = resultado.toInt()
                mostrarMensaje("Socio guardado correctamente - ID: $idSocioActual")

                // Esperar un poco para que se vea el mensaje y luego ir al APTO FÍSICO
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, AptoFisicoActivity::class.java)
                    intent.putExtra("socio_id", idSocioActual)
                    startActivity(intent)
                    finish() // Cierra esta actividad
                }, 1000) // 1 segundo de delay
            } else {
                mostrarMensaje("Error al guardar el socio")
            }
        } catch (e: Exception) {
            mostrarMensaje("Error: ${e.message}")
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}