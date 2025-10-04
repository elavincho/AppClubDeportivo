package com.elavincho.appclubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComprobantePagoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante_pago)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*Botón Volver a Cobrar Cuota*/

        val btnVolverCobrarCuota = findViewById<Button>(R.id.btnVolverCobrarCuota)

        btnVolverCobrarCuota.setOnClickListener {
            val intentVolverCobrarCuota = Intent(this, CobrarCuotaActivity::class.java)
            /* Por ultimo hay que llamar al método startActivity() y pasarle el intent*/
            startActivity(intentVolverCobrarCuota)
        }
    }
}