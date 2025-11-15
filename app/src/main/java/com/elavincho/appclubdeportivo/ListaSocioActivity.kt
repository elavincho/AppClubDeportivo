package com.elavincho.appclubdeportivo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerViewSocios: RecyclerView
    private lateinit var txtFecha: TextView
    //private lateinit var btnInicio: ImageView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_socio)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Inicializar vistas
        inicializarVistas()

        // Cargar lista de socios
        cargarSocios()

        // Configurar botones
        configurarBotones()
    }

    private fun inicializarVistas() {
        recyclerViewSocios = findViewById(R.id.recyclerViewSocios)
        btnBack = findViewById(R.id.btnBack)

        // Configurar RecyclerView
        recyclerViewSocios.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarSocios() {
        val listaSocios = dbHelper.obtenerTodosLosSocios()

        if (listaSocios.isEmpty()) {
            // Mostrar mensaje si no hay socios
            Toast.makeText(this, "No hay socios registrados", Toast.LENGTH_SHORT).show()
        } else {
            // Usar el adaptador con el listener del menú
            val adapter = SocioAdapter(listaSocios) { socio, position ->
                mostrarMenuSocio(socio, position)
            }
            recyclerViewSocios.adapter = adapter
        }
    }

    private fun mostrarMenuSocio(socio: Socio, position: Int) {
        val viewHolder = recyclerViewSocios.findViewHolderForAdapterPosition(position)
        val anchorView = viewHolder?.itemView?.findViewById<View>(R.id.btnMenuSocio)

        if (anchorView != null) {
            val popup = PopupMenu(this, anchorView)
            popup.menuInflater.inflate(R.menu.menu_socio, popup.menu)

            // FORZAR que muestre los íconos (esto es clave)
            try {
                val field = popup::class.java.getDeclaredField("mPopup")
                field.isAccessible = true
                val menuPopupHelper = field.get(popup)
                menuPopupHelper::class.java
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menuPopupHelper, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_ver_datos -> {
                        verDatosSocio(socio)
                        true
                    }
                    R.id.menu_editar -> {
                        editarSocio(socio)
                        true
                    }
                    R.id.menu_ver_comprobantes -> {
                        verComprobantesSocio(socio)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        } else {
            Toast.makeText(this, "Error al mostrar el menú", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verDatosSocio(socio: Socio) {
        val intent = Intent(this, DetalleSocioActivity::class.java).apply {
            putExtra("socio_id", socio.id)
            // También puedes pasar otros datos si los necesitas
            putExtra("socio_nombre", socio.nombre)
            putExtra("socio_apellido", socio.apellido)
        }
        startActivity(intent)
    }

    private fun editarSocio(socio: Socio) {
        val intent = Intent(this, EditarSocioActivity::class.java).apply {
            putExtra("socio_id", socio.id)
        }
        // Usamos startActivityForResult para recibir el resultado
        startActivityForResult(intent, REQUEST_EDITAR_SOCIO)
    }

    // Constante para identificar la solicitud
    companion object {
        private const val REQUEST_EDITAR_SOCIO = 1001
    }

    // Manejar el resultado cuando se edita un socio
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_EDITAR_SOCIO) {
            if (resultCode == RESULT_OK) {
                // Recargar la lista de socios
                cargarSocios()
                Toast.makeText(this, "Socio actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verComprobantesSocio(socio: Socio) {
        val intent = Intent(this, ComprobantesSocioActivity::class.java).apply {
            putExtra("socio_id", socio.id)
            putExtra("socio_nombre", socio.nombre)
            putExtra("socio_apellido", socio.apellido)
        }
        startActivity(intent)
    }

    private fun configurarBotones() {

        btnBack.setOnClickListener {
            finish()
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