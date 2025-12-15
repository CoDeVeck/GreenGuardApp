package com.example.greenguard.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greenguard.MainActivity
import com.example.greenguard.databinding.ItemRecentActivityBinding
import com.example.greenguard.R
import com.example.greenguard.domain.model.dto.RecentActivity

class RecentActivityAdapter(
    private val activities: List<RecentActivity>,
    private val onItemClick: ((RecentActivity) -> Unit)? = null
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemRecentActivityBinding,
        private val onItemClick: ((RecentActivity) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: RecentActivity) {

            Glide.with(binding.ivActivityIcon.context)
                .load(activity.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_close)
                .into(binding.ivActivityIcon)

            binding.tvActivityTitle.text = activity.title
            binding.tvActivityDate.text = activity.date
            binding.tvActivityStatus.text = activity.status

            when (activity.status) {
                "EP" -> {
                    binding.tvActivityStatus.setTextColor(Color.parseColor("#2196F3"))
                    binding.tvActivityStatus.setBackgroundResource(R.drawable.bg_status_in_progress)
                }
                "RE" -> {
                    binding.tvActivityStatus.setTextColor(Color.parseColor("#4CAF50"))
                    binding.tvActivityStatus.setBackgroundResource(R.drawable.bg_status_resolved)
                }
                "PE" -> {
                    binding.tvActivityStatus.setTextColor(Color.parseColor("#FF9800"))
                    binding.tvActivityStatus.setBackgroundResource(R.drawable.bg_status_pending)
                }
            }

            // ✅ CLICK AQUÍ
            binding.root.setOnClickListener {
                onItemClick?.invoke(activity) // 👈 solo si existe
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activities[position])
    }

    override fun getItemCount() = activities.size
}
