package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.CategoryAdapter
import com.example.greenguard.adapter.RecentActivityAdapter
import com.example.greenguard.data.api.ReporteApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance

import com.example.greenguard.databinding.ActivityMainBinding
import com.example.greenguard.domain.model.dto.CategoryMain
import com.example.greenguard.domain.model.dto.RecentActivity
import com.example.greenguard.domain.model.entities.Reporte
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPrefs: UserPreferences
    private lateinit var reporteApi: ReporteApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPreferences(applicationContext)

        val retrofit = RetrofitInstance.create(userPrefs)
        reporteApi = retrofit.create(ReporteApi::class.java)

        loadUserData()
        setupBottomNavigation()
        setupListeners()

        loadDashboardData()
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val nombre = userPrefs.obtenerNombreUsuario()
            binding.tvUserName.text = nombre ?: "Usuario"
        }
    }

    private fun loadDashboardData() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val idUsuario = userPrefs.obtenerIdUsuario()

                val response = reporteApi.getDashboard(idUsuario)

                if (response.isSuccessful && response.body() != null) {
                    val dashboard = response.body()!!

                    binding.tvTotalPoints.text = dashboard.puntosTotales.toString()
                    binding.tvReportsCount.text = dashboard.totalReportes.toString()
                    binding.tvPeopleCount.text = dashboard.puntosMes.toString()
                    binding.tvPeopleBenefited.text =
                        getString(
                            R.string.main_people_benefited,
                            dashboard.totalUsuariosBeneficiados
                        )
                    setupCategories(dashboard.categoriasMasReportadas)

                    setupRecentActivity(dashboard.reportesRecientes)

                    showLoading(false)
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this@MainActivity,
                        "Error al cargar datos: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Cargar datos de ejemplo como fallback (opcional)
                    setupCategoriesDefault()
                }

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun setupRecentActivity(reportes: List<Reporte>) {
        if (reportes.isEmpty()) {
            binding.rvRecentActivity.visibility = View.GONE
            return
        }

        // AQUÍ SE REEMPLAZAN LOS DATOS HARDCODEADOS CON LOS REALES DEL BACKEND
        val activities = reportes.map { reporte ->
            RecentActivity(
                id = reporte.idReporte,
                title = reporte.tipoIncidente?.descTipoInci ?: "",
                date = reporte.repoRegistado.toString(),
                status = reporte.estado.toString(),
                imageUrl = reporte.imagenRepo ?: "",
            )
        }

        binding.rvRecentActivity.visibility = View.VISIBLE
        binding.rvRecentActivity.layoutManager = LinearLayoutManager(this)
        binding.rvRecentActivity.adapter = RecentActivityAdapter(activities)
    }

    private fun setupCategories(stats: List<com.example.greenguard.domain.model.dto.ReporteStatsDTO>) {
        if (stats.isEmpty()) {
            binding.rvCategories.visibility = View.GONE
            return
        }

        val categories = stats.map { stat ->
            val nombreCategoria = stat.categoria.descTipoInci

            val iconRes = when (nombreCategoria.uppercase()) {
                "BACHES", "PISTAS CON HUECOS" -> R.drawable.ic_road
                "VEREDA RAJADA" -> R.drawable.ic_sidewalk_broken
                "BASURA ACUMULADA" -> R.drawable.ic_trash
                "POSTE CAÍDO", "ALUMBRADO PÚBLICO" -> R.drawable.ic_lightbulb
                "SEMÁFORO MALOGRADO" -> R.drawable.ic_traffic_light
                "FUGA DE AGUA" -> R.drawable.ic_water_leak
                "ÁRBOL CAÍDO" -> R.drawable.ic_tree_fall
                "OTROS" -> R.drawable.ic_info
                else -> R.drawable.ic_launcher_background
            }

            CategoryMain(
                name = nombreCategoria,
                reportsCount = "${stat.totalReportes} reportes",
                iconRes = iconRes
            )
        }

        binding.rvCategories.visibility = View.VISIBLE
        binding.rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = CategoryAdapter(categories)
    }

    private fun setupCategoriesDefault() {
        val categories = listOf(
            CategoryMain("Basura", "126 reportes", R.drawable.ic_trash),
            CategoryMain("Alumbrado", "89 reportes", R.drawable.ic_lightbulb),
            CategoryMain("Baches", "74 reportes", R.drawable.ic_road)
        )

        binding.rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = CategoryAdapter(categories)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_reports -> {
                    val intent = Intent(this, ReportHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_rewards -> {
                    val intent = Intent(this, CatalogoCuponesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.ivNotifications.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        binding.fabCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.btnRedeem.setOnClickListener {
            val intent = Intent(this, CatalogoCuponesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(show: Boolean) {
        // Si tienes un ProgressBar en tu layout, úsalo aquí
        // binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }
}