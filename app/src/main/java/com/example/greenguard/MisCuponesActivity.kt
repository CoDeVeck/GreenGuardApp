package com.example.greenguard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.greenguard.data.api.CuponApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.data.repository.CuponRepository
import com.example.greenguard.databinding.ActivityMisCuponesBinding
import com.example.greenguard.databinding.ItemMiCuponBinding
import com.example.greenguard.domain.model.dto.EstadoUsuarioCupon
import com.example.greenguard.domain.model.dto.UsuarioCuponDto
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MisCuponesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisCuponesBinding
    private lateinit var repository: CuponRepository

    private var todosLosCupones = listOf<UsuarioCuponDto>()
    private var cuponesActivos = listOf<UsuarioCuponDto>()
    private var cuponesUsados = listOf<UsuarioCuponDto>()
    private var cuponesVencidos = listOf<UsuarioCuponDto>()
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCuponesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        // Crear API
        val retrofit = RetrofitInstance.create(userPreferences)
        val cuponApi = retrofit.create(CuponApi::class.java)

        repository = CuponRepository(cuponApi)

        setupListeners()
        cargarCupones()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> mostrarCupones(cuponesActivos)
                    1 -> mostrarCupones(cuponesUsados)
                    2 -> mostrarCupones(cuponesVencidos)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun cargarCupones() {
        lifecycleScope.launch {
            try {
                val result = repository.obtenerMisCupones()

                result.onSuccess { cupones ->
                    todosLosCupones = cupones
                    clasificarCupones(cupones)
                    mostrarCupones(cuponesActivos)
                }.onFailure { error ->
                    Toast.makeText(
                        this@MisCuponesActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MisCuponesActivity,
                    "Error al cargar cupones: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                // binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun clasificarCupones(cupones: List<UsuarioCuponDto>) {
        cuponesActivos = cupones.filter {
            it.estado == EstadoUsuarioCupon.AC
        }

        cuponesUsados = cupones.filter {
            it.estado == EstadoUsuarioCupon.CA
        }

        cuponesVencidos = cupones.filter {
            it.estado == EstadoUsuarioCupon.VE
        }
    }

    private fun mostrarCupones(usuarioCupones: List<UsuarioCuponDto>) {
        binding.cuponesListContainer.removeAllViews()

        if (usuarioCupones.isEmpty()) {
            mostrarMensajeVacio()
            return
        }

        usuarioCupones.forEach { usuarioCupon ->
            val itemBinding = ItemMiCuponBinding.inflate(
                LayoutInflater.from(this),
                binding.cuponesListContainer,
                false
            )

            configurarCupon(itemBinding, usuarioCupon)
            binding.cuponesListContainer.addView(itemBinding.root)
        }
    }

    private fun mostrarMensajeVacio() {
        val emptyText = TextView(this).apply {
            text = "No hay cupones en esta categoría"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            setPadding(32, 64, 32, 32)
            gravity = android.view.Gravity.CENTER
        }
        binding.cuponesListContainer.addView(emptyText)
    }

    private fun configurarCupon(
        itemBinding: ItemMiCuponBinding,
        usuarioCupon: UsuarioCuponDto
    ) {
        with(itemBinding) {
            tvCuponTitulo.text = usuarioCupon.nombreCupon
            tvCuponEstado.text = obtenerTextoEstado(usuarioCupon.estado.toString())
            tvCuponEstado.setTextColor(obtenerColorEstado(usuarioCupon.estado.toString()))

            tvCuponFecha.text = when (usuarioCupon.estado) {
                EstadoUsuarioCupon.CA ->
                    "Canjeado el: ${usuarioCupon.fechaCanje ?: "N/A"}"

                EstadoUsuarioCupon.VE ->
                    "Cupón vencido"

                EstadoUsuarioCupon.AC ->
                    "Disponible"
            }

            tvCuponCodigo.text = usuarioCupon.codigoCupon

            // Botón Mostrar
            btnMostrar.setOnClickListener {
                if (usuarioCupon.estado == EstadoUsuarioCupon.AC) {
                    mostrarDetalleCupon(usuarioCupon)
                }
            }

            when (usuarioCupon.estado) {
                EstadoUsuarioCupon.CA,
                EstadoUsuarioCupon.VE -> {
                    btnMostrar.isEnabled = false
                    btnMostrar.alpha = 0.5f
                    btnMostrar.text = "No disponible"
                }

                EstadoUsuarioCupon.AC -> {
                    btnMostrar.isEnabled = true
                    btnMostrar.alpha = 1f
                    btnMostrar.text = "Mostrar"
                }
            }
        }
    }


    private fun mostrarDetalleCupon(usuarioCupon: UsuarioCuponDto) {
        val intent = Intent(this, DetailQrCuponActivity::class.java).apply {
            putExtra("idUsuarioCupon", usuarioCupon.idUsuarioCupon)
            putExtra("nombreCupon", usuarioCupon.nombreCupon)
            putExtra("puntosRequeridos", usuarioCupon.puntosRequeridos)
            putExtra("codigoCupon", usuarioCupon.codigoCupon)
            putExtra("fechaCanje", usuarioCupon.fechaCanje?.toString())
            putExtra("imagen", R.drawable.ic_launcher_background)
            putExtra("nombreTienda", usuarioCupon.nombreTienda)
            putExtra("qrBase64", usuarioCupon.qrBase64)
            putExtra("distritoTienda", usuarioCupon.distritoTienda)
        }
        startActivity(intent)
    }

    private fun devolverCupon(idUsuarioCupon: Int) {
        lifecycleScope.launch {
            try {
                val result = repository.devolverCupon(idUsuarioCupon)

                result.onSuccess { response ->
                    Toast.makeText(
                        this@MisCuponesActivity,
                        response.mensaje,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (response.valor) {
                        cargarCupones()
                    }
                }.onFailure { error ->
                    Toast.makeText(
                        this@MisCuponesActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MisCuponesActivity,
                    "Error al devolver: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun obtenerTextoEstado(estado: String): String {
        return when(estado) {
            "ACTIVO" -> "Activo"
            "VENCE_PRONTO" -> "Vence pronto"
            "USADO" -> "Usado"
            "VENCIDO" -> "Vencido"
            else -> estado
        }
    }

    private fun obtenerColorPorEstado(estado: String): String {
        return when(estado) {
            "ACTIVO" -> "#A5D6A7"
            "VENCE_PRONTO" -> "#FFB74D"
            "USADO" -> "#90CAF9"
            "VENCIDO" -> "#BDBDBD"
            else -> "#4CAF50"
        }
    }

    private fun obtenerColorEstado(estado: String): Int {
        return when(estado) {
            "ACTIVO" -> ContextCompat.getColor(this, android.R.color.holo_green_dark)
            "VENCE_PRONTO" -> ContextCompat.getColor(this, android.R.color.holo_orange_dark)
            "USADO", "VENCIDO" -> ContextCompat.getColor(this, android.R.color.darker_gray)
            else -> ContextCompat.getColor(this, android.R.color.darker_gray)
        }
    }



    override fun onResume() {
        super.onResume()
        cargarCupones()
    }
}