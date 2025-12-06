package com.example.greenguard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.greenguard.databinding.ActivityPerfilBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        loadProfileData()
        setupSettings()
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
        binding.apply {
            // Datos del usuario (ficticios)
            tvUserName.text = "María González"
            tvMemberSince.text = "Miembro desde Enero 2024"

            // Estadísticas principales
            tvReportsCount.text = "47"
            tvPointsCount.text = "2350"

            // Cargar avatar (usa Glide si tienes imágenes reales)
            // Glide.with(this@ProfileActivity)
            //     .load(userImageUrl)
            //     .placeholder(R.drawable.avatar_placeholder)
            //     .into(ivAvatar)
        }
    }

    private fun setupSettings() {
        // Aquí configurarías los settings dinámicamente si lo necesitas
        // Por ahora los includes del XML manejan la UI

        setupSettingsClicks()
    }

    private fun setupSettingsClicks() {
        // Encontrar y configurar los settings items
        val settingsCard = binding.cardSettings

        // Notificaciones Switch
        val notificationSwitch = settingsCard.findViewById<SwitchMaterial>(R.id.switchToggle)
        notificationSwitch?.setOnCheckedChangeListener { _, isChecked ->
            // Manejar cambio de notificaciones
            Toast.makeText(this, "Notificaciones: ${if(isChecked) "Activadas" else "Desactivadas"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Cerrar Sesión") { _, _ ->
                // Aquí cerrarías sesión y volverías al login
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}