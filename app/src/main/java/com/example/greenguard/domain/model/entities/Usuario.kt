package com.example.greenguard.domain.model.entities

data class Usuario(
    val idUsu: Int? = null,
    val nomUsu: String? = null,
    val apePatUsu: String? = null,
    val apeMatUsu: String? = null,
    val documentoUsu: String? = null,
    val correoUsu: String? = null,
    val passwordUsu: String? = null,
    val telefonoUsu: String? = null,
    val generoUsu: String? = null,
    val imagenUSU: String? = null,
    val registroUsu: String? = null,
    val puntosUsu: Int = 0,
    val rol: Rol? = null,
    val activo: Boolean? = null,
    val fmcToken: String? = null,
    val fmcTokenFecha: String? = null
)
