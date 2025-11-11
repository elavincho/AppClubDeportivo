package com.elavincho.appclubdeportivo

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ListaSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerViewSocios: RecyclerView
    private lateinit var txtFecha: TextView
    private lateinit var btnInicio: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_socio)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Configurar fecha actual
        configurarFecha()

        // Cargar lista de socios
        cargarSocios()

        // Configurar botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        recyclerViewSocios = findViewById(R.id.recyclerViewSocios)
        txtFecha = findViewById(R.id.txtFecha)
        btnInicio = findViewById(R.id.btnInicio)

        // Configurar RecyclerView
        recyclerViewSocios.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarFecha() {
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        txtFecha.text = "Fecha: $fechaActual"
    }

    private fun cargarSocios() {
        val listaSocios = dbHelper.obtenerTodosLosSocios()

        if (listaSocios.isEmpty()) {
            // Mostrar mensaje si no hay socios
            txtFecha.text = "No hay socios registrados"
        } else {
            val adapter = SocioAdapter(listaSocios)
            recyclerViewSocios.adapter = adapter
        }
    }

    private fun configurarBotones() {
        btnInicio.setOnClickListener {
            finish() // Volver a la actividad anterior
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar socios cada vez que la actividad se reanuda
        cargarSocios()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}