package com.elavincho.appclubdeportivo

data class AptoFisico(
    val id: Int = 0,
    val socioId: Int,
    val fechaVencimiento: String,
    val medicoNombre: String,
    val medicoApellido: String,
    val medicoMatricula: String,
    val esApto: Boolean,
    val fechaCreacion: String
)