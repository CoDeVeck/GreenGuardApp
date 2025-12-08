package com.example.greenguard.data.api

import com.example.greenguard.domain.model.dto.ChatbotResponse
import com.example.greenguard.domain.model.dto.PromptRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface Chatbot {

    @POST("chatbot/conversacion")
    suspend fun enviarPrompt(
        @Body prompt: PromptRequest
    ) : ChatbotResponse

}