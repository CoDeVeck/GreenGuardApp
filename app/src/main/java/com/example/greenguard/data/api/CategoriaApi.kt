package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.UsuarioCuponDto
import com.example.greenguard.domain.model.entities.Categoria
import retrofit2.Response
import retrofit2.http.GET

interface CategoriaApi {
    @GET("categoria/list")
    suspend fun listCategorias(): Response<List<Categoria>>
}