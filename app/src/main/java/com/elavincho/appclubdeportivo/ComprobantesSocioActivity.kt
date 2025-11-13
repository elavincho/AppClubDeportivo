package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ComprobantesSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerViewComprobantes: RecyclerView
    private lateinit var tvInfoSocio: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnInicio: ImageView

    private var socioId: Int = 0
    private var socioNombre: String = ""
    private var socioApellido: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobantes_socio)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Obtener datos del socio del intent
        obtenerDatosSocio()

        // Inicializar vistas
        inicializarVistas()

        // Configurar información del socio
        configurarInfoSocio()

        // Cargar comprobantes
        cargarComprobantes()

        // Configurar botones
        configurarBotones()
    }

    private fun obtenerDatosSocio() {
        socioId = intent.getIntExtra("socio_id", 0)
        socioNombre = intent.getStringExtra("socio_nombre") ?: ""
        socioApellido = intent.getStringExtra("socio_apellido") ?: ""

        if (socioId == 0) {
            Toast.makeText(this, "Error: No se recibió información del socio", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun inicializarVistas() {
        recyclerViewComprobantes = findViewById(R.id.recyclerViewComprobantes)
        tvInfoSocio = findViewById(R.id.tvInfoSocio)
        btnBack = findViewById(R.id.btnBack)
        btnInicio = findViewById(R.id.btnInicio)

        // Configurar RecyclerView
        recyclerViewComprobantes.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarInfoSocio() {
        tvInfoSocio.text = "Socio: $socioNombre $socioApellido (ID: $socioId)"
    }

    private fun cargarComprobantes() {
        val todosComprobantes = dbHelper.obtenerTodosLosComprobantes()

        // Filtrar comprobantes por el socio actual
        val comprobantesDelSocio = todosComprobantes.filter {
            it.numeroSocio == socioId.toString()
        }

        if (comprobantesDelSocio.isEmpty()) {
            Toast.makeText(this, "No hay comprobantes de pago para este socio", Toast.LENGTH_LONG).show()
        }

        val adapter = ComprobanteAdapter(comprobantesDelSocio) { comprobante ->
            // Al hacer clic en un comprobante, abrir el detalle
            verDetalleComprobante(comprobante)
        }
        recyclerViewComprobantes.adapter = adapter
    }

    private fun verDetalleComprobante(comprobante: ComprobantePago) {
        val intent = Intent(this, ComprobantePagoActivity::class.java).apply {
            // Pasar todos los datos del comprobante
            putExtra("fecha", comprobante.fecha)
            putExtra("numeroSocio", comprobante.numeroSocio)
            putExtra("nombre", comprobante.nombre)
            putExtra("apellido", comprobante.apellido)
            putExtra("actividad", comprobante.actividad)
            putExtra("fechaVencimiento", comprobante.fechaVencimiento)
            putExtra("metodoPago", comprobante.metodoPago)
            putExtra("cuota", comprobante.cuota)
            putExtra("importe", comprobante.importe)
            // Indicar que viene desde la lista de comprobantes
            putExtra("desde_lista", true)
            // Pasar datos del socio para poder volver
            putExtra("socio_id", socioId)
            putExtra("socio_nombre", socioNombre)
            putExtra("socio_apellido", socioApellido)
        }
        startActivity(intent)
    }

    private fun configurarBotones() {
        btnBack.setOnClickListener {
            finish()
        }

        btnInicio.setOnClickListener {
            // Navegar al inicio
            val intent = Intent(this, PantallaPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}