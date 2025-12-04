package com.example.greenguard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.NotificationsAdapter
import com.example.greenguard.databinding.ActivityNotificationsBinding
import com.example.greenguard.domain.model.dto.Notification
import com.example.greenguard.domain.model.dto.NotificationType

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var todayAdapter: NotificationsAdapter
    private lateinit var yesterdayAdapter: NotificationsAdapter
    private lateinit var weekAdapter: NotificationsAdapter

    private val notificationsToday = mutableListOf<Notification>()
    private val notificationsYesterday = mutableListOf<Notification>()
    private val notificationsWeek = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        loadNotifications()
        setupListeners()
    }

    private fun setupRecyclerViews() {
        // Hoy
        todayAdapter = NotificationsAdapter(notificationsToday) { notification ->
            onNotificationClick(notification)
        }
        binding.rvNotificationsToday.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = todayAdapter
        }

        // Ayer
        yesterdayAdapter = NotificationsAdapter(notificationsYesterday) { notification ->
            onNotificationClick(notification)
        }
        binding.rvNotificationsYesterday.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = yesterdayAdapter
        }

        // Esta Semana
        weekAdapter = NotificationsAdapter(notificationsWeek) { notification ->
            onNotificationClick(notification)
        }
        binding.rvNotificationsWeek.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = weekAdapter
        }
    }

    private fun loadNotifications() {
        // Notificaciones de Hoy
        notificationsToday.addAll(
            listOf(
                Notification(
                    id = 1,
                    title = "Tu reporte ha sido recibido",
                    description = "El reporte sobre el contenedor desbordado está siendo revisado.",
                    time = "hace 5m",
                    isUnread = true,
                    type = NotificationType.REPORT_RECEIVED
                ),
                Notification(
                    id = 2,
                    title = "¡Nuevo cupón desbloqueado!",
                    description = "15% de descuento en tu próxima compra en EcoTienda.",
                    time = "10:30 AM",
                    isUnread = true,
                    type = NotificationType.REWARD
                )
            )
        )

        // Notificaciones de Ayer
        notificationsYesterday.addAll(
            listOf(
                Notification(
                    id = 3,
                    title = "Logro conseguido",
                    description = "¡Felicidades! Has ganado la medalla 'Guardián Comunitario'.",
                    time = "8:15 AM",
                    isUnread = false,
                    type = NotificationType.ACHIEVEMENT
                ),
                Notification(
                    id = 4,
                    title = "Reporte resuelto",
                    description = "El reporte de luz dañada ha sido reparado. ¡Gracias por tu ayuda!",
                    time = "11:45 AM",
                    isUnread = false,
                    type = NotificationType.REPORT_RESOLVED
                )
            )
        )

        // Notificaciones de Esta Semana
        notificationsWeek.addAll(
            listOf(
                Notification(
                    id = 5,
                    title = "Nueva actualización disponible",
                    description = "Hemos mejorado el mapa de reportes. Actualiza para ver los cambios.",
                    time = "Miércoles",
                    isUnread = false,
                    type = NotificationType.UPDATE
                )
            )
        )

        todayAdapter.notifyDataSetChanged()
        yesterdayAdapter.notifyDataSetChanged()
        weekAdapter.notifyDataSetChanged()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnMarkAllRead.setOnClickListener {
            markAllAsRead()
        }
    }

    private fun onNotificationClick(notification: Notification) {
        // Marcar como leída
        notification.isUnread = false
        todayAdapter.notifyDataSetChanged()
        yesterdayAdapter.notifyDataSetChanged()
        weekAdapter.notifyDataSetChanged()

        // Navegar según el tipo
        when (notification.type) {
            NotificationType.REPORT_RECEIVED,
            NotificationType.REPORT_RESOLVED -> {
                Toast.makeText(this, "Abrir detalles del reporte", Toast.LENGTH_SHORT).show()
                // Navegar a detalles del reporte
            }
            NotificationType.REWARD -> {
                Toast.makeText(this, "Abrir cupones", Toast.LENGTH_SHORT).show()
                // Navegar a cupones
            }
            NotificationType.ACHIEVEMENT -> {
                Toast.makeText(this, "Ver logros", Toast.LENGTH_SHORT).show()
                // Navegar a logros
            }
            NotificationType.UPDATE -> {
                Toast.makeText(this, "Ver actualización", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun markAllAsRead() {
        notificationsToday.forEach { it.isUnread = false }
        notificationsYesterday.forEach { it.isUnread = false }
        notificationsWeek.forEach { it.isUnread = false }

        todayAdapter.notifyDataSetChanged()
        yesterdayAdapter.notifyDataSetChanged()
        weekAdapter.notifyDataSetChanged()

        Toast.makeText(this, "Todas las notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show()
    }


}