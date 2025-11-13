package com.elavincho.appclubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class CobrarCuotaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var socioSeleccionado: Socio? = null

    // Declarar vistas
    private lateinit var txtFecha: TextView
    private lateinit var spinnerTipoSocio: Spinner
    private lateinit var edtNumeroSocio: TextInputEditText
    private lateinit var btnBuscarSocio: Button
    private lateinit var txtInfoSocio: TextView
    private lateinit var spinnerActividad: Spinner
    private lateinit var spinnerMetodoPago: Spinner
    private lateinit var edtImporte: TextInputEditText
    private lateinit var txtVencimiento: TextView
    private lateinit var btnCobrarCuota: Button
    private lateinit var btnCerrar: ImageView
    private lateinit var btnInicio: ImageView

    // Arrays para los Spinners
    private val tiposSocio = arrayOf("Socio", "No Socio")
    private val actividades = arrayOf("Seleccione actividad", "Libre", "Spinning", "Yoga", "Boxeo", "Musculación", "Natación", "Pilates")
    private val metodosPago = arrayOf("Seleccione método", "Efectivo", "Tarjeta Débito", "Tarjeta Crédito", "Transferencia", "Mercado Pago")

    // Precios y días de vencimiento
    private val PRECIO_SOCIO = 35000.0
    private val PRECIO_NO_SOCIO = 10000.0
    private val DIAS_VENCIMIENTO_SOCIO = 30
    private val DIAS_VENCIMIENTO_NO_SOCIO = 0 // Vence el mismo día

    // Formato de fecha
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Propiedad para fecha actual
    private val fechaActual: String
        get() = dateFormat.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar fecha actual
        configurarFecha()

        // Configurar Spinners
        configurarSpinners()

        // Configurar listeners
        configurarListeners()
    }

    private fun inicializarVistas() {
        txtFecha = findViewById(R.id.txtFecha)
        spinnerTipoSocio = findViewById(R.id.spinnerTipoSocio)
        edtNumeroSocio = findViewById(R.id.edtNumeroSocio)
        btnBuscarSocio = findViewById(R.id.btnBuscarSocio)
        txtInfoSocio = findViewById(R.id.txtInfoSocio)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        spinnerMetodoPago = findViewById(R.id.spinnerMetodoPago)
        edtImporte = findViewById(R.id.edtImporte)
        txtVencimiento = findViewById(R.id.txtVencimiento)
        btnCobrarCuota = findViewById(R.id.btnCobrarCuota)
        btnCerrar = findViewById(R.id.btnCerrar)
        btnInicio = findViewById(R.id.btnInicio)
    }

    private fun configurarFecha() {
        txtFecha.text = "Fecha: $fechaActual"
    }

    private fun configurarSpinners() {
        // Spinner Tipo Socio
        val adapterTipoSocio = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposSocio)
        adapterTipoSocio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoSocio.adapter = adapterTipoSocio

        // Spinner Actividad
        val adapterActividad = ArrayAdapter(this, android.R.layout.simple_spinner_item, actividades)
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActividad.adapter = adapterActividad

        // Spinner Método Pago
        val adapterMetodoPago = ArrayAdapter(this, android.R.layout.simple_spinner_item, metodosPago)
        adapterMetodoPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodoPago.adapter = adapterMetodoPago
    }

    private fun configurarListeners() {
        // Botón Buscar Socio
        btnBuscarSocio.setOnClickListener {
            buscarSocio()
        }

        // Spinner Tipo Socio - Actualizar precio y vencimiento automáticamente
        spinnerTipoSocio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                actualizarImporteYVencimiento()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Botón Cobrar Cuota
        btnCobrarCuota.setOnClickListener {
            cobrarCuota()
        }

        // Botón Cerrar - Limpiar campos
        btnCerrar.setOnClickListener {
            limpiarCampos()
        }

        // Botón Inicio - Con confirmación para salir
        btnInicio.setOnClickListener {
            mostrarConfirmacionSalir()
        }
    }

    // NUEVO MÉTODO: Mostrar confirmación para salir
    private fun mostrarConfirmacionSalir() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Salida")
            .setMessage("¿Está seguro que desea salir? Se perderán los datos no guardados.")
            .setPositiveButton("Sí, Salir") { dialog, which ->
                // Si confirma, navegar a la pantalla principal
                val intent = Intent(this, PantallaPrincipalActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun buscarSocio() {
        val numeroSocio = edtNumeroSocio.text.toString().trim()

        if (numeroSocio.isEmpty()) {
            mostrarMensaje("Ingrese un número de socio")
            return
        }

        try {
            val socioId = numeroSocio.toInt()
            val listaSocios = dbHelper.obtenerTodosLosSocios()
            socioSeleccionado = listaSocios.find { it.id == socioId }

            if (socioSeleccionado != null) {
                mostrarInfoSocio(socioSeleccionado!!)
                // Seleccionar automáticamente el tipo de socio en el spinner
                val posicion = tiposSocio.indexOf(socioSeleccionado!!.tipoSocio)
                if (posicion != -1) {
                    spinnerTipoSocio.setSelection(posicion)
                }
                actualizarImporteYVencimiento()
            } else {
                txtInfoSocio.text = "Socio no encontrado"
                socioSeleccionado = null
                mostrarMensaje("No se encontró el socio con ID: $socioId")
            }
        } catch (e: NumberFormatException) {
            mostrarMensaje("Ingrese un número válido")
        }
    }

    private fun mostrarInfoSocio(socio: Socio) {
        val info = "Socio #${socio.id}\n${socio.nombre} ${socio.apellido}\n${socio.tipoDoc}: ${socio.nroDoc}\nTipo: ${socio.tipoSocio}"
        txtInfoSocio.text = info
    }

    private fun actualizarImporteYVencimiento() {
        val tipoSocioSeleccionado = spinnerTipoSocio.selectedItem.toString()

        // Actualizar importe
        val importe = when (tipoSocioSeleccionado) {
            "Socio" -> PRECIO_SOCIO
            "No Socio" -> PRECIO_NO_SOCIO
            else -> 0.0
        }
        edtImporte.setText(String.format(Locale.getDefault(), "%.2f", importe))

        // Actualizar vencimiento
        actualizarVencimiento(tipoSocioSeleccionado)
    }

    private fun actualizarVencimiento(tipoSocio: String) {
        val calendario = Calendar.getInstance()

        when (tipoSocio) {
            "Socio" -> {
                // Socio: vence en 30 días
                calendario.add(Calendar.DAY_OF_YEAR, DIAS_VENCIMIENTO_SOCIO)
                val fechaVencimiento = dateFormat.format(calendario.time)
                txtVencimiento.text = "Vencimiento: $fechaVencimiento (30 días)"
                txtVencimiento.setTextColor(getColor(android.R.color.holo_green_dark))
            }
            "No Socio" -> {
                // No Socio: vence el mismo día
                val fechaVencimiento = dateFormat.format(calendario.time)
                txtVencimiento.text = "Vencimiento: $fechaVencimiento (Hoy mismo)"
                txtVencimiento.setTextColor(getColor(android.R.color.holo_red_dark))
            }
            else -> {
                txtVencimiento.text = "Vencimiento: --/--/----"
                txtVencimiento.setTextColor(getColor(android.R.color.secondary_text_dark))
            }
        }
    }

    private fun obtenerFechaVencimiento(tipoSocio: String): String {
        val calendario = Calendar.getInstance()

        return when (tipoSocio) {
            "Socio" -> {
                calendario.add(Calendar.DAY_OF_YEAR, DIAS_VENCIMIENTO_SOCIO)
                dateFormat.format(calendario.time)
            }
            "No Socio" -> {
                dateFormat.format(calendario.time) // Mismo día
            }
            else -> dateFormat.format(calendario.time)
        }
    }

    private fun cobrarCuota() {
        // Validaciones
        if (socioSeleccionado == null) {
            mostrarMensaje("Seleccione un socio primero")
            return
        }

        val actividad = spinnerActividad.selectedItem.toString()
        if (actividad == "Seleccione actividad") {
            mostrarMensaje("Seleccione una actividad")
            return
        }

        val metodoPago = spinnerMetodoPago.selectedItem.toString()
        if (metodoPago == "Seleccione método") {
            mostrarMensaje("Seleccione un método de pago")
            return
        }

        val importeText = edtImporte.text.toString()
        if (importeText.isEmpty()) {
            mostrarMensaje("El importe no puede estar vacío")
            return
        }

        // Mostrar confirmación con información de vencimiento
        mostrarConfirmacionCobro()
    }

    private fun mostrarConfirmacionCobro() {
        val socio = socioSeleccionado!!
        val actividad = spinnerActividad.selectedItem.toString()
        val metodoPago = spinnerMetodoPago.selectedItem.toString()
        val importe = edtImporte.text.toString()
        val tipoSocio = spinnerTipoSocio.selectedItem.toString()
        val vencimiento = obtenerFechaVencimiento(tipoSocio)

        val mensaje = """
            ¿Confirmar cobro de cuota?
            
            👤 Socio: ${socio.nombre} ${socio.apellido}
            🏷️ Tipo: $tipoSocio
            🏃 Actividad: $actividad
            💳 Método de Pago: $metodoPago
            💰 Importe: $$importe
            📅 Vencimiento: $vencimiento
            
            ¿Desea continuar?
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Confirmar Cobro")
            .setMessage(mensaje)
            .setPositiveButton("Sí, Cobrar") { dialog, which ->
                registrarPago(vencimiento)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun registrarPago(fechaVencimiento: String) {
        val socio = socioSeleccionado!!
        val actividad = spinnerActividad.selectedItem.toString()
        val metodoPago = spinnerMetodoPago.selectedItem.toString()
        val importe = edtImporte.text.toString()
        val tipoSocio = spinnerTipoSocio.selectedItem.toString()

        // Determinar la cuota basada en el tipo de socio
        val cuota = when (tipoSocio) {
            "Socio" -> "Cuota Mensual"
            "No Socio" -> "Cuota Diaria"
            else -> "Cuota"
        }

        // Navegar al comprobante con todos los datos
        val intent = Intent(this, ComprobantePagoActivity::class.java).apply {
            putExtra("fecha", fechaActual)
            putExtra("numeroSocio", socio.id.toString())
            putExtra("nombre", socio.nombre)
            putExtra("apellido", socio.apellido)
            putExtra("actividad", actividad)
            putExtra("fechaVencimiento", fechaVencimiento)
            putExtra("metodoPago", metodoPago)
            putExtra("cuota", cuota)
            putExtra("importe", "$$importe")
        }
        startActivity(intent)

        // Mostrar mensaje de éxito
        val mensajeExito = """
            ✅ Cuota cobrada exitosamente!
            
            👤 Socio: ${socio.nombre} ${socio.apellido}
            🏷️ Tipo: $tipoSocio
            🏃 Actividad: $actividad
            💳 Método: $metodoPago
            💰 Importe: $$importe
            📅 Vencimiento: $fechaVencimiento
        """.trimIndent()

        mostrarMensaje(mensajeExito)

        // Limpiar campos después del cobro exitoso
        limpiarCampos()
    }

    private fun limpiarCampos() {
        edtNumeroSocio.setText("")
        txtInfoSocio.text = "Seleccione un socio"
        spinnerTipoSocio.setSelection(0)
        spinnerActividad.setSelection(0)
        spinnerMetodoPago.setSelection(0)
        edtImporte.setText("")
        txtVencimiento.text = "Vencimiento: --/--/----"
        txtVencimiento.setTextColor(getColor(android.R.color.secondary_text_dark))
        socioSeleccionado = null
        edtNumeroSocio.requestFocus()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}