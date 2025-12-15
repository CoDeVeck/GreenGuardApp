package com.example.greenguard

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.greenguard.data.api.UserAuth
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch
import java.io.File
import android.Manifest
import com.example.greenguard.data.repository.AuthRepository

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var repository: AuthRepository

    private var selectedImageUri: Uri? = null
    private var photoFile: File? = null

    // Map para convertir nombres de distrito a IDs
    private val distritoMap = mapOf(
        "Ancón" to 1,
        "Ate" to 2,
        "Barranco" to 3,
        "Breña" to 4,
        "Carabayllo" to 5,
        "Chaclacayo" to 6,
        "Chorrillos" to 7,
        "Cieneguilla" to 8,
        "Comas" to 9,
        "El Agustino" to 10,
        "Independencia" to 11,
        "Jesús María" to 12,
        "La Molina" to 13,
        "La Victoria" to 14,
        "Lince" to 15,
        "Los Olivos" to 16,
        "Lurigancho" to 17,
        "Lurín" to 18,
        "Magdalena del Mar" to 19,
        "Miraflores" to 20,
        "Pachacámac" to 21,
        "Pucusana" to 22,
        "Pueblo Libre" to 23,
        "Puente Piedra" to 24,
        "Punta Hermosa" to 25,
        "Punta Negra" to 26,
        "Rímac" to 27,
        "San Bartolo" to 28,
        "San Borja" to 29,
        "San Isidro" to 30,
        "San Juan de Lurigancho" to 31,
        "San Juan de Miraflores" to 32,
        "San Luis" to 33,
        "San Martín de Porres" to 34,
        "San Miguel" to 35,
        "Santa Anita" to 36,
        "Santa María del Mar" to 37,
        "Santa Rosa" to 38,
        "Santiago de Surco" to 39,
        "Surquillo" to 40,
        "Villa El Salvador" to 41,
        "Villa María del Triunfo" to 42
    )

    // Launcher para seleccionar imagen de galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            loadImageIntoView(it)
        }
    }

    // Launcher para tomar foto con la cámara
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoFile?.let { file ->
                selectedImageUri = Uri.fromFile(file)
                loadImageIntoView(selectedImageUri!!)
            }
        }
    }

    // Launcher para permisos de cámara
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRepository()
        setupUI()
        setupClickListeners()
    }

    private fun setupRepository() {
        val userPreferences = UserPreferences(this)
        val userAuth = RetrofitInstance.create(userPreferences).create(UserAuth::class.java)
        repository = AuthRepository(userAuth, applicationContext)
    }

    private fun setupUI() {
        // Configurar dropdown de género
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.actvGender.setAdapter(genderAdapter)

        // Configurar dropdown de distrito
        val districtOptions = resources.getStringArray(R.array.district_options)
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, districtOptions)
        binding.actvDistrict.setAdapter(districtAdapter)
    }

    private fun setupClickListeners() {
        // Click en el FAB para agregar foto
        binding.fabAddPhoto.setOnClickListener {
            showImageSourceDialog()
        }

        // Click en "Subir foto de perfil"
        binding.tvUploadPhoto.setOnClickListener {
            showImageSourceDialog()
        }

        // Click en la imagen de perfil
        binding.ivProfilePhoto.setOnClickListener {
            showImageSourceDialog()
        }

        // Click en el botón de registro
        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        // Click en "Inicia sesión"
        binding.tvLogin.setOnClickListener {
            finish() // Volver a la pantalla de login
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de galería", "Cancelar")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> pickImageLauncher.launch("image/*")
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {

        val photoFile = createImageFile()
        this.photoFile = photoFile

        val photoUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun loadImageIntoView(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivProfilePhoto)
    }

    private fun handleRegister() {
        // Obtener todos los valores de los campos
        val nombre = binding.etName.text.toString().trim()
        val apellidoPaterno = binding.etLastNamePaterno.text.toString().trim()
        val apellidoMaterno = binding.etLastNameMaterno.text.toString().trim()
        val documento = binding.etDni.text.toString().trim()
        val telefono = binding.etPhone.text.toString().trim()
        val genero = when (binding.actvGender.text.toString().trim()) {
            "Femenino" -> "F"
            "Masculino" -> "M"
            else -> ""
        }
        val distritoNombre = binding.actvDistrict.text.toString().trim()
        val correo = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmarPassword = binding.etConfirmPassword.text.toString().trim()
        val aceptoTerminos = binding.cbTerms.isChecked

        // Validar los datos
        if (!validateInputs(
                nombre, apellidoPaterno, apellidoMaterno, documento,
                telefono, genero, distritoNombre, correo, password,
                confirmarPassword, aceptoTerminos
            )) {
            return
        }

        // Obtener el ID del distrito
        val distritoId = distritoMap[distritoNombre] ?: 0

        // Realizar el registro
        registerUser(
            nombre, apellidoPaterno, apellidoMaterno, documento,
            telefono, genero, distritoId, correo, password
        )
    }

    private fun validateInputs(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        documento: String,
        telefono: String,
        genero: String,
        distrito: String,
        correo: String,
        password: String,
        confirmarPassword: String,
        aceptoTerminos: Boolean
    ): Boolean {
        return when {
            nombre.isBlank() -> {
                showError("Ingresa tus nombres")
                false
            }
            apellidoPaterno.isBlank() -> {
                showError("Ingresa tu apellido paterno")
                false
            }
            apellidoMaterno.isBlank() -> {
                showError("Ingresa tu apellido materno")
                false
            }
            documento.isBlank() -> {
                showError("Ingresa tu DNI")
                false
            }
            documento.length != 8 -> {
                showError("El DNI debe tener 8 dígitos")
                false
            }
            telefono.isBlank() -> {
                showError("Ingresa tu número de teléfono")
                false
            }
            genero.isBlank() -> {
                showError("Selecciona tu género")
                false
            }
            distrito.isBlank() -> {
                showError("Selecciona tu distrito")
                false
            }
            correo.isBlank() -> {
                showError("Ingresa tu correo electrónico")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                showError("Correo electrónico inválido")
                false
            }
            password.isBlank() -> {
                showError("Crea una contraseña")
                false
            }
            password.length < 6 -> {
                showError("La contraseña debe tener al menos 6 caracteres")
                false
            }
            confirmarPassword.isBlank() -> {
                showError("Confirma tu contraseña")
                false
            }
            password != confirmarPassword -> {
                showError("Las contraseñas no coinciden")
                false
            }
            !aceptoTerminos -> {
                showError("Debes aceptar los términos y condiciones")
                false
            }
            else -> true
        }
    }

    private fun registerUser(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        documento: String,
        telefono: String,
        genero: String,
        distritoId: Int,
        correo: String,
        password: String
    ) {
        showLoading(true)

        lifecycleScope.launch {
            val result = repository.registerUser(
                nombre = nombre,
                apellidoPaterno = apellidoPaterno,
                apellidoMaterno = apellidoMaterno,
                documento = documento,
                telefono = telefono,
                genero = genero,
                distritoId = distritoId,
                correo = correo,
                password = password,
                imagenUri = selectedImageUri
            )

            showLoading(false)

            result.fold(
                onSuccess = { response ->
                    if (response.valor) {
                        showSuccessDialog(response.mensaje)
                    } else {
                        showError(response.mensaje)
                    }
                },
                onFailure = { exception ->
                    showError("Error al registrar: ${exception.message}")
                }
            )
        }
    }

    private fun showLoading(show: Boolean) {
        binding.btnRegister.isEnabled = !show
        binding.btnRegister.text = if (show) "Registrando..." else getString(R.string.register_button_enter)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Registro exitoso")
            .setMessage(message)
            .setPositiveButton("Aceptar") { _, _ ->
                // Volver a la pantalla de login
                finish()
            }
            .setCancelable(false)
            .show()
    }
}