package com.example.greenguard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityReportDetailBinding

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        loadReportData()
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadReportData() {
        // Datos ficticios del reporte
        binding.apply {
            // Título y descripción
            tvReportTitle.text = "Vertido ilegal de residuos tóxicos"
            tvReportDate.text = "Reportado: 24/11/2025 22:47"
            tvLocation.text = "Parque Central, Calle Principal 123, Madrid"
            tvDescription.text = "Se ha detectado un vertido de líquidos sospechosos en el área del parque. El líquido tiene un color oscuro y emite un olor químico fuerte. Se recomienda precaución y señalización del sitio."

            // Criticidad
            tvCriticality.text = "Crítico"
            cardCriticality.setCardBackgroundColor(getColor(R.color.critical_red))

            // Puntos
            tvTotalPoints.text = "250 puntos totales"

            // Respuesta de autoridades
            tvAuthorityResponse.text = "Equipo de limpieza especializado ha sido despachado al sitio. Se realizará análisis de muestras para determinar la naturaleza de los residuos."
            tvEstimatedTime.text = "2-4 días hábiles"

            // Cargar imagen (usa Glide o tu método preferido)
            // Glide.with(this@ReportDetailActivity)
            //     .load("URL_DE_LA_IMAGEN")
            //     .into(ivReportImage)
        }

        setupStatusTimeline()
    }

    private fun setupStatusTimeline() {
        // Aquí configurarías el timeline programáticamente

    }
}