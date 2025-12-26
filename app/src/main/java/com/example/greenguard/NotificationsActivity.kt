package com.example.greenguard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.NotificationsAdapter
import com.example.greenguard.data.api.NotisApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.data.repository.NotificacionRepository
import com.example.greenguard.databinding.ActivityNotificationsBinding
import com.example.greenguard.domain.model.dto.NotificacionResponseDTO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var todayAdapter: NotificationsAdapter
    private lateinit var yesterdayAdapter: NotificationsAdapter
    private lateinit var weekAdapter: NotificationsAdapter

    private val notificationsToday = mutableListOf<NotificacionResponseDTO>()
    private val notificationsYesterday = mutableListOf<NotificacionResponseDTO>()
    private val notificationsWeek = mutableListOf<NotificacionResponseDTO>()

    private lateinit var repository: NotificacionRepository
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRepository()
        setupRecyclerViews()
        setupListeners()
        loadNotificationsFromApi()
    }

    private fun initRepository() {
        userPreferences = UserPreferences(this)
        val notisApi = RetrofitInstance.create(userPreferences)
            .create(NotisApi::class.java)
        repository = NotificacionRepository(notisApi, userPreferences)
    }

    private fun setupRecyclerViews() {
        todayAdapter = NotificationsAdapter(notificationsToday) { notification ->
            onNotificationClick(notification)
        }
        binding.rvNotificationsToday.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = todayAdapter
        }

        yesterdayAdapter = NotificationsAdapter(notificationsYesterday) { notification ->
            onNotificationClick(notification)
        }
        binding.rvNotificationsYesterday.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = yesterdayAdapter
        }

        weekAdapter = NotificationsAdapter(notificationsWeek) { notification ->
            onNotificationClick(notification)
        }
        binding.rvNotificationsWeek.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = weekAdapter
        }
    }

    private fun loadNotificationsFromApi() {
        showLoading(true)

        lifecycleScope.launch {
            repository.listarTodasNotificaciones()
                .onSuccess { notificaciones ->
                    showLoading(false)
                    procesarNotificaciones(notificaciones)
                }
                .onFailure { error ->
                    showLoading(false)
                    Toast.makeText(
                        this@NotificationsActivity,
                        "Error al cargar notificaciones: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun procesarNotificaciones(notificaciones: List<NotificacionResponseDTO>) {
        notificationsToday.clear()
        notificationsYesterday.clear()
        notificationsWeek.clear()

        val now = Calendar.getInstance()
        val hoy = now.get(Calendar.DAY_OF_YEAR)
        val añoActual = now.get(Calendar.YEAR)

        notificaciones.forEach { notificacion ->
            val fechaNotif = parseFecha(notificacion.fechaCreacion)

            val calNotif = Calendar.getInstance().apply {
                time = fechaNotif
            }

            val diaNotif = calNotif.get(Calendar.DAY_OF_YEAR)
            val añoNotif = calNotif.get(Calendar.YEAR)

            val diasDiferencia = if (añoActual == añoNotif) {
                hoy - diaNotif
            } else {
                val diff = now.timeInMillis - calNotif.timeInMillis
                TimeUnit.MILLISECONDS.toDays(diff).toInt()
            }

            when {
                diasDiferencia == 0 -> notificationsToday.add(notificacion)
                diasDiferencia == 1 -> notificationsYesterday.add(notificacion)
                diasDiferencia in 2..30 -> notificationsWeek.add(notificacion)  // 👈 Cambio de 7 a 30
            }
        }

        todayAdapter.notifyDataSetChanged()
        yesterdayAdapter.notifyDataSetChanged()
        weekAdapter.notifyDataSetChanged()

        actualizarVisibilidadSecciones()
    }

    private fun parseFecha(fechaStr: String): Date {
        return try {
            // Formato ISO 8601: "2025-12-21T08:00:00"
            val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            formato.parse(fechaStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    private fun actualizarVisibilidadSecciones() {
        // Ocultar/mostrar la sección "Hoy"
        binding.tvHeaderToday.visibility = if (notificationsToday.isEmpty()) View.GONE else View.VISIBLE
        binding.rvNotificationsToday.visibility = if (notificationsToday.isEmpty()) View.GONE else View.VISIBLE

        // Ocultar/mostrar la sección "Ayer"
        binding.tvHeaderYesterday.visibility = if (notificationsYesterday.isEmpty()) View.GONE else View.VISIBLE
        binding.rvNotificationsYesterday.visibility = if (notificationsYesterday.isEmpty()) View.GONE else View.VISIBLE

        // Ocultar/mostrar la sección "Esta Semana"
        binding.tvHeaderWeek.visibility = if (notificationsWeek.isEmpty()) View.GONE else View.VISIBLE
        binding.rvNotificationsWeek.visibility = if (notificationsWeek.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnMarkAllRead.setOnClickListener {
            marcarTodasComoLeidas()
        }
    }

    private fun onNotificationClick(notification: NotificacionResponseDTO) {
        // Marcar como leída en el backend si no está leída
        if (!notification.leida) {
            lifecycleScope.launch {
                repository.marcarComoLeida(notification.idNotificacion)
                    .onSuccess {
                        // Actualizar el objeto localmente
                        val updatedNotification = notification.copy(leida = true)
                        actualizarNotificacionEnListas(notification.idNotificacion, updatedNotification)

                        todayAdapter.notifyDataSetChanged()
                        yesterdayAdapter.notifyDataSetChanged()
                        weekAdapter.notifyDataSetChanged()
                    }
                    .onFailure { error ->
                        Toast.makeText(
                            this@NotificationsActivity,
                            "Error al marcar como leída",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        // Navegar según el tipo de notificación
        navegarSegunTipo(notification)
    }

    private fun actualizarNotificacionEnListas(id: Int, nuevaNotificacion: NotificacionResponseDTO) {
        val indexToday = notificationsToday.indexOfFirst { it.idNotificacion == id }
        if (indexToday != -1) {
            notificationsToday[indexToday] = nuevaNotificacion
            return
        }

        val indexYesterday = notificationsYesterday.indexOfFirst { it.idNotificacion == id }
        if (indexYesterday != -1) {
            notificationsYesterday[indexYesterday] = nuevaNotificacion
            return
        }

        val indexWeek = notificationsWeek.indexOfFirst { it.idNotificacion == id }
        if (indexWeek != -1) {
            notificationsWeek[indexWeek] = nuevaNotificacion
        }
    }

    private fun navegarSegunTipo(notification: NotificacionResponseDTO) {
        when (notification.tipoNotificacion) {
            "REPORTE_REGISTRADO",
            "CAMBIO_ESTADO_REPORTE" -> {
                // Navegar a detalles del reporte
                notification.reporte?.let { reporte ->
                    Toast.makeText(
                        this,
                        "Abrir reporte: ${reporte.numReport}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Intent a ReporteDetalleActivity
                    // val intent = Intent(this, ReporteDetalleActivity::class.java)
                    // intent.putExtra("ID_REPORTE", reporte.idReporte)
                    // startActivity(intent)
                }
            }

            "CUPON_CANJEADO",
            "CUPON_POR_VENCER",
            "CUPON_VENCIDO",
            "CUPON_USADO" -> {
                // Navegar a cupones
                notification.cupon?.let { cupon ->
                    Toast.makeText(
                        this,
                        "Abrir cupón: ${cupon.nombreCupon}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Intent a CuponesActivity
                    // val intent = Intent(this, CuponesActivity::class.java)
                    // intent.putExtra("ID_USUARIO_CUPON", cupon.idUsuarioCupon)
                    // startActivity(intent)
                }
            }

            "PUNTOS_GANADOS" -> {
                Toast.makeText(this, "Ver mis puntos", Toast.LENGTH_SHORT).show()
                // TODO: Intent a PuntosActivity o perfil
                // val intent = Intent(this, PerfilActivity::class.java)
                // startActivity(intent)
            }

            else -> {
                Toast.makeText(this, notification.titulo, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun marcarTodasComoLeidas() {
        showLoading(true)

        lifecycleScope.launch {
            repository.marcarTodasComoLeidas()
                .onSuccess { mensaje ->
                    showLoading(false)

                    // Actualizar todas las notificaciones localmente
                    notificationsToday.replaceAll { it.copy(leida = true) }
                    notificationsYesterday.replaceAll { it.copy(leida = true) }
                    notificationsWeek.replaceAll { it.copy(leida = true) }

                    todayAdapter.notifyDataSetChanged()
                    yesterdayAdapter.notifyDataSetChanged()
                    weekAdapter.notifyDataSetChanged()

                    Toast.makeText(this@NotificationsActivity, mensaje, Toast.LENGTH_SHORT).show()
                }
                .onFailure { error ->
                    showLoading(false)
                    Toast.makeText(
                        this@NotificationsActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun showLoading(show: Boolean) {
        // Si tienes un ProgressBar en tu layout, descoméntalo:
        // binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}