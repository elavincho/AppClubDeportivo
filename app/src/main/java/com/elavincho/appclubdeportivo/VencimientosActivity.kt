package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class VencimientosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerViewVencimientos: RecyclerView
    private lateinit var txtFecha: TextView
    private lateinit var txtContadorVencimientos: TextView
    private lateinit var txtMensajeVacio: TextView
    private lateinit var btnInicio: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar fecha actual
        configurarFecha()

        // Cargar vencimientos
        cargarVencimientosHoy()

        // Configurar botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        recyclerViewVencimientos = findViewById(R.id.recyclerViewVencimientos)
        txtFecha = findViewById(R.id.txtFecha)
        txtContadorVencimientos = findViewById(R.id.txtContadorVencimientos)
        txtMensajeVacio = findViewById(R.id.txtMensajeVacio)
        btnInicio = findViewById(R.id.btnInicio)

        // Configurar RecyclerView
        recyclerViewVencimientos.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarFecha() {
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        txtFecha.text = "Fecha: $fechaActual"
    }

    private fun cargarVencimientosHoy() {
        // Obtener fecha actual en formato dd/MM/yyyy
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Obtener todos los comprobantes
        val todosComprobantes = dbHelper.obtenerTodosLosComprobantes()

        // Filtrar comprobantes que vencen hoy y son de socios (no "No Socio")
        val comprobantesVencenHoy = todosComprobantes.filter { comprobante ->
            comprobante.fechaVencimiento == fechaHoy &&
                    comprobante.cuota.contains("Mensual", ignoreCase = true)
        }

        // Obtener IDs únicos de socios que vencen hoy
        val sociosIdsVencenHoy = comprobantesVencenHoy.map { it.numeroSocio.toInt() }.toSet()

        // Obtener datos completos de los socios
        val todosSocios = dbHelper.obtenerTodosLosSocios()
        val sociosVencenHoy = todosSocios.filter { socio ->
            sociosIdsVencenHoy.contains(socio.id) && socio.tipoSocio == "Socio"
        }

        // Mostrar resultados
        if (sociosVencenHoy.isNotEmpty()) {
            txtMensajeVacio.visibility = TextView.GONE
            recyclerViewVencimientos.visibility = RecyclerView.VISIBLE

            val adapter = VencimientoAdapter(sociosVencenHoy) { socio ->
                verDetalleSocio(socio)
            }
            recyclerViewVencimientos.adapter = adapter

            txtContadorVencimientos.text = "Socios con vencimiento hoy: ${sociosVencenHoy.size}"
            txtContadorVencimientos.setTextColor(getColor(android.R.color.holo_red_dark))

        } else {
            txtMensajeVacio.visibility = TextView.VISIBLE
            recyclerViewVencimientos.visibility = RecyclerView.GONE
            txtContadorVencimientos.text = "No hay socios con vencimiento hoy"
            txtContadorVencimientos.setTextColor(getColor(android.R.color.holo_green_dark))
        }
    }

    private fun verDetalleSocio(socio: Socio) {
        // Aquí puedes navegar al detalle del socio o mostrar un diálogo
        val mensaje = """
            📋 Socio con vencimiento hoy:
            
            👤 ${socio.nombre} ${socio.apellido}
            🆔 Socio N°: ${socio.id}
            📄 ${socio.tipoDoc}: ${socio.nroDoc}
            📞 Teléfono: ${socio.telefono}
            
            ⚠️ La cuota vence hoy
        """.trimIndent()

        Toast.makeText(this, "Socio: ${socio.nombre} ${socio.apellido}", Toast.LENGTH_LONG).show()

        // Opcional: Navegar al detalle del socio
        // val intent = Intent(this, DetalleSocioActivity::class.java)
        // intent.putExtra("socio_id", socio.id)
        // startActivity(intent)
    }

    private fun configurarBotones() {
        btnInicio.setOnClickListener {
            val intentPantallaPrincipal = Intent(this, PantallaPrincipalActivity::class.java)
            startActivity(intentPantallaPrincipal)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar vencimientos cuando la actividad se reanuda
        cargarVencimientosHoy()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}