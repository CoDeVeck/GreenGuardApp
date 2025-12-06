package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.LoginResponse
import com.example.greenguard.domain.model.entities.Usuario
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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
    /*@POST("auth/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("correoUsu") correo: String,
        @Field("passwordUsu") clave: String
    ): LoginResponse

    @GET("auth/me")
    suspend fun getUsuarioInfo(): UsuarioDTO

    @Multipart
    @POST("auth/register")
    suspend fun registro(
        @Part("nombreUsu") nombre: RequestBody,
        @Part("correoUsu") correo: RequestBody,
        @Part("passwordUsu") password: RequestBody,
        @Part imagenUsu: MultipartBody.Part?
    ): RegisterResponse*/
}
