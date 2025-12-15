package com.example.greenguard.domain.model.dto

import java.math.BigDecimal

data class DetalleReporteHistorialCliente(

    val idReporte: Int,
    val numeroReporte: String,
    val imagenRepo: String,
    val idTipoClasi: Int,
    val incidente: String,

    val estado: String,

    val latitud: BigDecimal,
    val longitud: BigDecimal,

    val descripcion: String,
    val puntosGanados: Int,

    val repoRegistado: String,
    val repoProceso: String,
    val repoResuelto: String
)