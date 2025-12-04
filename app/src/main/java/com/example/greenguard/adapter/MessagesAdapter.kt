package com.example.greenguard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greenguard.ChatActivity
import com.example.greenguard.R
import com.example.greenguard.domain.model.dto.Message

class MessagesAdapter(
    private val messages: List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_BOT = 2
        const val VIEW_TYPE_TYPING = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isTyping -> VIEW_TYPE_TYPING
            messages[position].isFromUser -> VIEW_TYPE_USER
            else -> VIEW_TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_user, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_TYPING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_typing, parent, false)
                TypingViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_bot, parent, false)
                BotMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is BotMessageViewHolder -> holder.bind(message)
            is TypingViewHolder -> holder.startAnimation()
        }
    }

    override fun getItemCount() = messages.size

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            tvTime.text = message.timestamp
        }
    }

    class BotMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            tvTime.text = message.timestamp
        }
    }

    class TypingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun startAnimation() {
            // Aquí puedes agregar animación para los puntos
        }
    }
}