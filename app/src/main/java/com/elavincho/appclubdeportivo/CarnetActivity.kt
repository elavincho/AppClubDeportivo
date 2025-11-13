package com.elavincho.appclubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class CarnetActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var edtNumeroSocio: TextInputEditText
    private lateinit var btnBuscarSocio: Button
    private lateinit var btnGenerarCarnet: Button
    private lateinit var btnInicio: ImageView
    private lateinit var layoutInfoSocio: LinearLayout

    // TextViews para mostrar datos
    private lateinit var tvNumeroSocio: TextView
    private lateinit var tvCondicion: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var tvActividad: TextView
    private lateinit var tvFechaVencimiento: TextView

    private var socioSeleccionado: Socio? = null
    private var aptoFisico: AptoFisico? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar listeners
        configurarListeners()
    }

    private fun inicializarVistas() {
        edtNumeroSocio = findViewById(R.id.edtNumeroSocio)
        btnBuscarSocio = findViewById(R.id.btnBuscarSocio)
        btnGenerarCarnet = findViewById(R.id.btnGenerarCarnet)
        btnInicio = findViewById(R.id.btnInicio)
        layoutInfoSocio = findViewById(R.id.layoutInfoSocio)

        // TextViews de información
        tvNumeroSocio = findViewById(R.id.tvNumeroSocio)
        tvCondicion = findViewById(R.id.tvCondicion)
        tvNombre = findViewById(R.id.tvNombre)
        tvApellido = findViewById(R.id.tvApellido)
        tvActividad = findViewById(R.id.tvActividad)
        tvFechaVencimiento = findViewById(R.id.tvFechaVencimiento)
    }

    private fun configurarListeners() {
        /* Botón Buscar Socio */
        btnBuscarSocio.setOnClickListener {
            buscarSocio()
        }

        /* Botón Generar Carnet */
        btnGenerarCarnet.setOnClickListener {
            generarCarnet()
        }

        /* Botón Inicio */
        btnInicio.setOnClickListener {
            val intentPantallaPrincipal = Intent(this, PantallaPrincipalActivity::class.java)
            startActivity(intentPantallaPrincipal)
            finish()
        }
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
                // Obtener apto físico del socio
                aptoFisico = dbHelper.obtenerUltimoAptoFisico(socioId)

                // Mostrar información del socio
                mostrarInformacionSocio()

                // Mostrar sección de información y botón generar carnet
                layoutInfoSocio.visibility = LinearLayout.VISIBLE
                btnGenerarCarnet.visibility = Button.VISIBLE

                mostrarMensaje("Socio encontrado: ${socioSeleccionado!!.nombre} ${socioSeleccionado!!.apellido}")
            } else {
                ocultarInformacionSocio()
                mostrarMensaje("No se encontró el socio con ID: $socioId")
            }
        } catch (e: NumberFormatException) {
            mostrarMensaje("Ingrese un número válido")
        }
    }

    private fun mostrarInformacionSocio() {
        val socio = socioSeleccionado!!

        tvNumeroSocio.text = socio.id.toString()
        tvCondicion.text = socio.tipoSocio
        tvNombre.text = socio.nombre
        tvApellido.text = socio.apellido

        // Obtener la última actividad del socio desde los comprobantes
        val ultimaActividad = dbHelper.obtenerUltimaActividadSocio(socio.id)
        tvActividad.text = if (ultimaActividad != "Sin actividad registrada") {
            ultimaActividad
        } else {
            "Sin actividad reciente"
        }

        // Mostrar información del apto físico
        if (aptoFisico != null) {
            tvFechaVencimiento.text = aptoFisico!!.fechaVencimiento

            // Cambiar color según estado del apto físico
            if (aptoFisico!!.esApto) {
                tvFechaVencimiento.setTextColor(Color.GREEN)
            } else {
                tvFechaVencimiento.setTextColor(Color.RED)
            }
        } else {
            tvFechaVencimiento.text = "Sin apto físico"
            tvFechaVencimiento.setTextColor(Color.RED)
        }

        // Personalizar colores según tipo de socio
        when (socio.tipoSocio) {
            "Socio" -> {
                tvCondicion.setTextColor(Color.BLUE)
                tvCondicion.text = "SOCIO ACTIVO"
            }
            "No Socio" -> {
                tvCondicion.setTextColor(Color.GRAY)
                tvCondicion.text = "NO SOCIO"
            }
            else -> {
                tvCondicion.setTextColor(Color.BLACK)
            }
        }
    }

    private fun ocultarInformacionSocio() {
        layoutInfoSocio.visibility = LinearLayout.GONE
        btnGenerarCarnet.visibility = Button.GONE
        socioSeleccionado = null
        aptoFisico = null
    }

    private fun generarCarnet() {
        if (socioSeleccionado == null) {
            mostrarMensaje("Primero busque un socio")
            return
        }

        // Obtener la actividad actual
        val actividad = dbHelper.obtenerUltimaActividadSocio(socioSeleccionado!!.id)

        val intent = Intent(this, VistaCarnetActivity::class.java).apply {
            putExtra("socio_id", socioSeleccionado!!.id)
            putExtra("socio_nombre", socioSeleccionado!!.nombre)
            putExtra("socio_apellido", socioSeleccionado!!.apellido)
            putExtra("socio_tipo", socioSeleccionado!!.tipoSocio)
            putExtra("socio_numero", socioSeleccionado!!.id.toString())
            putExtra("socio_actividad", actividad)

            // Pasar información del apto físico si existe
            if (aptoFisico != null) {
                putExtra("apto_fecha_vencimiento", aptoFisico!!.fechaVencimiento)
                putExtra("apto_es_apto", aptoFisico!!.esApto)
            }
        }
        startActivity(intent)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}