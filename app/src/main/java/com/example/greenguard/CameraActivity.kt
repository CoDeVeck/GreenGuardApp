package com.example.greenguard

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.greenguard.databinding.ActivityCameraBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var galleryLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        galleryLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    // Aquí puedes usar la URI, por ejemplo, abrir ClassificationActivity
                    val intent = Intent(this, ClassificationActivity::class.java)
                    intent.putExtra("imageUri", selectedImageUri.toString())
                    startActivity(intent)
                }
            }
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }

        binding.btnFlip.setOnClickListener {
            flipCamera()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Toast.makeText(this, "Error al iniciar la cámara", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        showFlashEffect()

        runOnUiThread {
            android.os.Handler(mainLooper).postDelayed({
                showLoading(true)
            }, 150)
        }

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GreenGuardian")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    showLoading(false)
                    Toast.makeText(
                        this@CameraActivity,
                        "Error al capturar la foto: ${exc.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri

                    val intent = Intent(this@CameraActivity, ClassificationActivity::class.java)
                    intent.putExtra("imageUri", savedUri.toString())
                    intent.putExtra("showLoading", true) // Indicar que viene con loading
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

                    android.os.Handler(mainLooper).postDelayed({
                        finish()
                    }, 300)
                }
            }
        )
    }

    private fun showFlashEffect() {
        binding.flashEffect.visibility = View.VISIBLE
        binding.flashEffect.alpha = 0f

        binding.flashEffect.animate()
            .alpha(0.9f)
            .setDuration(100)
            .withEndAction {
                binding.flashEffect.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction {
                        binding.flashEffect.visibility = View.GONE
                    }
                    .start()
            }
            .start()
    }

    private fun showLoading(show: Boolean) {
        binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toggleFlash() {
        flashMode = when (flashMode) {
            ImageCapture.FLASH_MODE_OFF -> {
                binding.btnFlash.setImageResource(R.drawable.ic_flash)
                ImageCapture.FLASH_MODE_ON
            }
            else -> {
                binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                ImageCapture.FLASH_MODE_OFF
            }
        }

        imageCapture?.flashMode = flashMode
    }

    private fun flipCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                showLoading(true)
                // Navegar a ClassificationActivity con la imagen de galería
                val intent = Intent(this, ClassificationActivity::class.java)
                intent.putExtra("imageUri", selectedImageUri.toString())
                intent.putExtra("showLoading", true)
                startActivity(intent)

                android.os.Handler(mainLooper).postDelayed({
                    finish()
                }, 300)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}