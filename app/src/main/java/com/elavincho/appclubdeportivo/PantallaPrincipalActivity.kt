package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class PantallaPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*Botón Volver*/

        val btnVolver = findViewById<Button>(R.id.btnSalir)

        btnVolver.setOnClickListener {
            val intentVolver = Intent(this, MainActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentVolver)
        }

        /*Botón Vencimientos*/

        val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)

        btnVencimientos.setOnClickListener {
            val intentVencimientos = Intent(this, VencimientosActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentVencimientos)
        }

        /*Botón Inicio (img_casa)*/

        val btnInicio = findViewById<ImageView>(R.id.btnInicio)

        btnInicio.setOnClickListener {
            val intentPantallaPrincipal = Intent(this, PantallaPrincipalActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentPantallaPrincipal)
        }

        /*Botón Cobrar Cuota*/

        val btnCobrarCuota = findViewById<Button>(R.id.btnCobrarCuota)

        btnCobrarCuota.setOnClickListener {
            val intentCobrarCuota = Intent(this, CobrarCuotaActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentCobrarCuota)
        }
    }
}