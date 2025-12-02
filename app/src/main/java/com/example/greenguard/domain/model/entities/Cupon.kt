package com.example.greenguard.domain.model.entities

data class Cupon(
    val idCupon: Int? = null,
    val nombreCupon: String? = null,
    val descCupon: String? = null,
    val categoria: Categoria? = null,
    val codCupon: String? = null,
    val puntosRequeridos: Int = 0,
    val tienda: Tienda? = null,
    val fechaCreacion: String? = null,
    val activo: Boolean? = null
)
