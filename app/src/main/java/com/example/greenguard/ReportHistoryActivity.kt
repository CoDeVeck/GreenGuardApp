package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.RecentActivityAdapter
import com.example.greenguard.data.api.ReporteApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityReportHistoryBinding
import com.example.greenguard.domain.model.dto.RecentActivity
import com.example.greenguard.domain.model.dto.ReporteHistorialCliente
import kotlinx.coroutines.launch

class ReportHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportHistoryBinding
    private lateinit var adapter: RecentActivityAdapter
    private var allReportes = listOf<RecentActivity>()
    private lateinit var reporteApi: ReporteApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRepository()
        setupRecyclerView()
        setupTabs()
        setupClickListeners()
        loadActivities()
    }
    private fun setupRepository() {
        val userPreferences = UserPreferences(this)

        reporteApi = RetrofitInstance
            .create(userPreferences)
            .create(ReporteApi::class.java)
    }

    private fun setupRecyclerView() {
        adapter = RecentActivityAdapter(emptyList()) { activity ->
            navigateToDetail(activity)
        }

        binding.rvReports.apply {
            layoutManager = LinearLayoutManager(this@ReportHistoryActivity)
            adapter = this@ReportHistoryActivity.adapter
            setHasFixedSize(true)

        }
    }

    private fun navigateToDetail(activity: RecentActivity) {
        val intent = Intent(this, ReportDetailActivity::class.java)
        intent.putExtra("REPORT_ID", activity.id)
        startActivity(intent)
    }

    private fun setupTabs() {
        binding.chipGroupTabs.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chipTodos -> loadActivities(null)
                    R.id.chipPendientes -> loadActivities("PE")
                    R.id.chipEnProceso -> loadActivities("EP")
                    R.id.chipResueltos -> loadActivities("RE")
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadActivities(estado: String? = null) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = reporteApi.historialDeReportes(estado)

                allReportes = response.map { reporte ->
                    mapToRecentActivity(reporte)
                }

                updateAdapter(allReportes)
                showLoading(false)

                if (allReportes.isEmpty()) {
                    Toast.makeText(
                        this@ReportHistoryActivity,
                        "No hay reportes para mostrar",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("ReportHistory", "Error al cargar reportes", e)
                showLoading(false)
                Toast.makeText(
                    this@ReportHistoryActivity,
                    "Error al cargar los reportes: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mapToRecentActivity(reporte: ReporteHistorialCliente): RecentActivity {
        val fechaMostrar = when (reporte.estado) {
            "Resuelto" -> reporte.repoResuelto
            "En Proceso" -> reporte.repoProceso
            else -> reporte.repoRegistado
        }

        val fechaFormateada = formatearFecha(fechaMostrar)

        val nivelRiesgo = when (reporte.idTipoClasi) {
            1 -> "Riesgo Alto"
            2 -> "Riesgo Medio"
            3 -> "Riesgo Bajo"
            else -> "Sin clasificar"
        }

        return RecentActivity(
            id = reporte.idReporte,
            imageUrl = reporte.imagenRepo,
            title = reporte.incidente,
            date = "$nivelRiesgo • $fechaFormateada",
            status = reporte.estado
        )
    }

    private fun formatearFecha(fecha: String): String {
        return try {

            fecha
        } catch (e: Exception) {
            fecha
        }
    }

    private fun updateAdapter(activities: List<RecentActivity>) {
        adapter = RecentActivityAdapter(activities) { activity ->
            navigateToDetail(activity)
        }
        binding.rvReports.adapter = adapter
    }


    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvLoading.visibility = if (show) View.VISIBLE else View.GONE
    }
}