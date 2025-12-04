package com.example.greenguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.CategoryAdapter
import com.example.greenguard.adapter.RecentActivityAdapter
import com.example.greenguard.databinding.ActivityMainBinding
import com.example.greenguard.domain.model.dto.CategoryMain
import com.example.greenguard.domain.model.dto.RecentActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loadUserData()
        setupRecentActivity()
        setupCategories()
        setupBottomNavigation()
        setupListeners()
    }
    private fun loadUserData() {
        binding.tvUserName.text = "Alex Johnson"
        binding.tvTotalPoints.text = "1,250"
        binding.tvReportsCount.text = "15"
        binding.tvPeopleCount.text = "350"
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
                    Toast.makeText(this, "Reports", Toast.LENGTH_SHORT).show()
                    // Aquí iría la navegación real (e.g., Intent o Fragment Transaction)
                    true
                }

                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_rewards -> {
                    Toast.makeText(this, "Rewards", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
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