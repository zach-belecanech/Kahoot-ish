package com.bignerdranch.android.kahoot_ish

data class ChatGptResponse(
    val id: String,
    val model: String,
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)
