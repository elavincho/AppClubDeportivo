package com.elavincho.appclubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AptoFisicoActivity : AppCompatActivity() {

   private lateinit var dbHelper: DBHelper
   private var socioId: Int = -1

   // Declarar vistas
   private lateinit var edTxtFechaVencimiento: EditText
   private lateinit var edTxtMedicoNombre: EditText
   private lateinit var edTxtMedicoApellido: EditText
   private lateinit var edTxtMedicoMatricula: EditText
   private lateinit var btnEsApto: Button
   private lateinit var btnClip: ImageView
   private lateinit var btnOk: ImageView
   private lateinit var btnCerrar: ImageView
   private lateinit var btnInicio: ImageView

   // Variable para controlar el estado del apto físico
   private var esApto: Boolean = true

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContentView(R.layout.activity_apto_fisico)

      // INICIALIZAR DBHelper PRIMERO
      dbHelper = DBHelper(this)

      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }

      // Obtener el ID del socio desde el intent
      socioId = intent.getIntExtra("socio_id", -1)

      // DEBUG: Verificar el ID recibido
      android.util.Log.d("APTO_DEBUG", "ID Socio recibido: $socioId")

      if (socioId == -1) {
         Toast.makeText(this, "Error: No se encontró el socio. Guarde el socio primero.", Toast.LENGTH_LONG).show()
         finish()
         return
      }

      // Inicializar vistas
      inicializarVistas()

      // Configurar botones
      configurarBotones()

      // Cargar datos existentes si hay
      cargarDatosExistentes()
   }

   private fun inicializarVistas() {
      edTxtFechaVencimiento = findViewById(R.id.edTxtFechaVencimiento)
      edTxtMedicoNombre = findViewById(R.id.edTxtMedicoNombre)
      edTxtMedicoApellido = findViewById(R.id.edTxtMedicoApellido)
      edTxtMedicoMatricula = findViewById(R.id.edTxtMedicoMatricula)
      btnEsApto = findViewById(R.id.btnEsApto)
      btnClip = findViewById(R.id.btnClip)
      btnOk = findViewById(R.id.btnOk)
      btnCerrar = findViewById(R.id.btnCerrar)
      btnInicio = findViewById(R.id.btnInicio)

      // Configurar estado inicial del botón
      actualizarBotonApto()
   }

   private fun configurarBotones() {
      // Botón Es Apto - Toggle del estado
      btnEsApto.setOnClickListener {
         esApto = !esApto
         actualizarBotonApto()
      }

      // Botón OK - Guardar apto físico
      btnOk.setOnClickListener {
         guardarAptoFisico()
      }

      // Botón Cerrar - Limpiar campos
      btnCerrar.setOnClickListener {
         limpiarCampos()
      }

      // Botón Inicio - Confirmar antes de salir
      btnInicio.setOnClickListener {
         val builder = AlertDialog.Builder(this)
         builder.setTitle("Confirmar Salida")
         builder.setMessage("¿Está seguro de que desea salir del apto físico?")

         builder.setPositiveButton("Sí, Salir") { dialog, which ->
            val intent = Intent(this, PantallaPrincipalActivity::class.java)
            startActivity(intent)
            finish()
         }

         builder.setNegativeButton("No, Quedarse") { dialog, which ->
            // No hacer nada, permanecer en la actividad
         }

         val dialog = builder.create()
         dialog.show()
      }

      // Botón Clip
      btnClip.setOnClickListener {
         Toast.makeText(this, "Funcionalidad de adjuntar archivo próximamente", Toast.LENGTH_SHORT).show()
      }
   }

   private fun actualizarBotonApto() {
      if (esApto) {
         btnEsApto.text = "ES APTO"
         btnEsApto.setBackgroundColor(Color.parseColor("#4CAF50")) // Verde
         btnEsApto.setTextColor(Color.WHITE)
      } else {
         btnEsApto.text = "NO APTO"
         btnEsApto.setBackgroundColor(Color.parseColor("#F44336")) // Rojo
         btnEsApto.setTextColor(Color.WHITE)
      }
   }

   private fun cargarDatosExistentes() {
      try {
         val ultimoApto = dbHelper.obtenerUltimoAptoFisico(socioId)
         ultimoApto?.let { apto ->
            edTxtFechaVencimiento.setText(apto.fechaVencimiento)
            edTxtMedicoNombre.setText(apto.medicoNombre)
            edTxtMedicoApellido.setText(apto.medicoApellido)
            edTxtMedicoMatricula.setText(apto.medicoMatricula)

            esApto = apto.esApto
            actualizarBotonApto()

            Toast.makeText(this, "Cargado apto físico existente", Toast.LENGTH_SHORT).show()
         }
      } catch (e: Exception) {
         android.util.Log.e("APTO_DEBUG", "Error cargando datos: ${e.message}")
      }
   }

   private fun guardarAptoFisico() {
      val fechaVencimiento = edTxtFechaVencimiento.text.toString().trim()
      val medicoNombre = edTxtMedicoNombre.text.toString().trim()
      val medicoApellido = edTxtMedicoApellido.text.toString().trim()
      val medicoMatricula = edTxtMedicoMatricula.text.toString().trim()

      // Validar campos
      if (fechaVencimiento.isEmpty()) {
         mostrarMensaje("La fecha de vencimiento es obligatoria")
         edTxtFechaVencimiento.requestFocus()
         return
      }

      if (medicoNombre.isEmpty()) {
         mostrarMensaje("El nombre del médico es obligatorio")
         edTxtMedicoNombre.requestFocus()
         return
      }

      if (medicoApellido.isEmpty()) {
         mostrarMensaje("El apellido del médico es obligatorio")
         edTxtMedicoApellido.requestFocus()
         return
      }

      if (medicoMatricula.isEmpty()) {
         mostrarMensaje("La matrícula del médico es obligatoria")
         edTxtMedicoMatricula.requestFocus()
         return
      }

      try {
         val resultado = dbHelper.agregarAptoFisico(
            socioId = socioId,
            fechaVencimiento = fechaVencimiento,
            medicoNombre = medicoNombre,
            medicoApellido = medicoApellido,
            medicoMatricula = medicoMatricula,
            esApto = esApto
         )

         if (resultado != -1L) {
            mostrarMensaje("Apto físico guardado correctamente")

            // Esperar un poco para que se vea el mensaje y luego ir a la lista
            Handler(Looper.getMainLooper()).postDelayed({
               val intent = Intent(this, ListaSocioActivity::class.java)
               startActivity(intent)
               finish() // Cierra esta actividad
            }, 1000) // 1 segundo de delay
         } else {
            mostrarMensaje("Error al guardar el apto físico")
         }
      } catch (e: Exception) {
         mostrarMensaje("Error: ${e.message}")
         android.util.Log.e("APTO_DEBUG", "Error guardando apto: ${e.message}")
      }
   }

   private fun limpiarCampos() {
      edTxtFechaVencimiento.setText("")
      edTxtMedicoNombre.setText("")
      edTxtMedicoApellido.setText("")
      edTxtMedicoMatricula.setText("")

      // Resetear estado a "Es Apto"
      esApto = true
      actualizarBotonApto()

      // Enfocar el primer campo
      edTxtFechaVencimiento.requestFocus()
   }

   private fun mostrarMensaje(mensaje: String) {
      Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
   }

   override fun onDestroy() {
      // Solo cerrar dbHelper si está inicializado
      if (::dbHelper.isInitialized) {
         dbHelper.close()
      }
      super.onDestroy()
   }
}