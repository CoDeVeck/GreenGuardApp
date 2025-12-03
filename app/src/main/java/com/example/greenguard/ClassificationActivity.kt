package com.example.greenguard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greenguard.databinding.ActivityClassificationBinding

class ClassificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassificationBinding

    private var imageUri: Uri? = null

    private var simulatedPoints: Int = 0
    private var simulatedRiskLevel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val showLoading = intent.getBooleanExtra("showLoading", false)
        if (showLoading) {
            showInitialLoading()
        }
        loadImage()
        setupListeners()
        performAIAnalysis()
    }

    private fun showInitialLoading() {
        binding.loadingOverlay.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.loadingOverlay.visibility = View.GONE
        }, 500)
    }

    private fun loadImage() {
        val uriString = intent.getStringExtra("imageUri")
        if (uriString != null) {
            if (uriString.startsWith("/")) {
                val file = java.io.File(uriString)
                binding.ivCapturedImage.setImageURI(Uri.fromFile(file))
            } else {
                imageUri = Uri.parse(uriString)
                binding.ivCapturedImage.setImageURI(imageUri)
            }
        }
    }

    private fun setupListeners() {
        // Access views directly via the binding object
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditImage.setOnClickListener {
            // Editar imagen
            Toast.makeText(this, "Editar imagen", Toast.LENGTH_SHORT).show()
        }


        binding.btnSubmitReport.setOnClickListener {
            submitReport()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun performAIAnalysis() {
        // Aquí integrarías tu modelo de IA para analizar la imagen
        // Por ahora simulamos el análisis y ASIGNAMOS LOS VALORES
        simulatedPoints = 150
        simulatedRiskLevel = "Riesgo Bajo"

        Toast.makeText(this, "Analizando imagen con IA...", Toast.LENGTH_SHORT).show()

        // Mostrar puntos simulados en la UI
        binding.tvEstimatedPoints.text = "$simulatedPoints Puntos"
    }

    private fun submitReport() {
        // Access etDescription via binding
        val description = binding.etDescription.text.toString()

        if (description.isEmpty()) {
            Toast.makeText(this, "Agrega una descripción", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, ReportConfirmationActivity::class.java)
        intent.putExtra("points", simulatedPoints) // Pass the assigned simulated value
        intent.putExtra("riskLevel", simulatedRiskLevel) // Pass the assigned simulated value
        startActivity(intent)
        finish()
    }
}