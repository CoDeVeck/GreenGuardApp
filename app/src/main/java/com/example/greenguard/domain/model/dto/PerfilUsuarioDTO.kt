package com.example.greenguard.domain.model.dto

data class PerfilUsuarioDTO(
    val totalReportes: Int,
    val totalReportesResueltos: Int,
    val totalPuntos: Int,
    val cuponesCanjeado: Int,
    val tiempoActivo: String,
    val imagenUrl: String
)