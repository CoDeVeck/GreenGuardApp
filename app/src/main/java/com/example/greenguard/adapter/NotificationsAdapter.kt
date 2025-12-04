package com.example.greenguard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenguard.databinding.ItemNotificationBinding // Importa la clase de binding
import com.example.greenguard.domain.model.dto.Notification
import com.example.greenguard.R

class NotificationsAdapter(
    private val notifications: List<Notification>,
    private val onItemClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.binding.apply {
            tvTitle.text = notification.title
            tvDescription.text = notification.description
            tvTime.text = notification.time

            unreadDot.visibility = if (notification.isUnread) View.VISIBLE else View.GONE

            ivIcon.setImageResource(R.drawable.ic_notifications_bell)
        }

        holder.itemView.setOnClickListener {
            onItemClick(notification)
        }
    }

    override fun getItemCount() = notifications.size
}