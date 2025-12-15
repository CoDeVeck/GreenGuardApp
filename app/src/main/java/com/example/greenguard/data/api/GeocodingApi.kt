package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("json")
    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "es"
    ): GeocodingResponse
}
