package com.example.greenguard.domain.model.dto

data class Message(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String,
    val isTyping: Boolean = false
)