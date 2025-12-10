package com.example.greenguard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityCanjeExitosoBinding

class CanjeExitosoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCanjeExitosoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanjeExitosoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos recibidos
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val codigoCupon = intent.getStringExtra("codigoCupon") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""
        val imagen = intent.getIntExtra("imagen", R.drawable.ic_launcher_background)

        // UI
        binding.tvDescripcion.text = descripcion
        binding.tvCodigo.text = codigoCupon
        binding.tvValidez.text = "Válido hasta: $fecha"
        binding.imgCupon.setImageResource(imagen)

        binding.btnVerCupones.setOnClickListener {
            // Abrir pantalla de mis cupones
            finish()
        }

        binding.btnExplorar.setOnClickListener {
            finish()
        }

        binding.btnCerrar.setOnClickListener {
            finish()
        }
    }
}