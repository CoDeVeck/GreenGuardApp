package com.example.greenguard.data.repository


import com.example.greenguard.data.api.CuponApi
import com.example.greenguard.domain.model.dto.CanjeCuponResponse
import com.example.greenguard.domain.model.dto.CuponCatalogoDTO
import com.example.greenguard.domain.model.dto.ResultadoResponse
import com.example.greenguard.domain.model.dto.UsuarioCuponDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CuponRepository(private val apiService: CuponApi) {

    suspend fun canjearCuponEnTienda(idCuponUsuario: Int): Result<ResultadoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.canjearCupon(idCuponUsuario)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: response.message()

                    Result.failure(Exception("Error al canjear cupón: $errorMessage"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun comprarCupon(idCupon: Int): Result<CanjeCuponResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.comprarCupon(idCupon)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al comprar cupón: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtener cupones del usuario
     */
    suspend fun obtenerMisCupones(): Result<List<UsuarioCuponDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerMisCupones()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener cupones: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Devolver cupón
     */
    suspend fun devolverCupon(idCuponUsuario: Int): Result<ResultadoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.devolverCupon(idCuponUsuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al devolver cupón: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtener catálogo de cupones
     */
    suspend fun obtenerCatalogo(
        activo: Boolean? = true,
        nombre: String? = null,
        categoria: Int? = null,
        puntosMin: Int? = null,
        puntosMax: Int? = null
    ): Result<List<CuponCatalogoDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerCatalogo(
                    activo, nombre, categoria, puntosMin, puntosMax
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener catálogo: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}