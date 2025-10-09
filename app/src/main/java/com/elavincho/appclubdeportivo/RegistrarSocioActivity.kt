package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import org.w3c.dom.Text

class RegistrarSocioActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()

      val edTxtNombre = findViewById<TextView>(R.id.edTxtNombre)
      val edTxtApellido = findViewById<TextView>(R.id.edTxtApellido)
      val edTxtNroDoc = findViewById<TextView>(R.id.edTxtNroDoc)
      val edTxtFechaNac = findViewById<TextView>(R.id.edTxtFechaNac)
      val edTxtTelefono = findViewById<TextView>(R.id.edTxtTelefono)
      val edTxtMail = findViewById<TextView>(R.id.edTxtMail)
      val edTxtDireccion = findViewById<TextView>(R.id.edTxtDireccion)

      setContentView(R.layout.activity_registrar_socio)
      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }

      /*Botón Inicio (img_casa)*/

      val btnInicio = findViewById<ImageView>(R.id.btnInicio)

      btnInicio.setOnClickListener {
         val intentPantallaPrincipal = Intent(this, PantallaPrincipalActivity::class.java)
         /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
         startActivity(intentPantallaPrincipal)
      }

      /*Botón Apto Físico*/

      val btnAptoFisico= findViewById<Button>(R.id.btnAptoFisico)

      btnAptoFisico.setOnClickListener {
         val intentAptoFisico= Intent(this, AptoFisicoActivity::class.java)
         /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
         startActivity(intentAptoFisico)

        /* Toast.makeText("contexto", "complete los campos",5)*/
      }


   }
}