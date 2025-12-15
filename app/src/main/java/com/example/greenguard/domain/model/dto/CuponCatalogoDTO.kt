package com.example.greenguard.domain.model.dto

import java.time.LocalDateTime

data class CuponCatalogoDTO(
    val idCupon: Int,
    val nombreCupon: String,
    val descCupon: String,
    val puntosRequeridos: Int,
    val categoria: String,
    val fechaVencimiento: String,
    val tienda: String,
    val stockDisponible: Int
)