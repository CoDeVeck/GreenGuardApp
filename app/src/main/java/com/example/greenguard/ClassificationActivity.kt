package com.example.greenguard

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.greenguard.data.api.ReporteApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityClassificationBinding
import com.example.greenguard.domain.model.dto.ClassificationResponse
import com.example.greenguard.util.FileUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ClassificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassificationBinding
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private lateinit var userPreferences: UserPreferences

    private val reporteService: ReporteApi by lazy {
        RetrofitInstance.createForAI(userPreferences).create(ReporteApi::class.java)
    }

    private val classificationService: ReporteApi by lazy {
        RetrofitInstance.createForAI(userPreferences).create(ReporteApi::class.java)
    }

    private var classificationData: ClassificationResponse? = null
    private var userId: Int = -1
    private var loadingHandler: Handler? = null
    private var loadingRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)

        // Cargar ID de usuario
        lifecycleScope.launch {
            userId = userPreferences.obtenerIdUsuario()
            Log.d("ClassificationActivity", "👤 ID Usuario: $userId")
        }

        // ✅ OCULTAR todos los controles hasta que termine el análisis
        hideControls()

        loadImage()
        setupListeners()

        // ✅ MOSTRAR OVERLAY INMEDIATAMENTE y empezar análisis
        showLoading(
            title = "🤖 Analizando con IA",
            subtitle = "Detectando objetos y clasificando incidente..."
        )

        // Pequeño delay para que la UI se renderice antes de procesar
        Handler(Looper.getMainLooper()).postDelayed({
            performAIAnalysis()
        }, 300)
    }

    /**
     * Oculta todos los controles de la interfaz durante el análisis
     */
    private fun hideControls() {
        binding.apply {
            // Ocultar botones de acción
            btnSubmitReport.visibility = View.GONE
            btnEditImage.visibility = View.GONE

            // Ocultar campo de descripción
            etDescription.visibility = View.GONE

            // Ocultar resultados de IA (si existen en tu layout)
            // tvTipoIncidente.visibility = View.GONE
            // tvNivelRiesgo.visibility = View.GONE
            // etc.
        }
    }

    /**
     * Muestra todos los controles después del análisis
     */
    private fun showControls() {
        binding.apply {
            btnSubmitReport.visibility = View.VISIBLE
            btnEditImage.visibility = View.VISIBLE
            etDescription.visibility = View.VISIBLE

            // Animar la aparición (opcional)
            btnSubmitReport.alpha = 0f
            btnSubmitReport.animate().alpha(1f).setDuration(300).start()
        }
    }

    private fun showLoading(title: String, subtitle: String = "") {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingTitle.text = title
        binding.tvLoadingSubtitle.text = subtitle
        binding.tvLoadingSubtitle.visibility = if (subtitle.isEmpty()) View.GONE else View.VISIBLE

        animateLoadingDots()
    }

    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE

        // Detener la animación de puntos
        loadingHandler?.removeCallbacks(loadingRunnable ?: return)
    }

    private fun animateLoadingDots() {
        val dots = arrayOf("", ".", "..", "...")
        var index = 0

        loadingHandler = Handler(Looper.getMainLooper())
        loadingRunnable = object : Runnable {
            override fun run() {
                if (binding.loadingOverlay.visibility == View.VISIBLE) {
                    binding.tvLoadingDots.text = dots[index]
                    index = (index + 1) % dots.size
                    loadingHandler?.postDelayed(this, 400)
                }
            }
        }
        loadingHandler?.post(loadingRunnable!!)
    }

    private fun loadImage() {
        val uriString = intent.getStringExtra("imageUri")
        if (uriString != null) {
            if (uriString.startsWith("/")) {
                imageFile = File(uriString)
                binding.ivCapturedImage.setImageURI(Uri.fromFile(imageFile))
            } else {
                imageUri = Uri.parse(uriString)
                binding.ivCapturedImage.setImageURI(imageUri)
                imageFile = FileUtil.uriToFile(this, imageUri!!)
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnEditImage.setOnClickListener {
            Toast.makeText(this, "Editar imagen", Toast.LENGTH_SHORT).show()
        }
        binding.btnSubmitReport.setOnClickListener { submitReport() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun performAIAnalysis() {
        if (imageFile == null || !imageFile!!.exists()) {
            hideLoading()
            Toast.makeText(this, "No se encontró la imagen", Toast.LENGTH_SHORT).show()
            showControls() // Mostrar controles aunque haya error
            return
        }

        lifecycleScope.launch {
            try {
                Log.d("ClassificationActivity", "📤 Enviando imagen (${imageFile!!.length() / 1024}KB) a IA...")

                val requestFile = imageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "file",
                    imageFile!!.name,
                    requestFile
                )

                val response = classificationService.clasificarImagen(imagePart)

                // ✅ Ocultar loading y mostrar controles
                hideLoading()
                showControls()

                if (response.isSuccessful && response.body() != null) {
                    classificationData = response.body()!!
                    Log.d("ClassificationActivity", "✅ Clasificación recibida: $classificationData")
                    displayAIResults(classificationData!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ClassificationActivity", "❌ Error ${response.code()}: $errorBody")
                    Toast.makeText(
                        this@ClassificationActivity,
                        "Error al clasificar: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: java.net.SocketTimeoutException) {
                hideLoading()
                showControls()
                Log.e("ClassificationActivity", "⏱️ Timeout", e)
                Toast.makeText(
                    this@ClassificationActivity,
                    "La clasificación tardó demasiado. Intenta con una imagen más pequeña.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                hideLoading()
                showControls()
                Log.e("ClassificationActivity", "❌ Error: ${e.message}", e)
                Toast.makeText(
                    this@ClassificationActivity,
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun displayAIResults(data: ClassificationResponse) {
        Log.d("ClassificationActivity", "📊 Mostrando resultados en UI...")

        binding.tvTipoIncidente.text = data.tipoIncidente
        binding.tvNivelRiesgo.text = data.nivelRiesgo
        binding.tvNivelRiesgo.setTextColor(getRiskColor(data.nivelRiesgo))

        val confianzaPorcentaje = (data.confianza * 100).toInt()
        binding.tvConfianza.text = "$confianzaPorcentaje%"

        binding.tvDescripcionIA.text = data.descripcionIA
        Log.d("ClassificationActivity", "descripcionIA: ${data.descripcionIA}")
        binding.tvEstimatedPoints.text = "${data.puntosEstimados} Puntos"

        // ✅ Animación de aparición de resultados
        binding.tvTipoIncidente.alpha = 0f
        binding.tvTipoIncidente.animate().alpha(1f).setDuration(400).start()

        Toast.makeText(this, "¡Clasificación completada! ✨", Toast.LENGTH_SHORT).show()
    }

    private fun getRiskColor(nivelRiesgo: String): Int {
        return when (nivelRiesgo.lowercase()) {
            "bajo" -> Color.parseColor("#4CAF50")
            "medio" -> Color.parseColor("#FF9800")
            "alto" -> Color.parseColor("#F44336")
            else -> Color.parseColor("#666666")
        }
    }

    private fun submitReport() {
        val description = binding.etDescription.text.toString()

        if (description.isEmpty()) {
            Toast.makeText(this, "Agrega una descripción", Toast.LENGTH_SHORT).show()
            return
        }

        if (classificationData == null) {
            Toast.makeText(this, "Esperando clasificación de IA...", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId <= 0) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(
            title = "📤 Enviando reporte",
            subtitle = "Guardando tu reporte en el servidor..."
        )

        lifecycleScope.launch {
            try {
                Log.d(
                    "ClassificationActivity",
                    "📝 Creando reporte CON clasificación ya obtenida..."
                )

                val requestFile = imageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "imagen",
                    imageFile!!.name,
                    requestFile
                )

                // Descripción completa (usuario + IA)
                val descripcionCompleta = buildString {
                    append(description)
                    append("\n\n[IA] ")
                    append(classificationData!!.descripcionIA)
                }

                // ✅ ENVIAR DATOS DE CLASIFICACIÓN YA OBTENIDOS
                val response = reporteService.crearReporteConClasificacion(
                    idUsu = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    detalleRepo = descripcionCompleta.toRequestBody("text/plain".toMediaTypeOrNull()),
                    latitud = "-12.046374".toRequestBody("text/plain".toMediaTypeOrNull()),
                    longitud = "-77.042793".toRequestBody("text/plain".toMediaTypeOrNull()),
                    idDistrito = "1".toRequestBody("text/plain".toMediaTypeOrNull()),
                    idTipoIncidente = classificationData!!.idTipoIncidente.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    idClasificacion = classificationData!!.idClasificacion.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    descripcionIA = classificationData!!.descripcionIA
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    puntosEstimados = classificationData!!.puntosEstimados.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull()),
                    imagen = imagePart
                )

                hideLoading()

                if (response.isSuccessful && response.body() != null) {
                    val reporteCreado = response.body()!!
                    Log.d("ClassificationActivity", "✅ Reporte creado: ${reporteCreado.numReport}")

                    val intent = Intent(
                        this@ClassificationActivity,
                        ReportConfirmationActivity::class.java
                    ).apply {
                        putExtra("points", reporteCreado.puntosGanados)
                        putExtra("riskLevel", reporteCreado.nivelRiesgo)
                        putExtra("reportNumber", reporteCreado.numReport)
                        putExtra("tipoIncidente", reporteCreado.tipoIncidente)
                    }

                    startActivity(intent)
                    finish()

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ClassificationActivity", "❌ Error: ${response.code()} - $errorBody")
                    Toast.makeText(
                        this@ClassificationActivity,
                        "Error al crear reporte: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                hideLoading()
                Log.e("ClassificationActivity", "❌ Error: ${e.message}", e)
                Toast.makeText(
                    this@ClassificationActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar handlers
        loadingHandler?.removeCallbacks(loadingRunnable ?: return)
    }
}