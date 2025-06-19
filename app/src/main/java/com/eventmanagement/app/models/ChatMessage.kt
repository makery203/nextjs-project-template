package com.eventmanagement.app.models

import com.google.firebase.Timestamp

data class ChatMessage(
    var id: String = "",
    var chatId: String = "",
    var senderId: String = "",
    var message: String = "",
    var timestamp: Timestamp? = null
)
