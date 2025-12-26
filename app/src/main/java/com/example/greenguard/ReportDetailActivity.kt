package com.example.greenguard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.greenguard.data.api.ReporteApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityReportDetailBinding
import com.example.greenguard.domain.model.dto.DetalleReporteHistorialCliente
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportDetailBinding
    private var idReporte: Int = -1
    private lateinit var reporteApi: ReporteApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID del reporte desde el intent
        idReporte = intent.getIntExtra("REPORT_ID", -1)

        if (idReporte == -1) {
            Toast.makeText(this, "Error: ID de reporte inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupRepository()
        setupViews()
        loadReportData()
    }
    private fun setupRepository() {
        val userPreferences = UserPreferences(this)

        reporteApi = RetrofitInstance
            .create(userPreferences)
            .create(ReporteApi::class.java)
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadReportData() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = reporteApi.obtenerDetalleReporte(idReporte)

                if (response.isNotEmpty()) {
                    val detalle = response[0]
                    displayReportDetails(detalle)
                } else {
                    Toast.makeText(
                        this@ReportDetailActivity,
                        "No se encontró información del reporte",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                showLoading(false)

            } catch (e: Exception) {
                Log.e("ReportDetail", "Error al cargar detalle del reporte", e)
                showLoading(false)
                Toast.makeText(
                    this@ReportDetailActivity,
                    "Error al cargar el reporte: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun displayReportDetails(detalle: DetalleReporteHistorialCliente) {
        binding.apply {
            tvReportTitle.text = detalle.incidente
            tvReportDate.text = "Reportado: ${formatearFecha(detalle.repoRegistado)}"

            tvLocation.text = "Lat: ${detalle.latitud}, Lon: ${detalle.longitud}"
            getAddressFromCoordinates(detalle.latitud.toDouble(), detalle.longitud.toDouble())

            tvDescription.text = detalle.descripcion ?: "Sin descripción"

            val (criticidadTexto, colorCriticidad) = when (detalle.idTipoClasi) {
                1 -> Pair("Crítico", R.color.critical_red)
                2 -> Pair("Medio", R.color.warning_orange)
                3 -> Pair("Bajo", R.color.success_green)
                else -> Pair("Sin clasificar", R.color.gray)
            }

            tvCriticality.text = criticidadTexto
            cardCriticality.setCardBackgroundColor(getColor(colorCriticidad))

            tvTotalPoints.text = "${detalle.puntosGanados} puntos totales"

            when (detalle.estado) {
                "Resuelto" -> {
                    tvAuthorityResponse.text = "El reporte ha sido resuelto satisfactoriamente."
                    tvEstimatedTime.text = "Completado el ${formatearFecha(detalle.repoResuelto)}"
                }
                "En Proceso" -> {
                    tvAuthorityResponse.text = "El reporte está siendo atendido por las autoridades."
                    tvEstimatedTime.text = "En proceso desde ${formatearFecha(detalle.repoProceso)}"
                }
                else -> {
                    tvAuthorityResponse.text = "El reporte ha sido recibido y está pendiente de revisión."
                    tvEstimatedTime.text = "Pendiente de atención"
                }
            }

            if (!detalle.imagenRepo.isNullOrEmpty()) {
                Glide.with(this@ReportDetailActivity)
                    .load(detalle.imagenRepo)
                    .placeholder(R.drawable.ic_reporte)
                    .error(R.drawable.ic_close)
                    .into(ivReportImage)
            }
        }

        setupStatusTimeline(detalle)
    }

    private fun setupStatusTimeline(detalle: DetalleReporteHistorialCliente) {
        // Aquí puedes configurar el timeline según el estado
        // Por ejemplo, marcar como completados los estados según las fechas
        val tieneRegistrado = !detalle.repoRegistado.isNullOrEmpty()
        val tieneProceso = !detalle.repoProceso.isNullOrEmpty()
        val tieneResuelto = !detalle.repoResuelto.isNullOrEmpty()

        // Lógica para actualizar el timeline visualmente
        // Esto depende de cómo tengas implementado tu timeline en el XML
    }

    private fun formatearFecha(fecha: String): String {
        return try {

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(fecha)
            date?.let { outputFormat.format(it) } ?: fecha
        } catch (e: Exception) {
            // Si no se puede parsear, devolver tal cual
            fecha
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        binding.contentLayout?.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun getAddressFromCoordinates(lat: Double, lng: Double) {
        lifecycleScope.launch {
            try {
                val latlng = "$lat,$lng"
                val response = RetrofitInstance.GeocodingClient.api
                    .getAddressFromCoordinates(
                        latlng = latlng,
                        apiKey = BuildConfig.MAPS_API_KEY
                    )

                if (response.status == "OK" && response.results.isNotEmpty()) {
                    val address = response.results[0].formatted_address
                    binding.tvLocation.text = address
                } else {
                    Log.w("ReportDetail", "No se pudo obtener la dirección: ${response.status}")
                }
            } catch (e: Exception) {
                Log.e("ReportDetail", "Error al obtener dirección", e)
            }
        }
    }

}