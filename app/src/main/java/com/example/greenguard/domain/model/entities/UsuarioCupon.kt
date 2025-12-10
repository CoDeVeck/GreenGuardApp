package com.example.greenguard.domain.model.entities

data class UsuarioCupon(
    val idUsuarioCupon: Int? = null,
    val cupon: Cupon? = null,
    val usuario: Usuario? = null,
    val codigoCupon: String? = null,
    val puntosUsuario: Int? = null,
    val qrVerificationCode: String? = null,
    val fechaCanje: String? = null,
    val canjeado: Boolean? = null,
    val fechaUso: String? = null,
    val estado: String? = null // Enum como String
)
