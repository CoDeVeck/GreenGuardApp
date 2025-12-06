package com.example.greenguard.domain.model.dto

data class ClassificationResponseDTO(
    val tipoIncidente: String,          // "BACHES", "BASURA ACUMULADA", etc.
    val tipoIncidenteId: Int,           // ID de la tabla tb_tipos_incidentes
    val clasificacion: String,           // "Riesgo Bajo", "Riesgo Medio", etc.
    val clasificacionId: Int,            // ID de la tabla tb_tipo_clasificacion
    val puntosEstimados: Int,
    val descripcionIA: String,
    val detecciones: List<String>,      // Lista de objetos detectados
    val confianza: Double
)