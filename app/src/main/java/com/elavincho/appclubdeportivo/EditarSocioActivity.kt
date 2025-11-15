package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class EditarSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var socioId: Int = -1
    private lateinit var socio: Socio

    // Declarar vistas
    //private lateinit var edtTipo: TextInputEditText
    private lateinit var edtTipoSocio: TextInputEditText
    private lateinit var edtNombre: TextInputEditText
    private lateinit var edtApellido: TextInputEditText
    private lateinit var edtTipoDoc: TextInputEditText
    private lateinit var edtNroDoc: TextInputEditText
    private lateinit var edtFechaNac: TextInputEditText
    private lateinit var edtTelefono: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtDireccion: TextInputEditText
    private lateinit var btnCancelar: ImageView
    private lateinit var btnGuardar: ImageView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_socio)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Obtener el ID del socio desde el intent
        socioId = intent.getIntExtra("socio_id", -1)
        if (socioId == -1) {
            Toast.makeText(this, "Error: No se recibió información del socio", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Inicializar vistas
        inicializarVistas()

        // Cargar datos del socio
        cargarDatosSocio()

        // Configurar botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        //edtTipo = findViewById(R.id.edtTipo)
        edtTipoSocio = findViewById(R.id.edtTipoSocio)
        edtNombre = findViewById(R.id.edtNombre)
        edtApellido = findViewById(R.id.edtApellido)
        edtTipoDoc = findViewById(R.id.edtTipoDoc)
        edtNroDoc = findViewById(R.id.edtNroDoc)
        edtFechaNac = findViewById(R.id.edtFechaNac)
        edtTelefono = findViewById(R.id.edtTelefono)
        edtEmail = findViewById(R.id.edtEmail)
        edtDireccion = findViewById(R.id.edtDireccion)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun cargarDatosSocio() {
        // Obtener todos los socios y buscar el que coincide con el ID
        val listaSocios = dbHelper.obtenerTodosLosSocios()
        socio = listaSocios.find { it.id == socioId } ?: run {
            Toast.makeText(this, "Error: Socio no encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cargar datos en los campos
        //edtTipo.setText(socio.tipo)
        edtTipoSocio.setText(socio.tipoSocio)
        edtNombre.setText(socio.nombre)
        edtApellido.setText(socio.apellido)
        edtTipoDoc.setText(socio.tipoDoc)
        edtNroDoc.setText(socio.nroDoc)
        edtFechaNac.setText(socio.fechaNac)
        edtTelefono.setText(socio.telefono)
        edtEmail.setText(socio.mail)
        edtDireccion.setText(socio.direccion)
    }

    private fun configurarBotones() {
        btnCancelar.setOnClickListener {
            // Mostrar confirmación antes de cancelar
            mostrarConfirmacionCancelar()
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        // Botón Atras
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun guardarCambios() {
        // Validar campos obligatorios
        if (edtNombre.text.toString().trim().isEmpty()) {
            edtNombre.error = "El nombre es obligatorio"
            edtNombre.requestFocus()
            return
        }

        if (edtNroDoc.text.toString().trim().isEmpty()) {
            edtNroDoc.error = "El número de documento es obligatorio"
            edtNroDoc.requestFocus()
            return
        }

        // Crear objeto Socio con los datos actualizados
        val socioActualizado = Socio(
            id = socioId,
            //tipo = edtTipo.text.toString().trim(),
            tipoSocio = edtTipoSocio.text.toString().trim(),
            nombre = edtNombre.text.toString().trim(),
            apellido = edtApellido.text.toString().trim(),
            tipoDoc = edtTipoDoc.text.toString().trim(),
            nroDoc = edtNroDoc.text.toString().trim(),
            fechaNac = edtFechaNac.text.toString().trim(),
            telefono = edtTelefono.text.toString().trim(),
            mail = edtEmail.text.toString().trim(),
            direccion = edtDireccion.text.toString().trim()
        )

        // Actualizar en la base de datos
        val resultado = actualizarSocioEnBD(socioActualizado)

        if (resultado) {
            Toast.makeText(this, "✅ Socio actualizado correctamente", Toast.LENGTH_LONG).show()

            // Enviar resultado a la actividad anterior
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "❌ Error al actualizar el socio", Toast.LENGTH_LONG).show()
        }
    }

    private fun actualizarSocioEnBD(socio: Socio): Boolean {
        val db = dbHelper.writableDatabase
        var resultado = false

        try {
            val values = android.content.ContentValues().apply {
                //put("tipo", socio.tipo)
                put("tipo_socio", socio.tipoSocio)
                put("nombre", socio.nombre)
                put("apellido", socio.apellido)
                put("tipo_doc", socio.tipoDoc)
                put("nro_doc", socio.nroDoc)
                put("fecha_nac", socio.fechaNac)
                put("telefono", socio.telefono)
                put("mail", socio.mail)
                put("direccion", socio.direccion)
            }

            val filasAfectadas = db.update(
                "socios",
                values,
                "id = ?",
                arrayOf(socio.id.toString())
            )

            resultado = filasAfectadas > 0

            if (resultado) {
                android.util.Log.d("EDICION", "Socio actualizado: ${socio.nombre} ${socio.apellido}")
            } else {
                android.util.Log.e("EDICION", "Error actualizando socio ID: ${socio.id}")
            }

        } catch (e: Exception) {
            android.util.Log.e("EDICION", "Error en actualizarSocioEnBD: ${e.message}")
            e.printStackTrace()
        } finally {
            db.close()
        }

        return resultado
    }

    private fun mostrarConfirmacionCancelar() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirmar Cancelación")
            .setMessage("¿Está seguro que desea salir? Se perderán los cambios no guardados.")
            .setPositiveButton("Salir") { dialog, which ->
                finish()
            }
            .setNegativeButton("Seguir Editando", null)
            .show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}