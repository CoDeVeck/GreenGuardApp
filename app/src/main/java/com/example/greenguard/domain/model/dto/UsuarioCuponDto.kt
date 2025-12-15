package com.example.greenguard.domain.model.dto

import java.time.LocalDateTime

data class UsuarioCuponDto(
    val idUsuarioCupon: Int,
    val nombreCupon: String,
    val puntosRequeridos: Int,
    val fechaCanje: String?,
    val estado: EstadoUsuarioCupon,
    val codigoCupon: String,
    val qrBase64: String,
    val nombreTienda: String,
    val distritoTienda: String
)

enum class EstadoUsuarioCupon {
    AC, // activo
    VE, // vencido
    CA  // canjeado
}