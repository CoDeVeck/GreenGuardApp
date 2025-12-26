package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.greenguard.data.api.UserAuth
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.data.repository.AuthRepository
import com.example.greenguard.databinding.ActivityPerfilBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var repository: AuthRepository
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRepository()
        setupViews()
        loadProfileData()
    }
    private fun setupRepository() {
        val userPreferences = UserPreferences(this)
        val userAuth = RetrofitInstance.create(userPreferences)
            .create(UserAuth::class.java)

        repository = AuthRepository(
            userAuth = userAuth,
            context = applicationContext
        )
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadProfileData() {
        lifecycleScope.launch {
            try {
                val perfil = repository.obtenerPerfilUsuario()
                val nombreUsuario = userPreferences.obtenerNombreUsuario()

                binding.apply {
                    tvReportsCount.text = perfil.totalReportes.toString()
                    tvPointsCount.text = perfil.totalPuntos.toString()
                    tvResolvedCount.text = perfil.totalReportesResueltos.toString()
                    tvCuponesCanjeados.text = perfil.cuponesCanjeado.toString()
                    tvUserName.text = nombreUsuario;
                    tvMemberSince.text = perfil.tiempoActivo.toString()

                    Glide.with(this@PerfilActivity)
                    .load(perfil.imagenUrl)
                    .placeholder(R.drawable.ic_user)
                    .into(ivAvatar)
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@PerfilActivity,
                    "Error al cargar perfil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Cerrar Sesión") { _, _ ->
                lifecycleScope.launch {
                    userPreferences.limpiarDatos()

                    Toast.makeText(
                        this@PerfilActivity,
                        "Sesión cerrada",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@PerfilActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}