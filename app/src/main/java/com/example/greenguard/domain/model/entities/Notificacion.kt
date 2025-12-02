package com.example.greenguard.domain.model.entities

data class Notificacion(
    val idNotificacion: Int? = null,
    val usuario: Usuario? = null,
    val tipoNotificacion: TipoNotificacion? = null,
    val titulo: String? = null,
    val mensaje: String? = null,
    val reporte: Reporte? = null,
    val usuarioCupon: UsuarioCupon? = null,
    val cupon: Cupon? = null,
    val leida: Boolean? = null,
    val descartada: Boolean? = null
)
