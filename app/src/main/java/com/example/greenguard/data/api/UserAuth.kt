package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.LoginResponse
import com.example.greenguard.domain.model.dto.PerfilUsuarioDTO
import com.example.greenguard.domain.model.dto.RegisterResponse
import com.example.greenguard.domain.model.entities.Usuario
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserAuth {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("correoUsu") correo: String,
        @Field("passwordUsu") password: String
    ): LoginResponse


    @GET("auth/me")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Usuario

    @Multipart
    @POST("auth/register")
    suspend fun registerUser(
        @Part("nomUsu") nombre: RequestBody,
        @Part("apePatUsu") apellidoPaterno: RequestBody,
        @Part("apeMatUsu") apellidoMaterno: RequestBody,
        @Part("documentoUsu") documento: RequestBody,
        @Part("telefonoUsu") telefono: RequestBody,
        @Part("generoUsu") genero: RequestBody,
        @Part("distrito.idDistrito") distritoId: RequestBody,
        @Part("correoUsu") correo: RequestBody,
        @Part("passwordUsu") password: RequestBody,
        @Part imagenUrl: MultipartBody.Part?
    ): RegisterResponse

    @GET("cliente/perfil/{idUsuario}")
    suspend fun obtenerPerfil(@Path("idUsuario") idUsuario: Int): PerfilUsuarioDTO
}
