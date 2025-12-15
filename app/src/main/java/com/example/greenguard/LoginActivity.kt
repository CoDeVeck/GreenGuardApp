package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greenguard.data.api.UserAuth
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var authApi: UserAuth

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(applicationContext)

        if (intent.getBooleanExtra("token_expired", false)) {
            Toast.makeText(this, "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
        }

        CoroutineScope(Dispatchers.Main).launch {
            val tokenGuardado = userPreferences.obtenerToken()

            if (!tokenGuardado.isNullOrEmpty() && !isTokenExpired(tokenGuardado)) {
                startActivity(Intent(this@LoginActivity, OnBoardingActivity::class.java))
                finish()
                return@launch
            } else if (!tokenGuardado.isNullOrEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.",
                    Toast.LENGTH_LONG
                ).show()
                userPreferences.limpiarDatos()
            }
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = RetrofitInstance.create(userPreferences)
        authApi = retrofit.create(UserAuth::class.java)
        setupListeners()
    }

    // ⚠️ NUEVO: Función para verificar si el token está expirado
    private fun isTokenExpired(token: String): Boolean {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return true

            // Decodificar payload
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            val json = org.json.JSONObject(payload)
            val exp = json.getLong("exp")

            // Comparar con tiempo actual (en segundos)
            val now = System.currentTimeMillis() / 1000

            // Agregar margen de 5 minutos (300 segundos) para renovar antes
            return now >= (exp - 300)

        } catch (e: Exception) {
            e.printStackTrace()
            return true // Si hay error, considerar expirado
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val correo = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(correo, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(correo: String, password: String) {
        launch() {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.login(correo, password)
                }

                val token = response.token
                if (token.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Token vacío", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    userPreferences.guardarToken(token)
                }

                authApi = RetrofitInstance.create(userPreferences).create(UserAuth::class.java)

                val usuario = withContext(Dispatchers.IO) {
                    authApi.getUserInfo("Bearer $token")
                }

                withContext(Dispatchers.IO) {
                    userPreferences.guardarIdUsuario(usuario.idUsu ?: -1)
                    userPreferences.guardarNombreUsuario(
                        listOf(
                            usuario.nomUsu,
                            usuario.apePatUsu,
                            usuario.apeMatUsu
                        ).filter { !it.isNullOrBlank() }
                            .joinToString(" ")
                    )

                    userPreferences.guardarCorreo(usuario.correoUsu ?: "")
                    userPreferences.guardarTelefono(usuario.telefonoUsu ?: "")
                }

                startActivity(Intent(this@LoginActivity, OnBoardingActivity::class.java))
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
                    "Error en login: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}