package com.example.greenguard.repositories

import android.content.Context
import android.net.Uri
import com.example.greenguard.data.api.UserAuth
import com.example.greenguard.domain.model.dto.RegisterResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AuthRepository(
    private val userAuth: UserAuth,
    private val context: Context
) {

    suspend fun registerUser(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        documento: String,
        telefono: String,
        genero: String,
        distritoId: Int,
        correo: String,
        password: String,
        imagenUri: Uri?
    ): Result<RegisterResponse> {
        return try {
            // Preparar los datos del formulario
            val nombreBody = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            val apePatBody = apellidoPaterno.toRequestBody("text/plain".toMediaTypeOrNull())
            val apeMatBody = apellidoMaterno.toRequestBody("text/plain".toMediaTypeOrNull())
            val documentoBody = documento.toRequestBody("text/plain".toMediaTypeOrNull())
            val telefonoBody = telefono.toRequestBody("text/plain".toMediaTypeOrNull())
            val generoBody = genero.toRequestBody("text/plain".toMediaTypeOrNull())
            val distritoBody = distritoId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val correoBody = correo.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

            // Preparar la imagen si existe
            val imagenPart = imagenUri?.let { uri ->
                val file = getFileFromUri(uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "imagenUrl",
                    file.name,
                    requestFile
                )
            }

            // Llamar al API
            val response = userAuth.registerUser(
                nombreBody,
                apePatBody,
                apeMatBody,
                documentoBody,
                telefonoBody,
                generoBody,
                distritoBody,
                correoBody,
                passwordBody,
                imagenPart
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }
}
