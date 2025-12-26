package com.example.greenguard.domain.model.dto

data class CanjeCuponResponse (
    val valor: Boolean,
    val mensaje: String,
    val codigoCupon:String,
    val fechaCanje: String,
    val qrBase64: String
)