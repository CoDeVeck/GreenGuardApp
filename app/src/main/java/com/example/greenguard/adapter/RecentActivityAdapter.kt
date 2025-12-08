package com.example.greenguard.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenguard.MainActivity
import com.example.greenguard.databinding.ItemRecentActivityBinding
import com.example.greenguard.R
import com.example.greenguard.domain.model.dto.RecentActivity

class RecentActivityAdapter(
    private val activities: List<RecentActivity>
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    // 1. El ViewHolder ahora acepta la instancia del Binding como parámetro
    // y extiende RecyclerView.ViewHolder usando binding.root
    class ViewHolder(private val binding: ItemRecentActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Función para poblar los datos
        fun bind(activity: RecentActivity) {
            // Acceso directo a las vistas del item a través de 'binding.'
            binding.ivActivityIcon.setImageResource(activity.imageRes)
            binding.tvActivityTitle.text = activity.title
            binding.tvActivityDate.text = activity.date
            binding.tvActivityStatus.text = activity.status

            // Color según estado
            when (activity.status) {
                "En Proceso" -> {
                    // Usar binding para acceder a la vista de estado
                    binding.tvActivityStatus.setTextColor(Color.parseColor("#2196F3"))
                    binding.tvActivityStatus.setBackgroundResource(R.drawable.bg_status_in_progress)
                }
                "Resuelto" -> {
                    binding.tvActivityStatus.setTextColor(Color.parseColor("#4CAF50"))
                    binding.tvActivityStatus.setBackgroundResource(R.drawable.bg_status_resolved)
                }
                "Pendiente" -> {
                    binding.tvActivityStatus.setTextColor(Color.parseColor("#FF9800"))
                    binding.tvActivityStatus.setBackgroundResource(R.drawable.bg_status_pending)
                }
            }


            // El tag almacena el objeto activity para recuperarlo después
            binding.root.tag = activity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activities[position])
    }

    override fun getItemCount() = activities.size
}