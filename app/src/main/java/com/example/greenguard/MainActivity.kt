package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.CategoryAdapter
import com.example.greenguard.adapter.RecentActivityAdapter
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.databinding.ActivityMainBinding
import com.example.greenguard.domain.model.dto.CategoryMain
import com.example.greenguard.domain.model.dto.RecentActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPreferences(applicationContext)

        loadUserData()
        setupRecentActivity()
        setupCategories()
        setupBottomNavigation()
        setupListeners()
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val nombre = userPrefs.obtenerNombreUsuario()
            //val puntos =  userPrefs.obtenerPuntosUsuario()  // si luego lo agregas
            val correo = userPrefs.obtenerCorreo()

            binding.tvUserName.text = nombre ?: "Usuario"
            //binding.tvTotalPoints.text = puntos?.toString() ?: "--"
            binding.tvReportsCount.text = "0"  // luego lo conectamos a tu backend
            binding.tvPeopleCount.text = "0"   // ejemplo si después cargas comunidad
        }
    }

    private fun setupRecentActivity() {
        val activities = listOf(
            RecentActivity("Basura desbordada", "25 Jul, 2024", "En Proceso", R.drawable.ic_launcher_background),
            RecentActivity("Farola rota", "24 Jul, 2024", "Resuelto", R.drawable.ic_launcher_background),
            RecentActivity("Graffiti en pared", "22 Jul, 2024", "Pendiente", R.drawable.ic_launcher_background)
        )

        binding.rvRecentActivity.layoutManager = LinearLayoutManager(this)
        binding.rvRecentActivity.adapter = RecentActivityAdapter(activities)
    }

    private fun setupCategories() {
        val categories = listOf(
            CategoryMain("Basura", "126 reportes", R.drawable.ic_trash),
            CategoryMain("Alumbrado", "89 reportes", R.drawable.ic_lightbulb),
            CategoryMain("Baches", "74 reportes", R.drawable.ic_road)
        )

        binding.rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = CategoryAdapter(categories)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_reports -> {
                    val intent = Intent(this, ReportHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_rewards -> {
                    val intent = Intent(this, CatalogoCuponesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.ivNotifications.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        binding.fabCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.btnRedeem.setOnClickListener {
            Toast.makeText(this, "Navegar a canjear puntos", Toast.LENGTH_SHORT).show()
        }
    }
}