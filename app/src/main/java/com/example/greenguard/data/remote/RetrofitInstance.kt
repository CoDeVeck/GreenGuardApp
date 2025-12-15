package com.example.greenguard.data.remote

import android.os.Build
import com.example.greenguard.data.api.GeocodingApi
import com.example.greenguard.data.dataStore.AuthInterceptor
import com.example.greenguard.data.dataStore.UserPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val LOCAL_IP = "192.168.1.10"
    private const val PORT = "8080"

    /**
     * Cliente estándar para operaciones normales (30 segundos timeout)
     */
    fun create(userPreferences: UserPreferences): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userPreferences))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    /**
     * Cliente con timeouts LARGOS para operaciones de IA (2 minutos timeout)
     * Usar este para clasificación de imágenes con YOLO + Ollama
     */
    fun createForAI(userPreferences: UserPreferences): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userPreferences))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            // ⚠️ TIMEOUTS AUMENTADOS PARA PROCESAMIENTO DE IA
            .connectTimeout(30, TimeUnit.SECONDS)     // Tiempo para establecer conexión
            .readTimeout(120, TimeUnit.SECONDS)       // 2 MINUTOS para leer respuesta (YOLO + Ollama tarda)
            .writeTimeout(60, TimeUnit.SECONDS)       // 1 minuto para enviar imagen
            .build()

        return Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getBaseUrl(): String {
        return if (isRunningOnEmulator()) {
            "http://10.0.2.2:$PORT/"
        } else {
            "http://$LOCAL_IP:$PORT/"
        }
    }
    object GeocodingClient {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"

        val api: GeocodingApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeocodingApi::class.java)
        }
    }
    private fun isRunningOnEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.lowercase().contains("vbox")
                || Build.FINGERPRINT.lowercase().contains("test-keys")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))
    }
}