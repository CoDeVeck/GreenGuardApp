package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.greenguard.data.api.CategoriaApi
import com.example.greenguard.data.api.CuponApi
import com.example.greenguard.data.api.ReporteApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.data.repository.CuponRepository
import com.example.greenguard.databinding.ActivityCatalogoCuponesBinding
import com.example.greenguard.databinding.ItemCuponCatalogoBinding
import com.example.greenguard.domain.model.dto.CuponCatalogoDTO
import com.example.greenguard.domain.model.entities.Categoria
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CatalogoCuponesActivity : AppCompatActivity() {

    private var puntosDisponibles = 0
    private var categoriaSeleccionada: Int? = null
    private var cuponesList = mutableListOf<CuponCatalogoDTO>()
    private var categoriasList = mutableListOf<Categoria>()
    private var botonesCategoria = mutableListOf<Button>()

    private lateinit var binding: ActivityCatalogoCuponesBinding
    private lateinit var repository: CuponRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var reporteApi: ReporteApi
    private lateinit var categoriaApi: CategoriaApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoCuponesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        val retrofit = RetrofitInstance.create(userPreferences)
        val cuponApi = retrofit.create(CuponApi::class.java)
        reporteApi = retrofit.create(ReporteApi::class.java)
        categoriaApi = retrofit.create(CategoriaApi::class.java)

        repository = CuponRepository(cuponApi)

        setupListeners()
        cargarCategorias()
        cargarPuntosYCatalogo()
    }

    private fun setupListeners() {
        binding.btnMisCupones.setOnClickListener {
            startActivity(Intent(this, MisCuponesActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val response = categoriaApi.listCategorias()

                if (response.isSuccessful && response.body() != null) {
                    categoriasList = response.body()!!.toMutableList()
                    crearBotonesCategoria()
                } else {
                    Toast.makeText(
                        this@CatalogoCuponesActivity,
                        "Error al cargar categorías",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("Catalogo", "Error cargando categorías", e)
                // Mostrar solo botón "Todos" si falla
                crearBotonTodos()
            }
        }
    }

    private fun crearBotonesCategoria() {
        binding.categoriasContainer.removeAllViews()
        botonesCategoria.clear()

        // Botón "Todos"
        val btnTodos = crearBotonCategoria("Todos", null, true)
        binding.categoriasContainer.addView(btnTodos)
        botonesCategoria.add(btnTodos)

        // Botones de categorías dinámicas
        categoriasList.forEach { categoria ->
            val btn = crearBotonCategoria(
                categoria.descCate.toString(),
                categoria.idCate,
                false
            )
            binding.categoriasContainer.addView(btn)
            botonesCategoria.add(btn)
        }
    }

    private fun crearBotonCategoria(
        nombre: String,
        idCategoria: Int?,
        seleccionado: Boolean
    ): Button {
        val button = Button(this).apply {
            text = nombre
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                resources.getDimensionPixelSize(R.dimen.category_button_height) // 40dp
            ).apply {
                marginEnd = resources.getDimensionPixelSize(R.dimen.category_button_margin) // 8dp
            }

            setPadding(
                resources.getDimensionPixelSize(R.dimen.category_button_padding), // 16dp
                0,
                resources.getDimensionPixelSize(R.dimen.category_button_padding),
                0
            )

            setBackgroundResource(
                if (seleccionado) R.drawable.btn_category_selected
                else R.drawable.btn_category_normal
            )

            setTextColor(
                if (seleccionado) getColor(android.R.color.white)
                else getColor(R.color.category_text_normal) // #666666
            )

            setOnClickListener {
                selectCategory(idCategoria, this)
            }
        }

        return button
    }

    private fun crearBotonTodos() {
        binding.categoriasContainer.removeAllViews()
        botonesCategoria.clear()

        val btnTodos = crearBotonCategoria("Todos", null, true)
        binding.categoriasContainer.addView(btnTodos)
        botonesCategoria.add(btnTodos)
    }

    private fun cargarPuntosYCatalogo() {
        lifecycleScope.launch {
            try {
                val idUsuario = userPreferences.obtenerIdUsuario()
                if (idUsuario == -1) return@launch

                puntosDisponibles = reporteApi.obtenerPuntos(idUsuario)
                binding.tvPuntos.text = formatPuntos(puntosDisponibles)

                cargarCatalogo()

            } catch (e: Exception) {
                Log.e("Catalogo", "Error cargando puntos o catálogo", e)
            }
        }
    }

    private fun cargarCatalogo() {
        lifecycleScope.launch {
            try {
                val result = repository.obtenerCatalogo(
                    activo = true,
                    categoria = categoriaSeleccionada
                )

                result.onSuccess { cupones ->
                    cuponesList = cupones.toMutableList()
                    mostrarCupones(cupones)
                }.onFailure { error ->
                    Toast.makeText(
                        this@CatalogoCuponesActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("CatalogoCupones", "Error al obtener cupones", error)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CatalogoCuponesActivity,
                    "Error al cargar cupones: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarCupones(cupones: List<CuponCatalogoDTO>) {
        binding.cuponesGrid.removeAllViews()

        if (cupones.isEmpty()) {
            Toast.makeText(this, "No hay cupones disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        cupones.forEach { cupon ->
            val itemBinding = ItemCuponCatalogoBinding.inflate(
                LayoutInflater.from(this),
                binding.cuponesGrid,
                false
            )

            configurarItemCupon(itemBinding, cupon)
            binding.cuponesGrid.addView(itemBinding.root)
        }
    }

    private fun configurarItemCupon(
        itemBinding: ItemCuponCatalogoBinding,
        cupon: CuponCatalogoDTO
    ) {
        with(itemBinding) {
            tvNombreCupon.text = cupon.nombreCupon
            tvPuntos.text = "${cupon.puntosRequeridos} Puntos"

            if (!cupon.descCupon.isNullOrEmpty()) {
                tvDescripcion.text = cupon.descCupon
                tvDescripcion.visibility = View.VISIBLE
            }

            val tienePuntos = puntosDisponibles >= cupon.puntosRequeridos
            val ahora = LocalDateTime.now()
            val fechaVencimiento = LocalDateTime.parse(cupon.fechaVencimiento)
            val estaVencido = fechaVencimiento.isBefore(ahora)

            if (!tienePuntos) {
                cardCupon.alpha = 0.5f
                layoutContent.setBackgroundColor(getColor(android.R.color.darker_gray))
                tvNombreCupon.text = "Te faltan ${cupon.puntosRequeridos - puntosDisponibles} puntos"
                tvNombreCupon.setTextColor(getColor(android.R.color.darker_gray))
                tvPuntos.setTextColor(getColor(android.R.color.darker_gray))
                btnVerDetalles.isEnabled = false
                btnVerDetalles.text = "Ver detalles"
                btnVerDetalles.setTextColor(getColor(android.R.color.darker_gray))
                btnVerDetalles.setBackgroundResource(R.drawable.btn_disabled)
            } else if (estaVencido) {
                cardCupon.alpha = 0.5f
                layoutContent.setBackgroundColor(getColor(android.R.color.darker_gray))
                btnVerDetalles.isEnabled = false
                btnVerDetalles.text = "Cupón vencido"
                btnVerDetalles.setTextColor(getColor(android.R.color.darker_gray))
                btnVerDetalles.setBackgroundResource(R.drawable.btn_disabled)
            } else {
                cardCupon.alpha = 1.0f

                val backgroundColor = when {
                    cupon.categoria.contains("ABARROTES", ignoreCase = true) -> "#E8F5E9"
                    cupon.categoria.contains("COMIDAS", ignoreCase = true) -> "#FFF3E0"
                    cupon.categoria.contains("ROPAS", ignoreCase = true) -> "#F3E5F5"
                    cupon.categoria.contains("SALUD", ignoreCase = true) -> "#E1F5FE"
                    cupon.categoria.contains("ELECTRODOMÉSTICOS", ignoreCase = true) -> "#FFF9C4"
                    cupon.categoria.contains("DEPORTES", ignoreCase = true) -> "#E8EAF6"
                    else -> "#E8F5E9"
                }

                try {
                    layoutContent.setBackgroundColor(android.graphics.Color.parseColor(backgroundColor))
                } catch (e: Exception) {
                    layoutContent.setBackgroundColor(getColor(android.R.color.white))
                }

                btnVerDetalles.isEnabled = true
                btnVerDetalles.text = "Ver detalles"
                btnVerDetalles.setTextColor(getColor(android.R.color.white))
                btnVerDetalles.setBackgroundResource(R.drawable.btn_orange)
            }

            btnVerDetalles.setOnClickListener {
                if (tienePuntos && !estaVencido) {
                    abrirDetalleCupon(cupon)
                }
            }

            cardCupon.setOnClickListener {
                if (tienePuntos && !estaVencido) {
                    abrirDetalleCupon(cupon)
                }
            }
        }
    }

    private fun abrirDetalleCupon(cupon: CuponCatalogoDTO) {
        val intent = Intent(this, CuponDetailActivity::class.java).apply {
            putExtra("idCupon", cupon.idCupon)
            putExtra("nombreCupon", cupon.nombreCupon)
            putExtra("descCupon", cupon.descCupon)
            putExtra("puntosRequeridos", cupon.puntosRequeridos)
            putExtra("nomTienda", cupon.tienda)
            putExtra("puntosUsuario", puntosDisponibles)
        }
        startActivity(intent)
    }

    private fun selectCategory(categoria: Int?, buttonClicked: Button) {
        categoriaSeleccionada = categoria

        // Resetear todos los botones
        botonesCategoria.forEach { btn ->
            btn.setBackgroundResource(R.drawable.btn_category_normal)
            btn.setTextColor(getColor(R.color.category_text_normal))
        }

        // Activar el botón seleccionado
        buttonClicked.setBackgroundResource(R.drawable.btn_category_selected)
        buttonClicked.setTextColor(getColor(android.R.color.white))

        // Recargar cupones con filtro
        cargarCatalogo()
    }

    private fun formatPuntos(puntos: Int): String {
        return String.format("%,d", puntos)
    }

    override fun onResume() {
        super.onResume()
        cargarPuntosYCatalogo()
    }
}