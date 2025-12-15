package com.example.greenguard.domain.model.dto

data class ReporteHistorialCliente(
    val idReporte: Int,
    val imagenRepo: String,
    val incidente: String,
    val idTipoClasi: Int,
    val puntosGanados: Int,
    val estado: String,
    val repoRegistado: String,
    val repoProceso: String,
    val repoResuelto: String,
)