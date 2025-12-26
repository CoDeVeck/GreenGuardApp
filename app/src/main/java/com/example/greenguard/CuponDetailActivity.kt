package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.greenguard.data.api.CuponApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.data.repository.CuponRepository
import com.example.greenguard.databinding.ActivityCuponDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class CuponDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuponDetailBinding
    private lateinit var repository: CuponRepository
    private lateinit var userPreferences: UserPreferences

    private var idCupon: Int = 0
    private var nombreCupon: String = ""
    private var descCupon: String = ""
    private var puntosRequeridos: Int = 0
    private var puntosUsuario: Int = 0
    private var nomTienda: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuponDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        // Crear API
        val retrofit = RetrofitInstance.create(userPreferences)
        val cuponApi = retrofit.create(CuponApi::class.java)

        // Crear Repository
        repository = CuponRepository(cuponApi)
        obtenerDatosIntent()
        setupUI()
        setupListeners()
    }

    private fun obtenerDatosIntent() {
        idCupon = intent.getIntExtra("idCupon", 0)
        nombreCupon = intent.getStringExtra("nombreCupon") ?: ""
        descCupon = intent.getStringExtra("descCupon") ?: ""
        puntosRequeridos = intent.getIntExtra("puntosRequeridos", 0)
        nomTienda = intent.getStringExtra("nomTienda") ?: "N/A"
        puntosUsuario = intent.getIntExtra("puntosUsuario", 0)
    }

    private fun setupUI() {
        binding.tvTituloCupon.text = nombreCupon
        binding.tvDescripcionCupon.text = descCupon
        binding.tvCosto.text = "Costo: $puntosRequeridos Puntos"
        binding.tvTusPuntos.text = "Tus puntos: $puntosUsuario"
        binding.tvTienda.text = "Tienda asociada: $nomTienda"

        // Calcular progreso
        val progreso = if (puntosRequeridos > 0) {
            (puntosUsuario * 100) / puntosRequeridos
        } else 0
        binding.progresoPuntos.progress = progreso.coerceIn(0, 100)

        // Configurar botón según disponibilidad de puntos
        if (puntosUsuario >= puntosRequeridos) {
            binding.btnCanjear.text = "Canjear por $puntosRequeridos puntos"
            binding.btnCanjear.isEnabled = true
        } else {
            val faltantes = puntosRequeridos - puntosUsuario
            binding.btnCanjear.text = "Te faltan $faltantes puntos"
            binding.btnCanjear.isEnabled = false
            binding.btnCanjear.alpha = 0.5f
        }
    }

    private fun setupListeners() {
        binding.btnCanjear.setOnClickListener {
            if (puntosUsuario >= puntosRequeridos) {
                mostrarConfirmacionCanje()
            }
        }
    }

    private fun mostrarConfirmacionCanje() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_confirmar_canje, null)
        dialog.setContentView(view)

        val tvDescripcionCupon = view.findViewById<TextView>(R.id.tvDescripcionCupon)
        val tvPuntosActuales = view.findViewById<TextView>(R.id.tvPuntosActuales)
        val tvPuntosDescontar = view.findViewById<TextView>(R.id.tvPuntosDescontar)
        val tvSaldoFinal = view.findViewById<TextView>(R.id.tvSaldoFinal)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        tvDescripcionCupon.text = nombreCupon
        tvPuntosActuales.text = puntosUsuario.toString()
        tvPuntosDescontar.text = "- $puntosRequeridos"
        tvSaldoFinal.text = (puntosUsuario - puntosRequeridos).toString()

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
            canjearCupon()
        }

        dialog.show()
    }

    private fun canjearCupon() {
        lifecycleScope.launch {
            try {
                // Mostrar loading
                binding.btnCanjear.isEnabled = false
                binding.btnCanjear.text = "Canjeando..."

                val result = repository.comprarCupon(idCupon)

                result.onSuccess { response ->
                    if (response.valor) {
                        // Canje exitoso
                        Toast.makeText(
                            this@CuponDetailActivity,
                            response.mensaje,
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a pantalla de éxito
                        val intent = Intent(
                            this@CuponDetailActivity,
                            CanjeExitosoActivity::class.java
                        ).apply {
                            putExtra("descripcion", nombreCupon)
                            putExtra("codigoCupon", response.codigoCupon)
                            putExtra("fecha", response.fechaCanje)
                            putExtra("qrBase64", response.qrBase64)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@CuponDetailActivity,
                            response.mensaje,
                            Toast.LENGTH_LONG
                        ).show()
                        binding.btnCanjear.isEnabled = true
                        binding.btnCanjear.text = "Canjear por $puntosRequeridos puntos"
                    }
                }.onFailure { error ->
                    Toast.makeText(
                        this@CuponDetailActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnCanjear.isEnabled = true
                    binding.btnCanjear.text = "Canjear por $puntosRequeridos puntos"
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CuponDetailActivity,
                    "Error al canjear: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                binding.btnCanjear.isEnabled = true
                binding.btnCanjear.text = "Canjear por $puntosRequeridos puntos"
            }
        }
    }
}