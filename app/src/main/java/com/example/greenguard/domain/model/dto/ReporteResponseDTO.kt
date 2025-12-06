package com.example.greenguard.domain.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReporteResponseDTO(
    val idReporte: Int,
    val numReport: String,
    val imagenUrl: String,
    val estado: String,
    val puntosGanados: Int,
    val tipoIncidente: String,           // "BACHES"
    val tipoIncidenteId: Int,
    val nivelRiesgo: String,             // "Riesgo Medio"
    val nivelRiesgoId: Int,
    val descripcionIA: String,           // Descripción generada por IA
    val deteccionesIA: List<String>,     // ["asfalto deteriorado", "hueco profundo"]
    val confianzaIA: Double,             // 0.95
    val repoRegistrado: String
) : Parcelable