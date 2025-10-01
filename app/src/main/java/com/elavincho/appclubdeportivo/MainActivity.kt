package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.elavincho.appclubdeportivo.R.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*Botón Acceder*/

        /*Hay que indicar el tipo de elemento <>
        * Hay que indicar el nombre el id dentro de ()*/
        val btnAcceder = findViewById<Button>(id.btnAcceder)

        /*Hay que activar un evento para que funcione el botón*/
        btnAcceder.setOnClickListener {
            /* Intent es un objeto para solicitar una acción de otro componente de la aplicación
            * para comunicar una activity con otra.
            * Lleva 2 parametros, primero el contexto en el cual estamos trabajando this, dentro de la aplicación
            * y luego el nombre de la activity con la cual se va a comunicar y agregarle ::class.java*/
            val intent = Intent(this, PantallaPrincipalActivity::class.java)
            /* Por ultimo hay que llamar al método startActiviti() y pasarle el intent*/
            startActivity(intent)
        }


    }
}