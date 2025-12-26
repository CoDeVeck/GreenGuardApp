package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.ContadorNotificacionesResponse
import com.example.greenguard.domain.model.dto.MensajeResponse
import com.example.greenguard.domain.model.dto.NotificacionResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotisApi {

    @GET("notificacion/usuario/{idUsuario}")
    suspend fun listarTodasNotificaciones(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<NotificacionResponseDTO>>

    // 2. GET - Listar solo NO LEÍDAS
    @GET("notificacion/usuario/{idUsuario}/no-leidas")
    suspend fun listarNoLeidas(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<NotificacionResponseDTO>>

    // 3. GET - Contador de no leídas
    @GET("notificacion/usuario/{idUsuario}/contador")
    suspend fun contarNoLeidas(
        @Path("idUsuario") idUsuario: Int
    ): Response<ContadorNotificacionesResponse>

    // 4. PUT - Marcar como leída
    @PUT("notificacion/{idNotificacion}/marcar-leida")
    suspend fun marcarComoLeida(
        @Path("idNotificacion") idNotificacion: Int
    ): Response<MensajeResponse>
}


