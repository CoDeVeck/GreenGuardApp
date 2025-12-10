package com.example.greenguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.MessagesAdapter
import com.example.greenguard.data.api.Chatbot
import com.example.greenguard.data.dataStore.UserPreferences
import com.example.greenguard.data.remote.RetrofitInstance
import com.example.greenguard.databinding.ActivityChatBinding
import com.example.greenguard.domain.model.dto.ChatbotResponse
import com.example.greenguard.domain.model.dto.Message
import com.example.greenguard.domain.model.dto.PromptRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private val messages = mutableListOf<Message>()

    private lateinit var chatbotApi: Chatbot
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)
        val retrofit = RetrofitInstance.createForAI(userPreferences)
        chatbotApi = retrofit.create(Chatbot::class.java)

        setupRecyclerView()
        setupListeners()

        addWelcomeMessage()
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(messages)
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messagesAdapter
        }
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener { sendMessage() }
        binding.btnBack.setOnClickListener { finish() }

        binding.chipSuggestion1.setOnClickListener {
            binding.etMessage.setText(binding.chipSuggestion1.text)
            sendMessage()
        }
        binding.chipSuggestion2.setOnClickListener {
            binding.etMessage.setText(binding.chipSuggestion2.text)
            sendMessage()
        }
        binding.chipSuggestion3.setOnClickListener {
            binding.etMessage.setText(binding.chipSuggestion3.text)
            sendMessage()
        }
    }

    private fun addWelcomeMessage() {
        val welcomeMessage = Message(
            text = "¡Hola! Soy tu Asistente Ambiental. ¿En qué puedo ayudarte hoy?",
            isFromUser = false,
            timestamp = getCurrentTime()
        )
        messages.add(welcomeMessage)
        messagesAdapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show()
            return
        }

        val userMessage = Message(
            text = messageText,
            isFromUser = true,
            timestamp = getCurrentTime()
        )
        messages.add(userMessage)
        messagesAdapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)

        binding.etMessage.text?.clear()

        showTypingIndicator()

        lifecycleScope.launch {
            try {
                val request = PromptRequest(prompt = messageText)

                val response: ChatbotResponse = chatbotApi.enviarPrompt(request)

                hideTypingIndicator()

                addBotResponse(response)

            } catch (e: Exception) {
                hideTypingIndicator()
                Toast.makeText(
                    this@ChatActivity,
                    "Error en la conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun addBotResponse(apiResponse: ChatbotResponse) {
        val botMessage = Message(
            text = apiResponse.choices[0].message.content,
            isFromUser = false,
            timestamp = getCurrentTime()
        )

        messages.add(botMessage)
        messagesAdapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun showTypingIndicator() {
        val typingMessage = Message(
            text = "",
            isFromUser = false,
            timestamp = "",
            isTyping = true
        )
        messages.add(typingMessage)
        messagesAdapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun hideTypingIndicator() {
        val index = messages.indexOfLast { it.isTyping }
        if (index != -1) {
            messages.removeAt(index)
            messagesAdapter.notifyItemRemoved(index)
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}