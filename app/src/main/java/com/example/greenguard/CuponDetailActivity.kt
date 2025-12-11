package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityCuponDetailBinding
import com.example.greenguard.domain.model.entities.UsuarioCupon
import com.google.android.material.bottomsheet.BottomSheetDialog

class CuponDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuponDetailBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuponDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreCupon = intent.getStringExtra("nombreCupon")
        val descCupon = intent.getStringExtra("descCupon")
        val puntosRequeridos = intent.getIntExtra("puntosRequeridos", 0)

        val nomTienda = intent.getStringExtra("nomTienda")
        val distrito = intent.getStringExtra("distrito")

        val codigoCupon = intent.getStringExtra("codigoCupon")
        val qrCode = intent.getStringExtra("qrCode")
        val fechaCanje = intent.getStringExtra("fechaCanje")
        val puntosUsuario = intent.getIntExtra("puntosUsuario", 0)




        // Imagen del cupón
        binding.imgCupon.setImageResource(R.drawable.ic_launcher_background)

        // Info básica
        binding.tvTituloCupon.text = nombreCupon
        binding.tvDescripcionCupon.text = descCupon

        // Costos
        binding.tvCosto.text = "Costo: ${puntosRequeridos?: 0} Puntos"
        binding.tvTusPuntos.text = "Tus puntos: ${puntosUsuario ?: 0}"

        val progreso = (puntosUsuario * 100) / (puntosRequeridos.takeIf { it > 0 } ?: 1)
        binding.progresoPuntos.progress = progreso


        // Tienda y vigencia
        binding.tvTienda.text = "Tienda asociada: ${nomTienda ?: "N/A"}"
        //binding.tvVigencia.text = "Vigencia: ${cupon?.duracionDias ?: "N/A"} días"

        // Términos
        //binding.tvTerminos.text = cupon?.terminos ?: "Sin términos."

        // Botón de canje
        binding.btnCanjear.text = "Canjear por ${puntosRequeridos} puntos"

        binding.btnCanjear.setOnClickListener {
            val intent = Intent(this, DetailQrCuponActivity::class.java)

            intent.putExtra("descripcion", descCupon ?: "")
            intent.putExtra("puntosActuales", puntosUsuario)
            intent.putExtra("puntosRequeridos", puntosRequeridos)
            intent.putExtra("codigoCupon", codigoCupon ?: "")
            intent.putExtra("fechaCanje", fechaCanje ?: "")
            intent.putExtra("nomTienda", nomTienda ?: "")
            intent.putExtra("distrito", distrito ?: "")
            intent.putExtra("imagen", R.drawable.ic_launcher_background)

            startActivity(intent)
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

        val tvDescripcionCupon = view.findViewById<TextView>(R.id.tvDescripcionCupon)
        val tvPuntosActuales = view.findViewById<TextView>(R.id.tvPuntosActuales)
        val tvPuntosDescontar = view.findViewById<TextView>(R.id.tvPuntosDescontar)
        val tvSaldoFinal = view.findViewById<TextView>(R.id.tvSaldoFinal)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        tvDescripcionCupon.text = descripcion
        tvPuntosActuales.text = puntosActuales.toString()
        tvPuntosDescontar.text = "- $puntosRequeridos"
        tvSaldoFinal.text = (puntosActuales - puntosRequeridos).toString()

        btnCancelar.setOnClickListener { dialog.dismiss() }

        btnConfirmar.setOnClickListener {
            dialog.dismiss()

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
