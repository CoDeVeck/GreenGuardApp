package com.example.greenguard.domain.model.dto

data class NotificacionResponseDTO(
    val idNotificacion: Int,
    val titulo: String,
    val mensaje: String,
    val tipoNotificacion: String, // Ej: "REPORTE_REGISTRADO"
    val leida: Boolean,
    val fechaCreacion: String, // LocalDateTime se serializa como String en JSON
    // Datos opcionales según el tipo
    val reporte: ReporteInfoDTO? = null,
    val cupon: CuponInfoDTO? = null
)

// Clase interna para info resumida de reporte
data class ReporteInfoDTO(
    val idReporte: Int,
    val numReport: String,
    val tipoIncidente: String
)

// Clase interna para info resumida de cupón
data class CuponInfoDTO(
    val idUsuarioCupon: Int,
    val codigoCupon: String,
    val nombreCupon: String
)

// Respuesta para el contador
data class ContadorNotificacionesResponse(
    val noLeidas: Long
)

// Respuesta para marcar como leída
data class MensajeResponse(
    val mensaje: String? = null,
    val error: String? = null
)