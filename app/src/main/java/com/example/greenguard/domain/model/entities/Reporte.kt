package com.example.greenguard.domain.model.entities

data class Reporte(
    val idReporte: Int? = null,
    val numReport: String? = null,
    val usuario: Usuario? = null,
    val detalleRepo: String? = null,
    val imagenRepo: String? = null,
    val estado: String? = null, // Enum como String
    val latitud: Double? = null,
    val longitud: Double? = null,
    val tipoIncidente: TipoIncidentes? = null,
    val tipoClasificacion: TipoClasificacion? = null,
    val distrito: Distrito? = null,
    val repoRegistado: String? = null,
    val repoResuelto: String? = null
)
