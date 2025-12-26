package com.example.greenguard.domain.model.dto

import com.example.greenguard.domain.model.entities.Reporte
import com.example.greenguard.domain.model.entities.TipoIncidentes

data class DashboardUsuarioDTO(
    val categoriasMasReportadas: List<ReporteStatsDTO>,
    val reportesRecientes: List<Reporte>,
    val totalReportes: Int,
    val puntosTotales: Int,
    val puntosMes: Int,
    val totalUsuariosBeneficiados: Int
)

data class ReporteStatsDTO(
    val categoria: TipoIncidentes,
    val totalReportes: Int
)

