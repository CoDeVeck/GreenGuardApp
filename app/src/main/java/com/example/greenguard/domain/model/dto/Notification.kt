package com.example.greenguard.domain.model.dto

data class Notification(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    var isUnread: Boolean,
    val type: NotificationType
)