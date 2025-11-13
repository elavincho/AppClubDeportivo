package com.elavincho.appclubdeportivo

import java.util.Date

data class ComprobantePago(
    val id: Long = 0,
    val fecha: String,
    val numeroSocio: String,
    val nombre: String,
    val apellido: String,
    val actividad: String,
    val fechaVencimiento: String,
    val metodoPago: String,
    val cuota: String,
    val importe: String,
    val fechaCreacion: Date = Date()
)