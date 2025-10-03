package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CobrarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)
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
    }
}