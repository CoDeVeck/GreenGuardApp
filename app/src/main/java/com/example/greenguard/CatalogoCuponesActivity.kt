package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityCatalogoCuponesBinding
import com.example.greenguard.domain.model.entities.Categoria
import com.example.greenguard.domain.model.entities.Cupon

class CatalogoCuponesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoCuponesBinding
    private var puntosDisponibles = 1250
    private var categoriaSeleccionada = "todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoCuponesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        loadCupones()
    }

    private fun setupUI() {
        // Mostrar puntos
        binding.tvPuntos.text = formatPuntos(puntosDisponibles)
    }

    private fun setupListeners() {
        // Navegar a Mis Cupones
        binding.btnMisCupones.setOnClickListener {
            val intent = Intent(this, MisCuponesActivity::class.java)
            startActivity(intent)
        }

        // Botón volver
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Botones de categoría
        binding.btnTodos.setOnClickListener {
            selectCategory("todos", binding.btnTodos)
        }

        binding.btnAlimentacion.setOnClickListener {
            selectCategory("alimentacion", binding.btnAlimentacion)
        }

        binding.btnTransporte.setOnClickListener {
            selectCategory("transporte", binding.btnTransporte)
        }
    }

    private fun selectCategory(categoria: String, buttonClicked: View) {
        categoriaSeleccionada = categoria

        // Resetear todos los botones
        resetCategoryButtons()

        // Activar el botón seleccionado
        buttonClicked.setBackgroundResource(R.drawable.btn_category_selected)

        // Filtrar cupones
        filterCupones(categoria)
    }

    private fun resetCategoryButtons() {
        binding.btnTodos.setBackgroundResource(R.drawable.btn_category_normal)
        binding.btnAlimentacion.setBackgroundResource(R.drawable.btn_category_normal)
        binding.btnTransporte.setBackgroundResource(R.drawable.btn_category_normal)
    }

    private fun loadCupones() {
        // Los cupones están en el XML de forma estática
        // Aquí puedes implementar lógica para mostrar/ocultar según disponibilidad
        val cupones = getCuponesFicticios()

        // Por ahora, simplemente mostramos todos
        Toast.makeText(this, "Cargados ${cupones.size} cupones", Toast.LENGTH_SHORT).show()
    }

    private fun filterCupones(categoria: String) {
        Toast.makeText(this, "Filtrando por: $categoria", Toast.LENGTH_SHORT).show()
        // Implementar lógica de filtrado si los cupones se cargan dinámicamente
    }

    private fun formatPuntos(puntos: Int): String {
        return String.format("%,d", puntos).replace(",", ",")
    }

    // Modelo de datos ficticios
    private fun getCuponesFicticios(): List<Cupon> {
        return listOf(
            Cupon(
                idCupon = 1,
                nombreCupon = "Café del Día Gratis",
                descCupon = "Disfruta de un café gratis",
                puntosRequeridos = 500,
                categoria = Categoria(descCate = "Alimentacion"),
                activo = true,
            ),
            Cupon(
                idCupon = 2,
                nombreCupon = "Descuento en Ropa",
                descCupon = "15% de descuento",
                puntosRequeridos = 800,
                categoria = Categoria(descCate = "Moda"),
                activo = true,

            ),
            Cupon(
                idCupon = 3,
                nombreCupon = "Tecnología",
                descCupon = "Te faltan 250 puntos",
                puntosRequeridos = 1500,
                categoria = Categoria(descCate = "Tecnologia"),
                activo = false,

            ),
            Cupon(
                idCupon = 4,
                nombreCupon = "Viaje",
                descCupon = "Te faltan 750 puntos",
                puntosRequeridos = 2000,
                categoria = Categoria(descCate = "Transporte"),
                activo = false,

            )
        )
    }
}