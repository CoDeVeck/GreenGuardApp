package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.ClassificationResponse
import com.example.greenguard.domain.model.dto.ReporteResponseDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ReporteApi {
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
        @Part("idTipoIncidente") idTipoIncidente: RequestBody,        // ✅ NUEVO
        @Part("idClasificacion") idClasificacion: RequestBody,        // ✅ NUEVO
        @Part("descripcionIA") descripcionIA: RequestBody,            // ✅ NUEVO
        @Part("puntosEstimados") puntosEstimados: RequestBody,        // ✅ NUEVO
        @Part imagen: MultipartBody.Part
    ): Response<ReporteResponseDTO>
}