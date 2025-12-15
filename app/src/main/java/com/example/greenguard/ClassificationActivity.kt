package com.example.greenguard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.greenguard.data.api.ReporteApi
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityClassificationBinding
import com.example.greenguard.domain.model.dto.ClassificationResponse
import com.example.greenguard.util.FileUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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

    // Variables de ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = -12.046374  // Default Lima
    private var longitude: Double = -77.042793 // Default Lima
    private var locationObtained: Boolean = false

    // Launcher para permisos de ubicación
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d("ClassificationActivity", "📍 Permisos de ubicación concedidos")
            getCurrentLocation()
        } else {
            Log.w("ClassificationActivity", "⚠️ Permisos de ubicación denegados, usando ubicación por defecto")
            Toast.makeText(
                this,
                "Se usará ubicación por defecto. Activa los permisos para mayor precisión.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Cargar ID de usuario
        lifecycleScope.launch {
            userId = userPreferences.obtenerIdUsuario()
            Log.d("ClassificationActivity", "👤 ID Usuario: $userId")
        }

        hideControls()
        loadImage()
        setupListeners()

        showLoading(
            title = "🤖 Analizando con IA",
            subtitle = "Detectando objetos y clasificando incidente..."
        )

        Handler(Looper.getMainLooper()).postDelayed({
            performAIAnalysis()
        }, 300)
    }

    private fun hideControls() {
        binding.apply {
            btnSubmitReport.visibility = View.GONE
            btnEditImage.visibility = View.GONE
            etDescription.visibility = View.GONE
        }
    }

    private fun showControls() {
        binding.apply {
            btnSubmitReport.visibility = View.VISIBLE
            btnEditImage.visibility = View.VISIBLE
            etDescription.visibility = View.VISIBLE

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
            showControls()
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

                hideLoading()

                if (response.isSuccessful && response.body() != null) {
                    classificationData = response.body()!!
                    Log.d("ClassificationActivity", "✅ Clasificación recibida: $classificationData")
                    displayAIResults(classificationData!!)

                    // ✅ Después del análisis, pedir ubicación
                    requestLocationAfterAnalysis()

                    showControls()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ClassificationActivity", "❌ Error ${response.code()}: $errorBody")
                    Toast.makeText(
                        this@ClassificationActivity,
                        "Error al clasificar: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                    showControls()
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

    /**
     * Solicita la ubicación después de completar el análisis de IA
     */
    private fun requestLocationAfterAnalysis() {
        when {
            hasLocationPermissions() -> {
                Log.d("ClassificationActivity", "✅ Ya tenemos permisos de ubicación")
                getCurrentLocation()
            }
            else -> {
                Log.d("ClassificationActivity", "📍 Solicitando permisos de ubicación...")
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    /**
     * Verifica si tenemos permisos de ubicación
     */
    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun getCurrentLocation() {
        if (!hasLocationPermissions()) {
            Log.w("ClassificationActivity", "No hay permisos para obtener ubicación")
            return
        }

        try {
            showLoading(
                title = "Obteniendo ubicación",
                subtitle = "Esto ayudará a las autoridades a localizar el incidente..."
            )

            val cancellationToken = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener { location: Location? ->
                hideLoading()

                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    locationObtained = true

                    Log.d("ClassificationActivity", "Ubicación obtenida: $latitude, $longitude")
                    Toast.makeText(
                        this,
                        "Ubicación detectada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.w("ClassificationActivity", "⚠No se pudo obtener ubicación, usando default")
                    Toast.makeText(
                        this,
                        "No se pudo detectar tu ubicación, se usará una aproximada",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener { e ->
                hideLoading()
                Log.e("ClassificationActivity", "Error al obtener ubicación: ${e.message}", e)
                Toast.makeText(
                    this,
                    "Error al obtener ubicación. Se usará ubicación por defecto.",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: SecurityException) {
            hideLoading()
            Log.e("ClassificationActivity", "Error de seguridad: ${e.message}", e)
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
                Log.d("ClassificationActivity", "📝 Creando reporte en ubicación: $latitude, $longitude")

                val requestFile = imageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "imagen",
                    imageFile!!.name,
                    requestFile
                )

                val descripcionCompleta = buildString {
                    append(description)
                    append("\n\n[IA] ")
                    append(classificationData!!.descripcionIA)
                }

                val response = reporteService.crearReporteConClasificacion(
                    idUsu = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    detalleRepo = descripcionCompleta.toRequestBody("text/plain".toMediaTypeOrNull()),
                    latitud = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    longitud = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
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
        loadingHandler?.removeCallbacks(loadingRunnable ?: return)
    }
}