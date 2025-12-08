package com.example.greenguard.domain.model.dto

data class ChatbotResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val message: Message
    ) {
        data class Message(
            val role: String,
            val content: String
        )
    }
}

