package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComprobantePagoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var vieneDeLista: Boolean = false
    private var socioId: Int = 0
    private var socioNombre: String = ""
    private var socioApellido: String = ""
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante_pago)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Inicializar DBHelper (el mismo que usas en CobrarCuotaActivity)
        dbHelper = DBHelper(this)

        // Obtener datos del intent
        val datosComprobante = obtenerDatosDelIntent()

        // Verificar de dónde viene la actividad
        verificarOrigen()

        // Mostrar datos en la interfaz
        mostrarDatosEnUI(datosComprobante)

        // Guardar comprobante en la base de datos
        guardarComprobanteEnDB(datosComprobante)

        // Configurar botón volver
        configurarBotonVolver()

        // Configurar botones de descargar y compartir
        configurarBotonesAcciones(datosComprobante)
    }

    private fun obtenerDatosDelIntent(): ComprobantePago {
        return ComprobantePago(
            fecha = intent.getStringExtra("fecha") ?: "Fecha no disponible",
            numeroSocio = intent.getStringExtra("numeroSocio") ?: "No especificado",
            nombre = intent.getStringExtra("nombre") ?: "No especificado",
            apellido = intent.getStringExtra("apellido") ?: "No especificado",
            actividad = intent.getStringExtra("actividad") ?: "No especificado",
            fechaVencimiento = intent.getStringExtra("fechaVencimiento") ?: "No especificado",
            metodoPago = intent.getStringExtra("metodoPago") ?: "No especificado",
            cuota = intent.getStringExtra("cuota") ?: "No especificado",
            importe = intent.getStringExtra("importe") ?: "No especificado"
        )
    }

    private fun verificarOrigen() {
        // Verificar si viene desde la lista de comprobantes
        vieneDeLista = intent.getBooleanExtra("desde_lista", false)

        if (vieneDeLista) {
            // Si viene de la lista, obtener datos del socio para poder volver
            socioId = intent.getIntExtra("socio_id", 0)
            socioNombre = intent.getStringExtra("socio_nombre") ?: ""
            socioApellido = intent.getStringExtra("socio_apellido") ?: ""
        }
    }

    private fun mostrarDatosEnUI(comprobante: ComprobantePago) {
        // Asignar datos a los TextViews (asegúrate de que tus TextViews tengan estos IDs en el XML)
        findViewById<TextView>(R.id.tvFecha).text = "Fecha Pago: " + comprobante.fecha
        findViewById<TextView>(R.id.tvNumeroSocio).text = "N° Socio: " + comprobante.numeroSocio
        findViewById<TextView>(R.id.tvNombre).text = comprobante.nombre
        findViewById<TextView>(R.id.tvApellido).text = comprobante.apellido
        findViewById<TextView>(R.id.tvActividad).text = "Actividad: " + comprobante.actividad
        findViewById<TextView>(R.id.tvFechaVencimiento).text = "Fecha Vencimiento: " + comprobante.fechaVencimiento
        findViewById<TextView>(R.id.tvMetodoPago).text = comprobante.metodoPago
        findViewById<TextView>(R.id.tvCuota).text = comprobante.cuota
        findViewById<TextView>(R.id.tvImporte).text = comprobante.importe
        btnBack = findViewById(R.id.btnBack)
    }

    private fun configurarBotonVolver() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun guardarComprobanteEnDB(comprobante: ComprobantePago) {
        // Solo guardar si NO viene de la lista (para evitar duplicados)
        if (!vieneDeLista) {
            try {
                val id = dbHelper.agregarComprobante(comprobante)
                if (id != -1L) {
                    Toast.makeText(this, "Comprobante guardado correctamente", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Error al guardar el comprobante", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun configurarBotonesAcciones(comprobante: ComprobantePago) {
        val btnDescargar = findViewById<ImageView>(R.id.btnDescargar)
        val btnCompartir = findViewById<ImageView>(R.id.btnCompartir)

        btnDescargar.setOnClickListener {
            Toast.makeText(this, "Descargando comprobante...", Toast.LENGTH_SHORT).show()
        }

        btnCompartir.setOnClickListener {
            compartirComprobante(comprobante)
        }
    }

    private fun compartirComprobante(comprobante: ComprobantePago) {
        val textoComprobante = """
            COMPROBANTE DE PAGO
            Fecha: ${comprobante.fecha}
            Socio Número: ${comprobante.numeroSocio}
            Nombre: ${comprobante.nombre}
            Apellido: ${comprobante.apellido}
            Actividad: ${comprobante.actividad}
            Fecha Venc.: ${comprobante.fechaVencimiento}
            Método de Pago: ${comprobante.metodoPago}
            Cuota: ${comprobante.cuota}
            Importe: ${comprobante.importe}
        """.trimIndent()

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoComprobante)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Compartir comprobante"))
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}