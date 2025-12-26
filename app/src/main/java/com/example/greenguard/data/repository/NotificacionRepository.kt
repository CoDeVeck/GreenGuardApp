package com.example.greenguard.data.repository

import com.example.greenguard.data.api.NotisApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.domain.model.dto.NotificacionResponseDTO


class NotificacionRepository(
    private val notisApi: NotisApi,
    private val userPreferences: UserPreferences
) {

    suspend fun listarTodasNotificaciones(): Result<List<NotificacionResponseDTO>> {
        return try {
            val idUsuario = userPreferences.obtenerIdUsuario()
            if (idUsuario == -1) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val response = notisApi.listarTodasNotificaciones(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener notificaciones: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarNoLeidas(): Result<List<NotificacionResponseDTO>> {
        return try {
            val idUsuario = userPreferences.obtenerIdUsuario()
            if (idUsuario == -1) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val response = notisApi.listarNoLeidas(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener notificaciones no leídas: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun contarNoLeidas(): Result<Long> {
        return try {
            val idUsuario = userPreferences.obtenerIdUsuario()
            if (idUsuario == -1) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val response = notisApi.contarNoLeidas(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.noLeidas)
            } else {
                Result.failure(Exception("Error al contar notificaciones: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarComoLeida(idNotificacion: Int): Result<String> {
        return try {
            val response = notisApi.marcarComoLeida(idNotificacion)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.mensaje ?: "Notificación marcada como leída")
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Error al marcar notificación: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarTodasComoLeidas(): Result<String> {
        return try {
            val notificaciones = listarNoLeidas().getOrNull() ?: emptyList()

            if (notificaciones.isEmpty()) {
                return Result.success("No hay notificaciones por marcar")
            }

            var exitosas = 0
            var fallidas = 0

            notificaciones.forEach { notif ->
                marcarComoLeida(notif.idNotificacion)
                    .onSuccess { exitosas++ }
                    .onFailure { fallidas++ }
            }

            when {
                fallidas == 0 -> Result.success("Todas las notificaciones marcadas como leídas")
                exitosas == 0 -> Result.failure(Exception("No se pudo marcar ninguna notificación"))
                else -> Result.success("Se marcaron $exitosas de ${notificaciones.size} notificaciones")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}