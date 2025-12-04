package com.example.greenguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenguard.adapter.MessagesAdapter
import com.example.greenguard.databinding.ActivityChatBinding
import com.example.greenguard.domain.model.dto.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private val messages = mutableListOf<Message>()
    private var isConnected = true // Simular estado de conexión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        checkConnection()

        if (isConnected) {
            addWelcomeMessage()
        }
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(messages)
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messagesAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnMenu.setOnClickListener {
            Toast.makeText(this, "Menú", Toast.LENGTH_SHORT).show()
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.btnRetry.setOnClickListener {
            retryConnection()
        }

        // Quick suggestions
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

    private fun checkConnection() {
        // Simular verificación de conexión
        isConnected = isNetworkAvailable()

        if (isConnected) {
            binding.rvMessages.visibility = View.VISIBLE
            binding.noConnectionLayout.visibility = View.GONE
            binding.suggestionsScrollView.visibility = View.VISIBLE
        } else {
            binding.rvMessages.visibility = View.GONE
            binding.noConnectionLayout.visibility = View.VISIBLE
            binding.suggestionsScrollView.visibility = View.GONE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        // Aquí implementarías la verificación real de conexión
        // Por ahora retornamos true para pruebas
        return true
    }

    private fun retryConnection() {
        Toast.makeText(this, "Reconectando...", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            checkConnection()
            if (isConnected) {
                addWelcomeMessage()
            }
        }, 1000)
    }

    private fun addWelcomeMessage() {
        val welcomeMessage = Message(
            text = "¡Hola! Soy tu Asistente Ambiental. ¿Cómo puedo ayudarte a ser más sostenible hoy?",
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

        if (!isConnected) {
            Toast.makeText(this, "No hay conexión", Toast.LENGTH_SHORT).show()
            return
        }

        // Agregar mensaje del usuario
        val userMessage = Message(
            text = messageText,
            isFromUser = true,
            timestamp = getCurrentTime()
        )
        messages.add(userMessage)
        messagesAdapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)

        // Limpiar input
        binding.etMessage.text?.clear()

        // Mostrar indicador de escritura
        showTypingIndicator()

        // Simular respuesta del bot
        Handler(Looper.getMainLooper()).postDelayed({
            hideTypingIndicator()
            addBotResponse(messageText)
        }, 2000)
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
        val typingIndex = messages.indexOfLast { it.isTyping }
        if (typingIndex != -1) {
            messages.removeAt(typingIndex)
            messagesAdapter.notifyItemRemoved(typingIndex)
        }
    }

    private fun addBotResponse(userMessage: String) {
        val response = generateBotResponse(userMessage)
        val botMessage = Message(
            text = response,
            isFromUser = false,
            timestamp = getCurrentTime()
        )
        messages.add(botMessage)
        messagesAdapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun generateBotResponse(userMessage: String): String {
        // Aquí integrarías tu API de IA (ChatGPT, Gemini, etc.)
        // Por ahora respuestas simuladas
        return when {
            userMessage.contains("plástico", ignoreCase = true) -> {
                "Claro, una gran idea es empezar por llevar tus propias bolsas reutilizables al supermercado. También puedes optar por productos a granel para evitar empaques innecesarios."
            }
            userMessage.contains("reciclar", ignoreCase = true) -> {
                "Para reciclar correctamente, asegúrate de separar: papel y cartón, plástico, vidrio y metal. Limpia los envases antes de reciclarlos y verifica los símbolos de reciclaje."
            }
            userMessage.contains("residuos", ignoreCase = true) -> {
                "Para reducir residuos, te recomiendo: comprar solo lo necesario, preferir productos con empaques reciclables, compostar residuos orgánicos y reutilizar lo que puedas."
            }
            else -> {
                "Esa es una excelente pregunta. ¿Podrías darme más detalles para ayudarte mejor?"
            }
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }


}