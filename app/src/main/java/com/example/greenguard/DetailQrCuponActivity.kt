package com.example.greenguard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityDetailQrCuponBinding
import com.example.greenguard.databinding.DialogConfirmarCanjeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailQrCuponActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailQrCuponBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailQrCuponBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val descripcion = intent.getStringExtra("descripcion")
        val puntosActuales = intent.getIntExtra("puntosActuales", 0)
        val puntosRequeridos = intent.getIntExtra("puntosRequeridos", 0)
        val codigoCupon = intent.getStringExtra("codigoCupon")
        val fechaCanje = intent.getStringExtra("fechaCanje")
        val nomTienda = intent.getStringExtra("nomTienda")
        val distrito = intent.getStringExtra("distrito")
        val imagen = intent.getIntExtra("imagen", R.drawable.ic_launcher_background)


        binding.tvDescripcionCupon.text = descripcion



        binding.tvCodigoCupon.text = codigoCupon

        binding.tvNombreTienda.text = nomTienda
        binding.tvDireccionTienda.text = distrito


        binding.btnUsarTienda.setOnClickListener {
            mostrarConfirmacionCanje(
                descripcion.toString(),
                puntosActuales,
                puntosRequeridos,
                codigoCupon.toString(),
                fechaCanje.toString()
            )
        }
    }


    private fun mostrarConfirmacionCanje(
        descripcion: String,
        puntosActuales: Int,
        puntosRequeridos: Int,
        codigoCupon: String,
        fechaCanje: String
    ) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_confirmar_canje, null)
        dialog.setContentView(view)

        // --- Obtener referencias correctamente ---
        val tvDescripcionCupon = view.findViewById<TextView>(R.id.tvDescripcionCupon)
        val tvPuntosActuales = view.findViewById<TextView>(R.id.tvPuntosActuales)
        val tvPuntosDescontar = view.findViewById<TextView>(R.id.tvPuntosDescontar)
        val tvSaldoFinal = view.findViewById<TextView>(R.id.tvSaldoFinal)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        // --- Setear valores ---
        tvDescripcionCupon.text = descripcion
        tvPuntosActuales.text = puntosActuales.toString()
        tvPuntosDescontar.text = "- $puntosRequeridos"
        tvSaldoFinal.text = (puntosActuales - puntosRequeridos).toString()

        // --- Botón Cancelar ---
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        // --- Botón Confirmar ---
        btnConfirmar.setOnClickListener {
            dialog.dismiss()

            // Abrir la pantalla de éxito
            val intent = Intent(this, CanjeExitosoActivity::class.java)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("codigoCupon", codigoCupon)
            intent.putExtra("fecha", fechaCanje)
            intent.putExtra("imagen", R.drawable.ic_launcher_background)
            startActivity(intent)
        }

        dialog.show()
    }
}
