package com.example.greenguard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.content.Intent
import android.view.animation.AnimationUtils
import android.widget.LinearLayout

import androidx.cardview.widget.CardView
import com.example.greenguard.databinding.ActivityReportConfirmationBinding

class ReportConfirmationActivity : AppCompatActivity() {

    // Declare the binding property
    private lateinit var binding: ActivityReportConfirmationBinding

    private var pointsEarned: Int = 0
    private var riskLevel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object and set the content view
        binding = ActivityReportConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initViews is no longer needed
        loadData()
        setupListeners()
        animateSuccess()
    }

    // private fun initViews() { ... } // Removed

    private fun loadData() {
        // Obtener datos del Intent
        pointsEarned = intent.getIntExtra("points", 55)
        riskLevel = intent.getStringExtra("riskLevel") ?: "Riesgo Medio"

        // Access views via binding
        binding.tvPointsEarned.text = "+$pointsEarned puntos"
        binding.tvRiskLevel.text = "por tu reporte de $riskLevel"
    }

    private fun setupListeners() {
        // Access views via binding
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
        // Animación de zoom in para el card
        val cardAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)

        // NOTE: The original code for finding the CardView was faulty.
        // We assume there is a View in your XML that represents the Card/Header
        // that needs animation. For this example, we'll assume the CardView
        // has the ID 'cardViewConfirmation' (if you named it differently in XML,
        // update this access point).

        // If the ID was actually 'headerLayout' and it was a CardView:
        (binding.root.findViewById(R.id.headerLayout) as? LinearLayout)?.startAnimation(cardAnimation)

        // Animar los puntos
        animatePoints()
    }

    private fun animatePoints() {
        // Animación de conteo de puntos
        val targetPoints = pointsEarned
        val duration = 1500L
        val startTime = System.currentTimeMillis()

        val handler = android.os.Handler(mainLooper)
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / duration).coerceAtMost(1f)
                val currentPoints = (targetPoints * progress).toInt()

                // Access tvPointsEarned via binding
                binding.tvPointsEarned.text = "+$currentPoints puntos"

                if (progress < 1f) {
                    handler.postDelayed(this, 16) // ~60fps
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
        /*val intent = Intent(this, MyReportsActivity::class.java)*/
        startActivity(intent)
        finish()
    }

    private fun navigateToCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }


}