package com.example.greenguard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenguard.R
import com.example.greenguard.databinding.ItemNotificationBinding
import com.example.greenguard.domain.model.dto.NotificacionResponseDTO

class NotificationsAdapter(
    private val notifications: List<NotificacionResponseDTO>,
    private val onItemClick: (NotificacionResponseDTO) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.binding.apply {
            tvTitle.text = notification.titulo

            // Construir descripción con info adicional
            val descripcion = buildString {
                append(notification.mensaje)

                notification.reporte?.let { reporte ->
                    append("\n📋 Reporte: ${reporte.numReport}")
                    append("\n🔖 ${reporte.tipoIncidente}")
                }

                notification.cupon?.let { cupon ->
                    append("\n🎟️ ${cupon.nombreCupon}")
                    append("\n🔑 ${cupon.codigoCupon}")
                }
            }
            tvDescription.text = descripcion

            tvTime.text = calcularTiempoRelativo(notification.fechaCreacion)

            // Mostrar/ocultar punto de no leída
            unreadDot.visibility = if (!notification.leida) View.VISIBLE else View.GONE

            // Cambiar ícono según el tipo de notificación
            ivIcon.setImageResource(getIconForType(notification.tipoNotificacion))

            // Cambiar opacidad si está leída
            root.alpha = if (!notification.leida) 1.0f else 0.6f
        }

        holder.itemView.setOnClickListener {
            onItemClick(notification)
        }
    }

    override fun getItemCount() = notifications.size

    private fun getIconForType(tipo: String): Int {
        return when (tipo) {
            "REPORTE_REGISTRADO" -> R.drawable.ic_report_add
            "CAMBIO_ESTADO_REPORTE" -> R.drawable.ic_report_status
            "PUNTOS_GANADOS" -> R.drawable.ic_points_star
            "CUPON_CANJEADO" -> R.drawable.ic_coupon_success
            "CUPON_POR_VENCER" -> R.drawable.ic_coupon_warning
            "CUPON_VENCIDO" -> R.drawable.ic_coupon_expired
            "CUPON_USADO" -> R.drawable.ic_coupon_used
            else -> R.drawable.ic_notifications_bell
        }
    }

    private fun calcularTiempoRelativo(fechaStr: String): String {
        return try {
            val formato = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val fecha = formato.parse(fechaStr) ?: return fechaStr
            val now = java.util.Date()

            val diffMillis = now.time - fecha.time
            val minutos = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(diffMillis)
            val horas = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diffMillis)
            val dias = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMillis)

            when {
                minutos < 1 -> "Ahora"
                minutos < 60 -> "hace ${minutos}m"
                horas < 24 -> {
                    val cal = java.util.Calendar.getInstance().apply { time = fecha }
                    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                    val minute = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
                    "$hour:$minute"
                }
                dias < 7 -> {
                    val cal = java.util.Calendar.getInstance().apply { time = fecha }
                    val diasSemana = listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
                    diasSemana[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                }
                else -> {
                    val cal = java.util.Calendar.getInstance().apply { time = fecha }
                    val day = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                    val month = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
                    "$day/$month"
                }
            }
        } catch (e: Exception) {
            fechaStr
        }
    }
}