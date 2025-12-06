package com.example.greenguard.data.dataStore

import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreferences.token.firstOrNull() }

        Log.d("AuthInterceptor", "🔑 TOKEN ACTUAL: $token")

        val request = if (!token.isNullOrEmpty()) {
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            Log.d("AuthInterceptor", "🛰️ Enviando a URL: ${newRequest.url}")
            Log.d("AuthInterceptor", "🧾 Headers: ${newRequest.headers}")
            newRequest
        } else {
            Log.e("AuthInterceptor", "⚠️ Token vacío, no se agregará Authorization Header")
            chain.request()
        }

        return chain.proceed(request)
    }
}