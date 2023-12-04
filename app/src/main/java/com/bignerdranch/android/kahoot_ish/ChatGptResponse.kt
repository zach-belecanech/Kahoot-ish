package com.bignerdranch.android.kahoot_ish

data class ChatGptResponse(
    val choices: List<Map<String, String>> // assuming the API returns a list of choices
)