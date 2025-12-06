package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.greenguard.databinding.ActivityReportConfirmationBinding

class ReportConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportConfirmationBinding

    private var pointsEarned: Int = 0
    private var riskLevel: String = ""
    private var reportNumber: String = ""
    private var tipoIncidente: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        setupListeners()
        animateSuccess()
    }

    private fun loadData() {
        // Obtener datos del Intent
        pointsEarned = intent.getIntExtra("points", 55)
        riskLevel = intent.getStringExtra("riskLevel") ?: "Riesgo Medio"
        reportNumber = intent.getStringExtra("reportNumber") ?: "N/A"
        tipoIncidente = intent.getStringExtra("tipoIncidente") ?: "Reporte"

        // Mostrar datos
        binding.tvPointsEarned.text = "+$pointsEarned puntos"
        binding.tvRiskLevel.text = "por tu reporte de $riskLevel"

        // Si tienes un TextView para el número de reporte, muéstralo
        // binding.tvReportNumber?.text = "Reporte #$reportNumber"
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            navigateToHome()
        }

        binding.btnViewReports.setOnClickListener {
            navigateToReports()
        }

        binding.btnReportAnother.setOnClickListener {
            navigateToCamera()
        }
    }

    private fun animateSuccess() {
        val cardAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        (binding.root.findViewById(R.id.headerLayout) as? LinearLayout)?.startAnimation(cardAnimation)
        animatePoints()
    }

    private fun animatePoints() {
        val targetPoints = pointsEarned
        val duration = 1500L
        val startTime = System.currentTimeMillis()

        val handler = android.os.Handler(mainLooper)
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / duration).coerceAtMost(1f)
                val currentPoints = (targetPoints * progress).toInt()

                binding.tvPointsEarned.text = "+$currentPoints puntos"

                if (progress < 1f) {
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToReports() {
        // Implementar cuando tengas MyReportsActivity
        navigateToHome()
    }

    private fun navigateToCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }
}