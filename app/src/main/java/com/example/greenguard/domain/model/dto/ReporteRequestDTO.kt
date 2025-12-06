package com.example.greenguard.domain.model.dto

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ReporteRequestDTO(
    val idUsu: RequestBody,
    val detalleRepo: RequestBody,
    val imagen: MultipartBody.Part,
    val latitud: RequestBody,
    val longitud: RequestBody,
    val idDistrito: RequestBody
)