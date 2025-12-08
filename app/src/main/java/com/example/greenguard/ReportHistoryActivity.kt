package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.RecentActivityAdapter
import com.example.greenguard.databinding.ActivityReportHistoryBinding
import com.example.greenguard.domain.model.dto.RecentActivity
import com.example.greenguard.domain.model.entities.Reporte

class ReportHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportHistoryBinding
    private lateinit var adapter: RecentActivityAdapter
    private var allReportes = listOf<RecentActivity>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupRecyclerView()
        setupTabs()
        setupClickListeners()
        loadActivities()
    }


    private fun setupRecyclerView() {
        // Crear datos ficticios
        val activities = getDummyActivities()

        // Inicializar adapter con los datos
        adapter = RecentActivityAdapter(activities)

        // Configurar RecyclerView
        binding.rvReports.apply {
            layoutManager = LinearLayoutManager(this@ReportHistoryActivity)
            adapter = this@ReportHistoryActivity.adapter
            setHasFixedSize(true)

            // Agregar OnItemClickListener usando RecyclerView
            addOnItemTouchListener(RecyclerItemClickListener(
                context = this@ReportHistoryActivity,
                recyclerView = this,
                onItemClick = { view, position ->
                    // Recuperar el activity desde el tag
                    val activity = view.tag as? RecentActivity
                    activity?.let { navigateToDetail(it) }
                }
            ))
        }
    }

    private fun navigateToDetail(activity: RecentActivity) {
        val intent = Intent(this, ReportDetailActivity::class.java)
        intent.putExtra("REPORT_TITLE", activity.title)
        intent.putExtra("REPORT_DATE", activity.date)
        intent.putExtra("REPORT_STATUS", activity.status)
        startActivity(intent)
    }

    private fun setupTabs() {
        binding.chipGroupTabs.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chipTodos -> filterActivities(null)
                    R.id.chipPendientes -> filterActivities("Pendiente")
                    R.id.chipEnProceso -> filterActivities("En Proceso")
                    R.id.chipResueltos -> filterActivities("Resuelto")
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadActivities() {
        showLoading(true)

        // Simular carga de datos
        binding.rvReports.postDelayed({
            allReportes = getDummyActivities()
            updateAdapter(allReportes)
            showLoading(false)
        }, 1000)
    }

    private fun filterActivities(status: String?) {
        val filtered = if (status == null) {
            allReportes
        } else {
            allReportes.filter { it.status == status }
        }
        updateAdapter(filtered)
    }


    private fun updateAdapter(activities: List<RecentActivity>) {
        // Crear nuevo adapter con datos filtrados
        adapter = RecentActivityAdapter(activities)
        binding.rvReports.adapter = adapter
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvLoading.visibility = if (show) View.VISIBLE else View.GONE
    }


    // Datos ficticios para mostrar
    private fun getDummyActivities(): List<RecentActivity> {
        return listOf(
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background, // Usa tus propios drawables
                title = "Fuga de agua",
                date = "Riesgo Alto • 25/10/24 10:30",
                status = "Resuelto"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Basura acumulada",
                date = "Riesgo Medio • 24/10/24 15:12",
                status = "En Proceso"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Poste caído",
                date = "Riesgo Alto • 22/10/24 08:45",
                status = "Pendiente"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Bache peligroso",
                date = "Riesgo Bajo • 20/10/24 18:00",
                status = "Resuelto"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Luz de calle fundida",
                date = "Riesgo Medio • 19/10/24 20:15",
                status = "En Proceso"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Vandalismo en muro",
                date = "Riesgo Bajo • 18/10/24 14:30",
                status = "Pendiente"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Árbol caído",
                date = "Riesgo Alto • 17/10/24 09:00",
                status = "Resuelto"
            ),
            RecentActivity(
                imageRes = R.drawable.ic_launcher_background,
                title = "Vereda rota",
                date = "Riesgo Medio • 16/10/24 11:20",
                status = "En Proceso"
            )
        )
    }
}