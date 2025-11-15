package com.elavincho.appclubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetalleSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var socioId: Int = -1
    private lateinit var socio: Socio

    // Declarar TextViews del socio
    private lateinit var txtSocioNumero: TextView
    private lateinit var txtNombreCompleto: TextView
    private lateinit var txtDocumento: TextView
    private lateinit var txtFechaNacimiento: TextView
    private lateinit var txtTelefono: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtDireccion: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtTipoSocio: TextView

    // Declarar TextViews del apto físico
    private lateinit var txtEstadoApto: TextView
    private lateinit var txtFechaVencimiento: TextView
    private lateinit var txtMedico: TextView
    private lateinit var txtMatricula: TextView

    private lateinit var btnGestionarApto: Button

    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_socio)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Obtener el ID del socio desde el intent
        socioId = intent.getIntExtra("socio_id", -1)
        if (socioId == -1) {
            Toast.makeText(this, "Error: No se encontró el socio", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Inicializar vistas
        inicializarVistas()

        // Cargar datos del socio
        cargarDatosSocio()

        // Cargar datos del apto físico
        cargarDatosAptoFisico()

        // Configurar botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        // TextViews del socio
        txtSocioNumero = findViewById(R.id.txtSocioNumero)
        txtNombreCompleto = findViewById(R.id.txtNombreCompleto)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtEmail = findViewById(R.id.txtEmail)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtTipoSocio = findViewById(R.id.txtTipoSocio)

        // TextViews del apto físico
        txtEstadoApto = findViewById(R.id.txtEstadoApto)
        txtFechaVencimiento = findViewById(R.id.txtFechaVencimiento)
        txtMedico = findViewById(R.id.txtMedico)
        txtMatricula = findViewById(R.id.txtMatricula)

        // Botones
        btnGestionarApto = findViewById(R.id.btnGestionarApto)
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

        // Mostrar datos en los TextViews
        txtSocioNumero.text = socio.id.toString()
        txtNombreCompleto.text = "${socio.nombre} ${socio.apellido}"
        txtDocumento.text = "${socio.tipoDoc}: ${socio.nroDoc}"
        txtFechaNacimiento.text = socio.fechaNac
        txtTelefono.text = socio.telefono
        txtEmail.text = socio.mail
        txtDireccion.text = socio.direccion
        txtTipoSocio.text = socio.tipoSocio
    }

    private fun cargarDatosAptoFisico() {
        val ultimoApto = dbHelper.obtenerUltimoAptoFisico(socioId)

        if (ultimoApto != null) {
            // Hay apto físico registrado
            txtEstadoApto.text = if (ultimoApto.esApto) "APTO" else "NO APTO"
            txtEstadoApto.setTextColor(if (ultimoApto.esApto) Color.GREEN else Color.RED)

            txtFechaVencimiento.text = ultimoApto.fechaVencimiento
            txtMedico.text = "${ultimoApto.medicoNombre} ${ultimoApto.medicoApellido}"
            txtMatricula.text = ultimoApto.medicoMatricula

            btnGestionarApto.text = "Editar Apto Físico"
        } else {
            // No hay apto físico registrado
            txtEstadoApto.text = "NO REGISTRADO"
            txtEstadoApto.setTextColor(Color.GRAY)

            txtFechaVencimiento.text = "No registrado"
            txtMedico.text = "No registrado"
            txtMatricula.text = "No registrado"

            btnGestionarApto.text = "Cargar Apto Físico"
        }
    }

    private fun configurarBotones() {
        // Botón Gestionar Apto Físico
        btnGestionarApto.setOnClickListener {
            val intent = Intent(this, AptoFisicoActivity::class.java)
            intent.putExtra("socio_id", socioId)
            startActivity(intent)
        }

        // Botón Atras
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanuda (por si se editó el apto físico)
        cargarDatosAptoFisico()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}