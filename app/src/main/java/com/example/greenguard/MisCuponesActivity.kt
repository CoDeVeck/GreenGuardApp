package com.example.greenguard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityMisCuponesBinding
import com.example.greenguard.databinding.ItemMiCuponBinding
import com.example.greenguard.domain.model.entities.Categoria
import com.example.greenguard.domain.model.entities.Cupon
import com.example.greenguard.domain.model.entities.Tienda
import com.example.greenguard.domain.model.entities.Usuario
import com.example.greenguard.domain.model.entities.UsuarioCupon
import com.google.android.material.tabs.TabLayout
class MisCuponesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisCuponesBinding

    private var cuponesActivos = mutableListOf<UsuarioCupon>()
    private var cuponesUsados = mutableListOf<UsuarioCupon>()
    private var cuponesVencidos = mutableListOf<UsuarioCupon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCuponesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarDatosFicticios()
        setupListeners()
        showCuponesActivos()
    }

    private fun setupListeners() {
        // Botón volver
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Botón agregar cupón
        binding.btnAdd.setOnClickListener {
            Toast.makeText(this, "Función agregar cupón", Toast.LENGTH_SHORT).show()
        }

        // Tabs para cambiar entre categorías
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> showCuponesActivos()
                    1 -> showCuponesUsados()
                    2 -> showCuponesVencidos()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun cargarDatosFicticios() {
        // === CUPONES ACTIVOS ===
        cuponesActivos = mutableListOf(
            UsuarioCupon(
                idUsuarioCupon = 1,
                cupon = Cupon(
                    idCupon = 1,
                    nombreCupon = "Café Sostenible Gratis",
                    descCupon = "Recompensa por reporte verificado",
                    categoria = Categoria(1, "Alimentación"),
                    codCupon = "CAFE2024",
                    puntosRequeridos = 500,
                    tienda = Tienda(1, "EcoCafé" ),
                    fechaCreacion = "2024-11-15",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "XY98-AB12-CD34",
                qrVerificationCode = "QR123456789",
                fechaCanje = "2024-12-01",
                canjeado = false,
                fechaUso = null,
                estado = "ACTIVO"
            ),
            UsuarioCupon(
                idUsuarioCupon = 2,
                cupon = Cupon(
                    idCupon = 2,
                    nombreCupon = "20% de descuento en tienda",
                    descCupon = "Obtenido por 3 reportes verificados",
                    categoria = Categoria(2, "Compras"),
                    codCupon = "DESC20",
                    puntosRequeridos = 800,
                    tienda = Tienda(2, "TiendaVerde"),
                    fechaCreacion = "2024-11-20",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "A812-EF56-GH78",
                qrVerificationCode = "QR987654321",
                fechaCanje = "2024-12-03",
                canjeado = false,
                fechaUso = null,
                estado = "ACTIVO"
            ),
            UsuarioCupon(
                idUsuarioCupon = 3,
                cupon = Cupon(
                    idCupon = 3,
                    nombreCupon = "10% en tu próxima compra",
                    descCupon = "Recompensa de inicio",
                    categoria = Categoria(2, "Compras"),
                    codCupon = "INICIO10",
                    puntosRequeridos = 300,
                    tienda = Tienda(3, "SuperMercado Eco"),
                    fechaCreacion = "2024-12-01",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "ZW76-IJ90-KL12",
                qrVerificationCode = "QR456789123",
                fechaCanje = "2024-12-05",
                canjeado = false,
                fechaUso = null,
                estado = "VENCE_PRONTO"
            ),
            UsuarioCupon(
                idUsuarioCupon = 4,
                cupon = Cupon(
                    idCupon = 4,
                    nombreCupon = "Transporte gratis",
                    descCupon = "Por acumular 5 reportes",
                    categoria = Categoria(3, "Transporte"),
                    codCupon = "TRANS24",
                    puntosRequeridos = 1000,
                    tienda = Tienda(4, "EcoTransport"),
                    fechaCreacion = "2024-11-25",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "TR45-MN67-OP89",
                qrVerificationCode = "QR789123456",
                fechaCanje = "2024-12-04",
                canjeado = false,
                fechaUso = null,
                estado = "ACTIVO"
            )
        )

        // === CUPONES USADOS ===
        cuponesUsados = mutableListOf(
            UsuarioCupon(
                idUsuarioCupon = 5,
                cupon = Cupon(
                    idCupon = 5,
                    nombreCupon = "Descuento en restaurante",
                    descCupon = "15% de descuento",
                    categoria = Categoria(1, "Alimentación"),
                    codCupon = "REST15",
                    puntosRequeridos = 600,
                    tienda = Tienda(5, "Restaurante Verde"),
                    fechaCreacion = "2024-10-01",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "AB12-MN34-OP56",
                qrVerificationCode = "QR111222333",
                fechaCanje = "2024-10-10",
                canjeado = true,
                fechaUso = "2024-10-15",
                estado = "USADO"
            ),
            UsuarioCupon(
                idUsuarioCupon = 6,
                cupon = Cupon(
                    idCupon = 6,
                    nombreCupon = "Envío gratis",
                    descCupon = "Envío sin costo",
                    categoria = Categoria(2, "Compras"),
                    codCupon = "ENVIO0",
                    puntosRequeridos = 400,
                    tienda = Tienda(6, "Tienda Online"),
                    fechaCreacion = "2024-10-05",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "CD34-QR78-ST90",
                qrVerificationCode = "QR444555666",
                fechaCanje = "2024-10-08",
                canjeado = true,
                fechaUso = "2024-10-10",
                estado = "USADO"
            ),
            UsuarioCupon(
                idUsuarioCupon = 7,
                cupon = Cupon(
                    idCupon = 7,
                    nombreCupon = "Combo hamburguesa",
                    descCupon = "Hamburguesa + bebida",
                    categoria = Categoria(1, "Alimentación"),
                    codCupon = "COMBO1",
                    puntosRequeridos = 700,
                    tienda = Tienda(7, "BurgerEco"),
                    fechaCreacion = "2024-11-01",
                    activo = true
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "HB99-XY12-ZA34",
                qrVerificationCode = "QR777888999",
                fechaCanje = "2024-11-03",
                canjeado = true,
                fechaUso = "2024-11-05",
                estado = "USADO"
            )
        )

        // === CUPONES VENCIDOS ===
        cuponesVencidos = mutableListOf(
            UsuarioCupon(
                idUsuarioCupon = 8,
                cupon = Cupon(
                    idCupon = 8,
                    nombreCupon = "Cupón de bienvenida",
                    descCupon = "Regalo de registro",
                    categoria = Categoria(4, "Promociones"),
                    codCupon = "BIENVEN",
                    puntosRequeridos = 0,
                    tienda = Tienda(8, "Varios"),
                    fechaCreacion = "2024-08-01",
                    activo = false
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "EF56-UV12-WX34",
                qrVerificationCode = "QR000111222",
                fechaCanje = "2024-08-15",
                canjeado = false,
                fechaUso = null,
                estado = "VENCIDO"
            ),
            UsuarioCupon(
                idUsuarioCupon = 9,
                cupon = Cupon(
                    idCupon = 9,
                    nombreCupon = "Descuento especial",
                    descCupon = "25% descuento único",
                    categoria = Categoria(2, "Compras"),
                    codCupon = "ESP25",
                    puntosRequeridos = 900,
                    tienda = Tienda(9, "Mega Store"),
                    fechaCreacion = "2024-08-10",
                    activo = false
                ),
                usuario = Usuario(1, "Juan Pérez", "juan@email.com"),
                codigoCupon = "GH78-YZ90-AB12",
                qrVerificationCode = "QR333444555",
                fechaCanje = "2024-08-18",
                canjeado = false,
                fechaUso = null,
                estado = "VENCIDO"
            )
        )
    }

    private fun showCuponesActivos() {
        mostrarCupones(cuponesActivos)
    }

    private fun showCuponesUsados() {
        mostrarCupones(cuponesUsados)
    }

    private fun showCuponesVencidos() {
        mostrarCupones(cuponesVencidos)
    }

    private fun mostrarCupones(usuarioCupones: List<UsuarioCupon>) {
        // Limpiar el contenedor antes de agregar nuevos cupones
        binding.cuponesListContainer.removeAllViews()

        // Si no hay cupones, mostrar mensaje
        if (usuarioCupones.isEmpty()) {
            mostrarMensajeVacio()
            return
        }

        // Inflar y agregar cada cupón dinámicamente
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


    private fun configurarCupon(itemBinding: ItemMiCuponBinding, usuarioCupon: UsuarioCupon) {
        with(itemBinding) {
            // Obtener datos del cupón
            val cupon = usuarioCupon.cupon
            val colorAvatar = obtenerColorPorEstado(usuarioCupon.estado)

            // Configurar color del avatar
            try {
                cuponAvatar.setCardBackgroundColor(Color.parseColor(colorAvatar))
            } catch (e: Exception) {
                cuponAvatar.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            }

            // Configurar textos con binding
            tvCuponTitulo.text = cupon?.nombreCupon ?: "Sin nombre"
            tvCuponDescripcion.text = cupon?.descCupon ?: "Sin descripción"
            tvCuponEstado.text = obtenerTextoEstado(usuarioCupon)
            tvCuponEstado.setTextColor(obtenerColorEstado(usuarioCupon.estado))

            // Formatear fecha de vencimiento
            val fechaVencimiento = calcularFechaVencimiento(usuarioCupon.fechaCanje)
            tvCuponFecha.text = if (usuarioCupon.estado == "USADO") {
                "Usado el: ${usuarioCupon.fechaUso ?: "N/A"}"
            } else {
                "Vence el: $fechaVencimiento"
            }

            tvCuponCodigo.text = formatearCodigoOculto(usuarioCupon.codigoCupon)

            // Configurar botón "Mostrar"
            btnMostrar.setOnClickListener {
                mostrarDialogoCodigo(usuarioCupon)
            }

            // Deshabilitar botón según estado
            when (usuarioCupon.estado) {
                "USADO", "VENCIDO" -> {
                    btnMostrar.isEnabled = false
                    btnMostrar.alpha = 0.5f
                    btnMostrar.text = "No disponible"
                }
                else -> {
                    btnMostrar.isEnabled = true
                    btnMostrar.alpha = 1.0f
                    btnMostrar.text = "Mostrar"
                }
            }
        }
    }

    private fun obtenerTextoEstado(usuarioCupon: UsuarioCupon): String {
        return when(usuarioCupon.estado) {
            "ACTIVO" -> "Activo"
            "VENCE_PRONTO" -> "Vence pronto"
            "USADO" -> "Usado"
            "VENCIDO" -> "Vencido"
            else -> usuarioCupon.estado ?: "Desconocido"
        }
    }

    private fun obtenerColorPorEstado(estado: String?): String {
        return when(estado) {
            "ACTIVO" -> "#A5D6A7"
            "VENCE_PRONTO" -> "#FFB74D"
            "USADO" -> "#90CAF9"
            "VENCIDO" -> "#BDBDBD"
            else -> "#4CAF50"
        }
    }

    private fun obtenerColorEstado(estado: String?): Int {
        return when(estado) {
            "ACTIVO" -> ContextCompat.getColor(this, android.R.color.holo_green_dark)
            "VENCE_PRONTO" -> ContextCompat.getColor(this, android.R.color.holo_orange_dark)
            "USADO", "VENCIDO" -> ContextCompat.getColor(this, android.R.color.darker_gray)
            else -> ContextCompat.getColor(this, android.R.color.darker_gray)
        }
    }

    private fun formatearCodigoOculto(codigo: String?): String {
        if (codigo.isNullOrEmpty()) return "•••• - •••• - ••••"
        val partes = codigo.split("-")
        return if (partes.size >= 3) {
            "${partes[0]} - •••• - ••••"
        } else {
            codigo.take(4) + " - •••• - ••••"
        }
    }

    private fun calcularFechaVencimiento(fechaCanje: String?): String {
        // En un caso real, calcularías la fecha de vencimiento
        // Por ahora retornamos una fecha ficticia
        return "31/01/2025"
    }

    private fun mostrarDialogoCodigo(usuarioCupon: UsuarioCupon) {
        val cupon = usuarioCupon.cupon
        val tienda = cupon?.tienda

        val intent = Intent(this, CuponDetailActivity::class.java)

        // CUPÓN
        intent.putExtra("nombreCupon", cupon?.nombreCupon)
        intent.putExtra("descCupon", cupon?.descCupon)
        intent.putExtra("puntosRequeridos", cupon?.puntosRequeridos ?: 0)

        // TIENDA
        intent.putExtra("nomTienda", tienda?.nomTienda)
        //intent.putExtra("distrito", tienda?.distrito)

        // USUARIO CUPÓN
        intent.putExtra("codigoCupon", usuarioCupon.codigoCupon)
        intent.putExtra("qrCode", usuarioCupon.qrVerificationCode)
        intent.putExtra("fechaCanje", usuarioCupon.fechaCanje)
        intent.putExtra("puntosUsuario", usuarioCupon.puntosUsuario)

        startActivity(intent)
    }


    private fun copiarAlPortapapeles(texto: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Código de cupón", texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "✅ Código copiado: $texto", Toast.LENGTH_SHORT).show()
    }
}
