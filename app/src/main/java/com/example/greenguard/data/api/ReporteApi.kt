package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.ClassificationResponse
import com.example.greenguard.domain.model.dto.DashboardUsuarioDTO
import com.example.greenguard.domain.model.dto.DetalleReporteHistorialCliente
import com.example.greenguard.domain.model.dto.ReporteHistorialCliente
import com.example.greenguard.domain.model.dto.ReporteResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ReporteApi {
    @GET("cliente/dashboard/{idUsuario}")
    suspend fun getDashboard(@Path("idUsuario") idUsuario: Int): Response<DashboardUsuarioDTO>

    @GET("/cliente/{idUsuario}/puntos")
    suspend fun obtenerPuntos(@Path("idUsuario") idUsuario: Int): Int

    @Multipart
    @POST("api/classify")
    suspend fun clasificarImagen(
        @Part file: MultipartBody.Part
    ): Response<ClassificationResponse>

    @Multipart
    @POST("reportes/con-clasificacion")
    suspend fun crearReporteConClasificacion(
        @Part("idUsu") idUsu: RequestBody,
        @Part("detalleRepo") detalleRepo: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
        @Part("idDistrito") idDistrito: RequestBody,
        @Part("idTipoIncidente") idTipoIncidente: RequestBody,
        @Part("idClasificacion") idClasificacion: RequestBody,
        @Part("descripcionIA") descripcionIA: RequestBody,
        @Part("puntosEstimados") puntosEstimados: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ReporteResponseDTO>

    @GET("cliente/reporte")
    suspend fun historialDeReportes(
        @Query("estado") estado: String? = null
    ): List<ReporteHistorialCliente>

    @GET("cliente/detalleReporte/{idReporte}")
    suspend fun obtenerDetalleReporte(
        @Path("idReporte") idReporte: Int
    ): List<DetalleReporteHistorialCliente>
}