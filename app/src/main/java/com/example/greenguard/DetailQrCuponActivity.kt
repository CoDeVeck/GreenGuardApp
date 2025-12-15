package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.greenguard.data.api.CuponApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.data.repository.CuponRepository
import com.example.greenguard.databinding.ActivityDetailQrCuponBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import kotlinx.coroutines.launch

class DetailQrCuponActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailQrCuponBinding
    private lateinit var repository: CuponRepository
    private lateinit var userPreferences: UserPreferences

    private var idUsuarioCupon: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailQrCuponBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)
        val retrofit = RetrofitInstance.create(userPreferences)
        val cuponApi = retrofit.create(CuponApi::class.java)
        repository = CuponRepository(cuponApi)

        idUsuarioCupon = intent.getIntExtra("idUsuarioCupon", -1)
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val puntosActuales = intent.getIntExtra("puntosActuales", 0)
        val puntosRequeridos = intent.getIntExtra("puntosRequeridos", 0)
        val codigoCupon = intent.getStringExtra("codigoCupon") ?: ""
        val fechaCanje = intent.getStringExtra("fechaCanje") ?: ""
        val nomTienda = intent.getStringExtra("nombreTienda") ?: ""
        val distritoTienda = intent.getStringExtra("distritoTienda") ?: ""
        val qrBase64 = intent.getStringExtra("qrBase64")

        binding.tvDescripcionCupon.text = descripcion
        binding.tvCodigoCupon.text = codigoCupon
        binding.tvNombreTienda.text = nomTienda
        binding.tvDireccionTienda.text = distritoTienda

        if (!qrBase64.isNullOrEmpty()) {
            try {
                val qrBitmap = base64ToBitmap(qrBase64)
                binding.imgQrCupon.setImageBitmap(qrBitmap)
                binding.imgQrCupon.visibility = View.VISIBLE
                Log.d("DetailQrCupon", "QR cargado exitosamente")
            } catch (e: Exception) {
                Log.e("DetailQrCupon", "Error al decodificar QR", e)
                binding.imgQrCupon.setImageResource(R.drawable.ic_qr_placeholder)
                binding.imgQrCupon.visibility = View.VISIBLE
            }
        } else {
            Log.w("DetailQrCupon", "qrBase64 es null o vacío")
            binding.imgQrCupon.setImageResource(R.drawable.ic_qr_placeholder)
            binding.imgQrCupon.visibility = View.VISIBLE
        }

        binding.btnUsarTienda.setOnClickListener {
            mostrarDialogConfirmacionCanje(
                descripcion,
                puntosActuales,
                puntosRequeridos,
                codigoCupon,
                fechaCanje
            )
        }
    }

    private fun mostrarDialogConfirmacionCanje(
        descripcion: String,
        puntosActuales: Int,
        puntosRequeridos: Int,
        codigoCupon: String,
        fechaCanje: String
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Canje")
            .setMessage(
                """
                ¿Estás seguro de que deseas canjear este cupón en la tienda?
                
                Cupón: $descripcion
                Código: $codigoCupon
                
                Esta acción no se puede deshacer.
                """.trimIndent()
            )
            .setPositiveButton("Sí, canjear") { dialog, _ ->
                dialog.dismiss()
                realizarCanje(descripcion, codigoCupon, fechaCanje)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun realizarCanje(
        descripcion: String,
        codigoCupon: String,
        fechaCanje: String
    ) {
        if (idUsuarioCupon == -1) {
            mostrarError("Error: ID de cupón no válido")
            return
        }

        // Mostrar loading
        binding.btnUsarTienda.isEnabled = false
        binding.btnUsarTienda.text = "Canjeando..."

        lifecycleScope.launch {
            try {
                val result = repository.canjearCuponEnTienda(idUsuarioCupon)

                result.onSuccess { response ->
                    if (response.valor) {
                        finish()
                    } else {
                        mostrarError(response.mensaje ?: "No se pudo canjear el cupón")
                        binding.btnUsarTienda.isEnabled = true
                        binding.btnUsarTienda.text = "Usar en Tienda"
                    }
                }.onFailure { error ->
                    mostrarError("Error: ${error.message}")
                    binding.btnUsarTienda.isEnabled = true
                    binding.btnUsarTienda.text = "Usar en Tienda"
                }

            } catch (e: Exception) {
                Log.e("DetailQrCupon", "Error al canjear cupón", e)
                mostrarError("Error inesperado: ${e.message}")
                binding.btnUsarTienda.isEnabled = true
                binding.btnUsarTienda.text = "Usar en Tienda"
            }
        }
    }



    private fun mostrarError(mensaje: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val cleanBase64 = base64.trim()
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}