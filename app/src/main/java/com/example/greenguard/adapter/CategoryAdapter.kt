package com.example.greenguard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenguard.MainActivity
import com.example.greenguard.databinding.ItemCategoryBinding
import com.example.greenguard.domain.model.dto.CategoryMain

class CategoryAdapter(
    private val categories: List<CategoryMain>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryMain) {
            binding.ivCategoryIcon.setImageResource(category.iconRes)
            binding.tvCategoryName.text = category.name
            binding.tvReportsCount.text = category.reportsCount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}