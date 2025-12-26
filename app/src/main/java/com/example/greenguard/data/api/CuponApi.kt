package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.CanjeCuponResponse
import com.example.greenguard.domain.model.dto.CuponCatalogoDTO
import com.example.greenguard.domain.model.dto.ResultadoResponse
import com.example.greenguard.domain.model.dto.UsuarioCuponDto
import retrofit2.Response
import retrofit2.http.*

interface CuponApi {

    @PUT("cliente/canjear/{id}")
    suspend fun canjearCupon(@Path("id") id: Int) : Response<ResultadoResponse>

    @POST("cliente/comprarCupon/{idCupon}")
    suspend fun comprarCupon(
        @Path("idCupon") idCupon: Int
    ): Response<CanjeCuponResponse>

    @GET("cliente/misCupones")
    suspend fun obtenerMisCupones(): Response<List<UsuarioCuponDto>>

    @POST("cliente/misCupones/{idCuponUsuario}/devolver")
    suspend fun devolverCupon(
        @Path("idCuponUsuario") idCuponUsuario: Int
    ): Response<ResultadoResponse>

    @GET("cliente/catalogo")
    suspend fun obtenerCatalogo(
        @Query("activo") activo: Boolean? = null,
        @Query("nombre") nombre: String? = null,
        @Query("categoria") categoria: Int? = null,
        @Query("puntosMin") puntosMin: Int? = null,
        @Query("puntosMax") puntosMax: Int? = null
    ): Response<List<CuponCatalogoDTO>>
}