package com.bignerdranch.android.kahoot_ish

data class ChatGptRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

data class Message(
    val role: String, // "user" or "assistant"
    val content: String
)
