package com.example.greenguard.data.dataStore

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.greenguard.LoginActivity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {

    private val context: Context = userPreferences.getContext()

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreferences.obtenerToken() }

        Log.d("AuthInterceptor", "🔑 TOKEN ACTUAL: $token")

        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
                Log.d("AuthInterceptor", "🛰️ Enviando a URL: ${chain.request().url}")
                Log.d("AuthInterceptor", "🧾 Headers: Authorization: ██")
            }
        }.build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.e("AuthInterceptor", "❌ Token expirado o inválido (401)")
            response.close()

            runBlocking {
                userPreferences.limpiarDatos()
            }

            redirectToLogin()

            return response.newBuilder()
                .code(401)
                .message("Token expired")
                .body(null)
                .build()
        }

        return response
    }

    private fun redirectToLogin() {
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("token_expired", true)
        }
        context.startActivity(intent)
    }
}