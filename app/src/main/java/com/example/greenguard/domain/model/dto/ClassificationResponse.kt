package com.example.greenguard.domain.model.dto

import com.google.gson.annotations.SerializedName


data class ClassificationResponse(
    @SerializedName("idTipoIncidente")
    val idTipoIncidente: Int,

    @SerializedName("idClasificacion")
    val idClasificacion: Int,

    @SerializedName("tipoIncidente")
    val tipoIncidente: String,

    @SerializedName("clasificacion")
    val clasificacion: String,

    @SerializedName("nivelRiesgo")
    val nivelRiesgo: String,

    @SerializedName("puntosEstimados")
    val puntosEstimados: Int,

    @SerializedName("descIA")
    val descripcionIA: String,

    @SerializedName("confianza")
    val confianza: Float
)