package com.example.greenguard

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
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
        val qrBase64 = intent.getStringExtra("qrBase64")

        binding.tvDescripcion.text = descripcion
        binding.tvCodigo.text = codigoCupon
        binding.tvValidez.text = "Válido hasta: $fecha"

        if (!qrBase64.isNullOrEmpty()) {
            val bytes = Base64.decode(qrBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imgCupon.setImageBitmap(bitmap)
        }

        binding.btnVerCupones.setOnClickListener {
            val intent = Intent(this, MisCuponesActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnExplorar.setOnClickListener {
            val intent = Intent(this, CatalogoCuponesActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnCerrar.setOnClickListener {
            finish() // solo cierra la activity actual
        }

    }
}