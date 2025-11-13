package com.elavincho.appclubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VistaCarnetActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    // Declarar vistas
    private lateinit var tvSocioNumero: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var tvTipoSocio: TextView
    private lateinit var tvCondicion: TextView
    private lateinit var tvActividad: TextView
    private lateinit var tvFechaVencimiento: TextView
    private lateinit var btnDescargar: ImageView
    private lateinit var btnCompartir: ImageView
    private lateinit var btnVolverVerCarnet: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vista_carnet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Cargar datos del socio
        cargarDatosSocio()

        // Configurar botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        tvSocioNumero = findViewById(R.id.tvSocioNumero)
        tvNombre = findViewById(R.id.tvNombre)
        tvApellido = findViewById(R.id.tvApellido)
        tvTipoSocio = findViewById(R.id.tvTipoSocio)
        tvCondicion = findViewById(R.id.tvCondicion)
        tvActividad = findViewById(R.id.tvActividad)
        tvFechaVencimiento = findViewById(R.id.tvFechaVencimiento)
        btnDescargar = findViewById(R.id.btnDescargar)
        btnCompartir = findViewById(R.id.btnCompartir)
        btnVolverVerCarnet = findViewById(R.id.btnVolverVerCarnet)
    }

    private fun cargarDatosSocio() {
        // Obtener datos del intent
        val socioId = intent.getIntExtra("socio_id", -1)
        val socioNombre = intent.getStringExtra("socio_nombre") ?: "No especificado"
        val socioApellido = intent.getStringExtra("socio_apellido") ?: "No especificado"
        val socioTipo = intent.getStringExtra("socio_tipo") ?: "No especificado"
        val socioNumero = intent.getStringExtra("socio_numero") ?: "No especificado"
        val socioActividad = intent.getStringExtra("socio_actividad") ?: "Sin actividad"

        if (socioId == -1) {
            Toast.makeText(this, "Error: No se recibieron datos del socio", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Obtener apto físico del socio
        val aptoFisico = dbHelper.obtenerUltimoAptoFisico(socioId)

        // Mostrar datos en los TextViews
        tvSocioNumero.text = "Socio N° $socioNumero"
        tvNombre.text = socioNombre
        tvApellido.text = socioApellido
        tvTipoSocio.text = socioTipo
        tvActividad.text = "Actividad: $socioActividad"

        // Configurar condición y tipo de socio
        when (socioTipo) {
            "Socio" -> {
                tvCondicion.text = "ACTIVO"
                //tvCondicion.setTextColor(Color.GREEN)
                //tvTipoSocio.setTextColor(Color.BLUE)
            }
            "No Socio" -> {
                tvCondicion.text = "VISITANTE"
                //tvCondicion.setTextColor(Color.GRAY)
                //tvTipoSocio.setTextColor(Color.DKGRAY)
            }
            else -> {
                tvCondicion.text = socioTipo
                tvCondicion.setTextColor(Color.BLACK)
                tvTipoSocio.setTextColor(Color.BLACK)
            }
        }

        // Mostrar información del apto físico
        if (aptoFisico != null) {
            tvFechaVencimiento.text = "Apto vence: ${aptoFisico.fechaVencimiento}"
            if (aptoFisico.esApto) {
                //tvFechaVencimiento.setTextColor(Color.GREEN)
            } else {
                //tvFechaVencimiento.setTextColor(Color.RED)
                tvFechaVencimiento.text = "NO APTO - Vence: ${aptoFisico.fechaVencimiento}"
            }
        } else {
            tvFechaVencimiento.text = "Sin apto físico registrado"
            //tvFechaVencimiento.setTextColor(Color.RED)
        }
    }

    private fun configurarBotones() {
        /* Botón Descargar */
        btnDescargar.setOnClickListener {
            Toast.makeText(this, "Descargando carnet...", Toast.LENGTH_SHORT).show()
            // Aquí puedes implementar la funcionalidad de descarga
        }

        /* Botón Compartir */
        btnCompartir.setOnClickListener {
            compartirCarnet()
        }

        /* Botón Volver a Ver Carnet */
        btnVolverVerCarnet.setOnClickListener {
            val intentVolverVerCarnet = Intent(this, CarnetActivity::class.java)
            startActivity(intentVolverVerCarnet)
            finish()
        }
    }

    private fun compartirCarnet() {
        val socioNombre = intent.getStringExtra("socio_nombre") ?: ""
        val socioApellido = intent.getStringExtra("socio_apellido") ?: ""
        val socioNumero = intent.getStringExtra("socio_numero") ?: ""
        val socioTipo = intent.getStringExtra("socio_tipo") ?: ""
        val socioActividad = intent.getStringExtra("socio_actividad") ?: ""

        val textoCarnet = """
            🎫 CARNET DEL CLUB DEPORTIVO
            
            👤 Socio: $socioNombre $socioApellido
            🆔 Número: $socioNumero
            🏷️ Tipo: $socioTipo
            🏃 Actividad: $socioActividad
            📅 Fecha Emisión: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())}
            
            Este carnet identifica al portador como miembro del club.
        """.trimIndent()

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoCarnet)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Compartir carnet"))
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}